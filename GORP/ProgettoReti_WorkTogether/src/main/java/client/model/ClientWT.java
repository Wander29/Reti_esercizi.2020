package client.model;

import client.model.rmi.ClientNotify;
import protocol.CSOperations;
import protocol.CSProtocol;
import protocol.CSReturnValues;
import server.logic.rmi.ServerInterface;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ClientWT implements Runnable {

    // RMI
    private ClientNotify clientStub;
    private Registry r = null;
    private ServerInterface serverStub = null;
    // TCP
    private ConnectionThread connThread = new ConnectionThread();

    /*
    uses a daemon thread to manage tcp connection with server
     */
    public ClientWT() throws RemoteException, NotBoundException {
        this.connThread.setDaemon(true);
        this.startRMI();
    }

    private void startRMI() throws RemoteException, NotBoundException {
        r = LocateRegistry.getRegistry(CSProtocol.RMI_SERVICE_PORT());
        serverStub = (ServerInterface) r.lookup(CSProtocol.RMI_SERVICE_NAME());
    }

    public void run() {
        // TCP connection
        /*

                // 3) Create Project
                this.createProject("Wander29", "ProjectWander", bos);
                System.out.println(bis.readLine());

                Thread.sleep(10000);


                // LAST) LOGOUT
                this.logout("Wander29", bos, serverStub);
                System.out.println(bis.readLine());


            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        */

    }

    private List<String> tokenizeRequest(String s) {
        ArrayList<String> tokens = new ArrayList<>();
        StringTokenizer tokenizer = new StringTokenizer(s, ";");

        while(tokenizer.hasMoreTokens()) {
            tokens.add(tokenizer.nextToken());
        }

        return tokens;
    }

    private String getFirstToken(String s) {
        StringTokenizer tokenizer = new StringTokenizer(s, ";");

        if(tokenizer.hasMoreTokens())
            return tokenizer.nextToken();

        return null;
    }

    // RMI operations
    public CSReturnValues register(String username, String password) throws RemoteException {
        String ret = serverStub.register(username, password);

        return CSReturnValues.valueOf(ret);
    }

    public Set<String> listUsers() {
        return this.clientStub.getAllUsers();
    }

    public Set<String> listOnlineUsers() {
       return this.clientStub.getOnlineUsers();
    }

    /* TCP operation
    protocol for LOGIN operation:
    -   LOGIN;username;psw

    possible responses:
    LOGIN_OK                if everything is ok

    USERNAME_NOT_PRESENT    if the username is not registered
    PSW_INCORRECT           if the password is incorrect for the given username
    ALREADY_LOGGED_IN
     */
    public CSReturnValues login(String username, String password) throws IOException {
        // ask server to login
        StringBuilder sbuild = new StringBuilder(CSOperations.LOGIN.toString());
        sbuild.append(";");
        sbuild.append(username);
        sbuild.append(";");
        sbuild.append(password);

        connThread.bOutput.write(sbuild.toString());
        connThread.bOutput.flush();

        String ret = connThread.bInput.readLine();

        return CSReturnValues.valueOf(ret);
    }

    public void registerForCallbacks() throws RemoteException {
        clientStub = new ClientNotify();
        // registration for callbacks AND getting users state info
        Map<String, Boolean> usersStateUnmodifiable =
                this.serverStub.registerForCallback(clientStub);

        clientStub.setUsersMap(
                new ConcurrentHashMap<>(usersStateUnmodifiable) );
    }

    /* TCP operation
        protocol for LOGOUT operation:
        -   LOGOUT;username

        possible reponses:
        LOGOUT_OK               if everything is ok

        USERNAME_NOT_PRESENT    if the given username is not registered
        USERNAME_NOT_ONLINE     if the user related to this username is not online
     */
    public void logout(String username)
            throws IOException {
        this.serverStub.unregisterForCallback(this.clientStub);

        String req = CSOperations.LOGOUT.toString() + ';' + username;

        connThread.bOutput.write(req);
        connThread.bOutput.flush();
    }

    public void exit() throws IOException {
        connThread.bOutput.write("EXIT");
        connThread.bOutput.flush();
    }

    /* TCP operation
        -   CREATE_PROJECT;username;projectName

        possible responses:
        CREATE_PROJECT_OK;ip            if everything is ok
        PROJECT_ALREADY_PRESENT         if a project with the same name is already present in the server
        SERVER_INTERNAL_NETWORK_ERROR   if server can't use a Multicast Ip
 */
    public void createProject(String username, String projName, BufferedWriter stream) throws IOException {
        String req = CSOperations.CREATE_PROJECT.toString()
                + ";" + username + ";" + projName;

        stream.write(req);
        stream.flush();
    }

    protected String getServerIP() throws UnknownHostException {
        return InetAddress.getLocalHost().getHostName();
    }

    protected int getServerPort() {
        return CSProtocol.SERVER_PORT();
    }

    public void startConnection() throws Exception {
        connThread.start();
    }

    public void closeConnection() throws IOException {
        connThread.socket.close();
    }

    private class ConnectionThread extends Thread {
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

