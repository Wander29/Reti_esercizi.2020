package client.model;

import client.data.ChatMsgObservable;
import client.data.DbHandler;
import client.model.rmi.ClientNotify;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import protocol.CSOperations;
import protocol.CSProtocol;
import protocol.CSReturnValues;
import protocol.ChatUtils;
import protocol.classes.CardStatus;
import protocol.classes.Project;
import protocol.exceptions.IllegalProtocolMessageException;
import protocol.classes.ChatMsg;
import protocol.classes.ListProjectEntry;
import server.logic.rmi.ServerInterface;
import protocol.exceptions.IllegalProjectException;
import protocol.exceptions.IllegalUsernameException;
import utils.StringUtils;

import java.io.*;
import java.lang.reflect.Type;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.Pipe;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ClientWT {

    // RMI
    private static ClientNotify clientStub;
    private static Registry r = null;
    private static ServerInterface serverStub = null;
    private static String user;
    // TCP
    private static ConnectionThread connThread = new ConnectionThread();

    /*
    every read of a volatile variable will be read
    from the computer's main memory,
    and not from the CPU cache,

    every write to a volatile variable will be written to main memory,
    and not just to the CPU cache.
     */
    protected volatile static ClientWT instance;
    /*
    PROTECTED => can't be created outside package
    uses a daemon thread to manage tcp connection with server
     */
    protected ClientWT() throws SQLException {
        connThread.setDaemon(true);
        try {
            startRMI();
        }
        catch(RemoteException re) {
            re.printStackTrace();
            System.exit(-1);
        }
        catch (NotBoundException e) {
            System.out.println("Servizio" +
                    CSProtocol.RMI_SERVICE_NAME() + " non presente");
            System.exit(-1);
        }
    }
    /*
    only one instance shared
     */
    public static synchronized ClientWT getInstance()
            throws RemoteException, NotBoundException, SQLException
    {
        if(instance == null) {
            instance = new ClientWT();
        }

        return instance;
    }

/*
RMI
 */
    private static void startRMI() throws RemoteException, NotBoundException {
        r = LocateRegistry.getRegistry(CSProtocol.RMI_SERVICE_PORT());
        serverStub = (ServerInterface) r.lookup(CSProtocol.RMI_SERVICE_NAME());
    }

    public static CSReturnValues register(String username, String password)
            throws RemoteException, InvalidKeySpecException, NoSuchAlgorithmException
    {
        String ret = serverStub.register(username, password);

        return CSReturnValues.valueOf(ret);
    }

    public static void registerForCallbacks() throws RemoteException {
        if(clientStub == null)
            clientStub = new ClientNotify();

        // registration for callbacks AND getting users state info
        Map<String, Boolean> usersStateUnmodifiable =
                serverStub.registerForCallback(clientStub);

        clientStub.setUsersMap(
                new ConcurrentHashMap<>(usersStateUnmodifiable) );
    }

    public static Map<String, Boolean> listUsers() {
        return clientStub.getAllUsers();
    }

    public static Set<String> listOnlineUsers() {
       return clientStub.getOnlineUsers();
    }

/*
UDP
 */
    private static Pipe pipe;
    private static Pipe.SinkChannel pipe_writeChannel;
    private static DbHandler   dbHandler   = null;
    private static ChatManager chatManager = null;

    private static ObservableList<ChatMsgObservable> chatCurrentMsgs = FXCollections.observableArrayList();
    public static ObservableList<ChatMsgObservable> getChatCurrentMsgs() {
        return chatCurrentMsgs;
    }

    public static void startChatManager() throws IOException, SQLException {

        // opens an unnamed pipe
        pipe = Pipe.open();
        pipe_writeChannel = pipe.sink();
        pipe_writeChannel.configureBlocking(false);

        // link to dbHandler
        dbHandler = new DbHandler(user);
        dbHandler.setObservableChatList(chatCurrentMsgs);
        dbHandler.createDB();

        // starts a daemon thread: chatManager
        chatManager = new ChatManager(pipe, user);
        chatManager.setDaemon(true);
        chatManager.start();
    }

    // used to send chat messages, it has to know ip and port
    private static Map<String, ListProjectEntry> projectsInfo = new HashMap<>();
    private ByteBuffer bbPipe = ByteBuffer.allocate(CSProtocol.BUF_SIZE_CLIENT());
    public void startChatConnection(ListProjectEntry project) throws IOException {

        bbPipe.clear();

        String sendToPipe = project.ip + ";" + project.port + ",";
        bbPipe.put(StringUtils.stringToBytes(sendToPipe));
        bbPipe.flip();

        // writes the data into a sink channel.
        while(bbPipe.hasRemaining()) {
            pipe_writeChannel.write(bbPipe);
        }

        // add to map info
        this.projectsInfo.put(project.project, project);
    }

    public static List<ChatMsg> readChat(String projectName) throws SQLException {
        return dbHandler.readChat(projectName);
    }

    /*
    protocol for SEND CHAT MSG operation
        -   CHAT_MSG;username;project;timeSent;msg

                (timeSent : LONG as String)
                (msg : max 2048 chars)
     */
    public static void sendChatMsg(String projectName, String text) throws IOException {
        ListProjectEntry project = projectsInfo.get(projectName);
        InetAddress multicastAddress = InetAddress.getByName(project.ip);

        ChatUtils.sendChatMsg(user, projectName, text, multicastAddress, project.port);
    }

    public static void removeProject(String projectName) {
        projectsInfo.remove(projectName);
    }
/*
TCP
 */
    /*
    protocol for LOGIN operation:
        -   LOGIN;username;psw
    */
    public static CSReturnValues login(String username, String password) throws IOException {
        StringBuilder sbuild = new StringBuilder(CSOperations.LOGIN.toString());
        sbuild.append(";");
        sbuild.append(username);
        sbuild.append(";");
        sbuild.append(password);

        String req = sbuild.toString();
        CSProtocol.printRequest(req);

        connThread.bOutput.write(req);
        connThread.bOutput.flush();

        String ret = connThread.bInput.readLine();
        CSProtocol.printResponse(ret);

        if(CSReturnValues.valueOf(ret).equals(CSReturnValues.LOGIN_OK)) {
            user =  username;
        }

        return CSReturnValues.valueOf(ret);
    }

    /* protocol for CREATE PROJECT operation
    -   CREATE_PROJECT;projectName

    possible responses:
    CREATE_PROJECT_OK            if everything is ok
    PROJECT_ALREADY_PRESENT         if a project with the same name is already present in the server
    SERVER_INTERNAL_NETWORK_ERROR   if server can't use a Multicast Ip
*/
    public static CSReturnValues createProject(String projName) throws IOException {
        String req = CSOperations.CREATE_PROJECT.toString() + ";" + projName;

        CSProtocol.printRequest(req);
        connThread.bOutput.write(req);
        connThread.bOutput.flush();

        String ret = connThread.bInput.readLine();
        CSProtocol.printResponse(ret);

        return CSReturnValues.valueOf(ret);
    }

    /*
    protocol for LIST PROJECTS operation:
        -   LIST_PROJECTS
     */
    public static List<ListProjectEntry> listProjects()
            throws IOException, IllegalUsernameException, IllegalProtocolMessageException
    {
        String req = CSOperations.LIST_PROJECTS.toString();

        CSProtocol.printRequest(req);
        connThread.bOutput.write(req);
        connThread.bOutput.flush();

        String ret = connThread.bInput.readLine();
        CSProtocol.printResponse(ret);

        StringTokenizer tokenizer = new StringTokenizer(ret, ";");
        String firstToken = tokenizer.nextToken();

        switch(CSReturnValues.valueOf(firstToken)) {

            case LIST_PROJECTS_OK:
                List<ListProjectEntry> list = new ArrayList<>();

                // deserialization
                Gson gson = new Gson();
                Type listType = new TypeToken<List<ListProjectEntry>>() {}.getType();
                list = gson.fromJson(tokenizer.nextToken(), listType);

                return list;

            case USERNAME_NOT_PRESENT:
                throw new IllegalUsernameException();
        }

        return null;
    }

    /*
    protocol for operation SHOW PROJECT
        - SHOW_PROJECT;projectName
     */
    public static Project showProject(String projectName)
            throws IOException, IllegalUsernameException, IllegalProjectException
    {
        String req = CSOperations.SHOW_PROJECT.toString() + ";" + projectName;

        CSProtocol.printRequest(req);
        connThread.bOutput.write(req);
        connThread.bOutput.flush();

        String ret = connThread.bInput.readLine();
        CSProtocol.printResponse(ret);

        StringTokenizer tokenizer = new StringTokenizer(ret, ";");
        String firstToken = tokenizer.nextToken();

        switch(CSReturnValues.valueOf(firstToken)) {

            case SHOW_PROJECT_OK:
                // deserialization
                Gson gson = new Gson();
                Type listType = new TypeToken<Project>() {}.getType();
                Project p = gson.fromJson(tokenizer.nextToken(), listType);

                return p;

            case USERNAME_NOT_PRESENT:
                throw new IllegalUsernameException();

            case PROJECT_NOT_PRESENT:
                throw new IllegalProjectException();

            default:
                break;
        }

        return null;
    }

    /*
    protocol for MOVE CARD operation
        -   MOVE_CARD;projectName;cardName;fromStatus;toStatus
     */
    public static CSReturnValues moveCard(
            String projectName, String cardName, CardStatus from, CardStatus to)
        throws IOException
    {
        String req = CSOperations.MOVE_CARD.toString() + ";" +
                projectName + ";" + cardName + ";" +
                from.toString() + ";" + to.toString();

        CSProtocol.printRequest(req);
        connThread.bOutput.write(req);
        connThread.bOutput.flush();

        String ret = connThread.bInput.readLine();
        CSProtocol.printResponse(ret);

        return CSReturnValues.valueOf(ret);
    }

    /*
    protocol for operation ADD CARD
        -   ADD_CARD;projectName;cardName;description
     */
    public static CSReturnValues addCard(String projectName, String cardName, String description)
            throws IOException
    {
        String req = CSOperations.ADD_CARD.toString() + ";" +
                        projectName + ";" + cardName + ";" + description;

        CSProtocol.printRequest(req);
        connThread.bOutput.write(req);
        connThread.bOutput.flush();

        String ret = connThread.bInput.readLine();
        CSProtocol.printResponse(ret);

        return CSReturnValues.valueOf(ret);
    }

    /*
    protocol for operation DELETE PROJECT
        -   DELETE_PROJECT;projectName
     */
    public static CSReturnValues deleteProject(String projectName) throws IOException {
        String req = CSOperations.DELETE_PROJECT.toString() + ";" + projectName;

        CSProtocol.printRequest(req);
        connThread.bOutput.write(req);
        connThread.bOutput.flush();

        String ret = connThread.bInput.readLine();
        CSProtocol.printResponse(ret);

        return CSReturnValues.valueOf(ret);
    }


    public static List<String> showMembers(String projectName)
            throws IOException, IllegalUsernameException, IllegalProjectException
    {
        String req = CSOperations.SHOW_MEMBERS.toString() + ";" + projectName;

        CSProtocol.printRequest(req);
        connThread.bOutput.write(req);
        connThread.bOutput.flush();

        String ret = connThread.bInput.readLine();
        CSProtocol.printResponse(ret);

        List<String> tokens = StringUtils.tokenizeRequest(ret);
        String firstToken = tokens.remove(0);

        switch(CSReturnValues.valueOf(firstToken)) {
            case SHOW_MEMBERS_OK:
                return tokens;

            case USERNAME_NOT_PRESENT:
                throw new IllegalUsernameException();

            case PROJECT_NOT_PRESENT:
                throw new IllegalProjectException();

            default:
                break;
        }

        return null;
    }

    /*
        protocol for ADD MEMBER operation
        -   ADD_MEMBER;projectName;newMember
     */
    public static CSReturnValues addMember(String projectName, String newMember) throws IOException {
        String req = CSOperations.ADD_MEMBER.toString() + ";" +
                projectName + ";" + newMember;

        CSProtocol.printRequest(req);
        connThread.bOutput.write(req);
        connThread.bOutput.flush();

        String ret = connThread.bInput.readLine();
        CSProtocol.printResponse(ret);

        return CSReturnValues.valueOf(ret);
    }

/*
******************************************** EXIT OPERATIONS
 */
    /*
    protocol for LOGOUT operation:
        -   LOGOUT;username
    */
    public static CSReturnValues logout() throws IOException {
        serverStub.unregisterForCallback(clientStub);

        String req = CSOperations.LOGOUT.toString();

        CSProtocol.printRequest(req);
        connThread.bOutput.write(req);
        connThread.bOutput.flush();

        String ret = connThread.bInput.readLine();
        CSProtocol.printResponse(ret);
        user = null;

        return CSReturnValues.valueOf(ret);
    }

    public static void exit() throws IOException {
        String req = CSOperations.EXIT.toString();

        CSProtocol.printRequest(req);
        connThread.bOutput.write(req);
        connThread.bOutput.flush();
    }

/*
    TCP connection handling
 */
    protected static String getServerIP() throws UnknownHostException {
        return InetAddress.getLocalHost().getHostName();
    }

    protected static int getServerPort() {
        return CSProtocol.WORTH_TCP_PORT();
    }

    public static void closeLogic() throws IOException, SQLException, InterruptedException {
        // tcp close
        if(connThread != null) {
            connThread.socket.close();
            connThread.interrupt();
            connThread.join();
        }

        // udp chat close
        if(dbHandler != null)
            dbHandler.closeConnection();

        if(chatManager != null){
            chatManager.interrupt();
            chatManager.join();
        }
    }

    public static void startConnection() throws IOException {
        connThread.start();
    }

    public static void closeConnection() throws IOException {
        connThread.socket.close();
    }

    /*
    private class used to establish a TCP connection in background
     */
    private static class ConnectionThread extends Thread {
        private Socket socket;
        private BufferedReader bInput;
        private BufferedWriter bOutput;

        public void run() {
            // TCP connection
            try (   Socket cliSock = new Socket(getServerIP(), getServerPort());

                    BufferedReader in = new BufferedReader(new
                            InputStreamReader( cliSock.getInputStream() )) ;
                    BufferedWriter out = new BufferedWriter(new
                            OutputStreamWriter( cliSock.getOutputStream() )))
            {
                this.socket = cliSock;
                this.bInput = in;
                this.bOutput = out;

                System.out.println("[CLIENT] connessione TCP instaurata");

                this.socket.setTcpNoDelay(true);
                /*
                    it wont'be waked up. It will sleep for the whole execution, client controllers
                    will use its streams
                 */
                synchronized (this) {
                    this.wait();
                }
            }
            catch(UnknownHostException ue)  { ue.printStackTrace(); }
            catch(IOException e)            { e.printStackTrace(); }
            catch(InterruptedException e) {
                return;
            }
        }
    }
}

