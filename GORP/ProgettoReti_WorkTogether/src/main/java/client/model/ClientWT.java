package client.model;

import client.model.rmi.ClientNotify;
import protocol.CSOperations;
import protocol.CSProtocol;
import protocol.CSReturnValues;
import server.logic.rmi.ServerInterface;
import utils.exceptions.IllegalProjectException;
import utils.exceptions.IllegalUsernameException;
import utils.StringUtils;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
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
    protected volatile static ClientWT instance = new ClientWT();
    /*
    PROTECTED => can't be created outside package
    uses a daemon thread to manage tcp connection with server
     */
    protected ClientWT(){
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
    public static synchronized ClientWT getInstance() throws RemoteException, NotBoundException {
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

    public static CSReturnValues register(String username, String password) throws RemoteException {
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

    public static Set<String> listUsers() {
        return clientStub.getAllUsers();
    }

    public static Set<String> listOnlineUsers() {
       return clientStub.getOnlineUsers();
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

        if(CSProtocol.DEBUG()) {
            System.out.println("trying LOGIN for " + username);
        }

        connThread.bOutput.write(sbuild.toString());
        connThread.bOutput.flush();

        String ret = connThread.bInput.readLine();

        if(CSProtocol.DEBUG()) {
            System.out.println("response: " + ret);
        }
        if(CSReturnValues.valueOf(ret).equals(CSReturnValues.LOGIN_OK)) {
            user =  username;
        }

        return CSReturnValues.valueOf(ret);
    }

    /* protocol for CREATE PROJECT operation
    -   CREATE_PROJECT;projectName

    possible responses:
    CREATE_PROJECT_OK;ip            if everything is ok
    PROJECT_ALREADY_PRESENT         if a project with the same name is already present in the server
    SERVER_INTERNAL_NETWORK_ERROR   if server can't use a Multicast Ip
*/
    public static CSReturnValues createProject(String projName) throws IOException {
        String req = CSOperations.CREATE_PROJECT.toString() + ";" + projName;

        connThread.bOutput.write(req);
        connThread.bOutput.flush();

        if(CSProtocol.DEBUG()) {
            System.out.println("req: CREATE PROJECT for " +  user + ": " + projName);
        }

        String ret = connThread.bInput.readLine();

        // @todo CHAT: retrieve multicast IP for chat
        String firstToken = StringUtils.tokenizeRequest(ret).get(0);
        if(CSReturnValues.CREATE_PROJECT_OK.equals(CSReturnValues.valueOf(firstToken))) {
            if(CSProtocol.DEBUG()) {
                System.out.println("response: " + ret);
            }
        }

        return CSReturnValues.valueOf(firstToken);
    }

    /*
    protocol for LIST PROJECTS operation:
        -   LIST_PROJECTS
     */
    public static List<String> listProjects() throws IOException, IllegalUsernameException {
        String req = CSOperations.LIST_PROJECTS.toString();

        if(CSProtocol.DEBUG()) {
            System.out.println("req: LIST PROJECT for " +  user);
        }
        connThread.bOutput.write(req);
        connThread.bOutput.flush();

        String ret = connThread.bInput.readLine();

        List<String> tokens = StringUtils.tokenizeRequest(ret);
        String firstToken = tokens.remove(0);
        switch(CSReturnValues.valueOf(firstToken)) {
            case LIST_PROJECTS_OK:
                return tokens;

            case USERNAME_NOT_PRESENT:
                throw new IllegalUsernameException();
        }

        return null;
    }

    public static List<String> showMembers(String projectName) throws IOException, IllegalUsernameException, IllegalProjectException {
        String req = CSOperations.SHOW_MEMBERS.toString() + ";" + projectName;

        if(CSProtocol.DEBUG()) {
            System.out.println("req: SHOW MEMBERS for " +  user + " proj: " + projectName);
        }
        connThread.bOutput.write(req);
        connThread.bOutput.flush();

        String ret = connThread.bInput.readLine();

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
                System.out.println(firstToken);
        }

        return null;
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

        if(CSProtocol.DEBUG()) {
            System.out.println("req: LOGOUT for " + user);
        }

        connThread.bOutput.write(req);
        connThread.bOutput.flush();

        String ret = connThread.bInput.readLine();

        return CSReturnValues.valueOf(ret);
    }

    public static void exit() throws IOException {
        connThread.bOutput.write("EXIT");
        connThread.bOutput.flush();
    }

/*
    TCP connection handling
 */
    protected static String getServerIP() throws UnknownHostException {
        return InetAddress.getLocalHost().getHostName();
    }

    protected static int getServerPort() {
        return CSProtocol.SERVER_PORT();
    }

    public static void startConnection() throws Exception {
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
            catch(UnknownHostException ue) { ue.printStackTrace(); }
            catch(IOException e) { e.printStackTrace(); }
            catch(InterruptedException e) { e.printStackTrace(); }
        }
    }
}

