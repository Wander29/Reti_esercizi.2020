package com;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ClientWT implements Runnable {

    ClientNotify clientStub;

    public ClientWT() { }

    public void run() {
        Registry r = null;
        ServerInterface serverStub = null;

        // RMI
        try {
            r = LocateRegistry.getRegistry(CSProtocol.RMI_SERVICE_PORT());
            serverStub = (ServerInterface) r.lookup(CSProtocol.RMI_SERVICE_NAME());

            this.register("Wander29", "Ludo1234", serverStub);
        }
        catch(RemoteException re)           { re.printStackTrace(); }
        catch (NotBoundException e)         { System.out.println("Servizio" +
                CSProtocol.RMI_SERVICE_NAME() + " non presente"); }

        // TCP connection
        try ( Socket cliSock = new Socket() )
        {
            cliSock.connect(new InetSocketAddress(CSProtocol.SERVER_PORT()));

            if(CSProtocol.DEBUG()) {
                System.out.println("[Client log] Connessione TCP instaurata");
            }
            try(BufferedReader bis = new
                    BufferedReader(new InputStreamReader( cliSock.getInputStream() )) ;

                BufferedWriter bos = new
                        BufferedWriter(new OutputStreamWriter( cliSock.getOutputStream() )))
            {
                // 1) LOGIN
                this.login("Wander29", "Ludo1234", bos);
                // reading server response to Login attempt
                System.out.println(bis.readLine());

                // 2) registerToRMICallback
                try {

                    clientStub = new ClientNotify();
                    // registration for callbacks AND getting users state info
                    Map<String, Boolean> usersStateUnmodifiable =
                            serverStub.registerForCallback(clientStub);

                    clientStub.setUsersMap(
                            new ConcurrentHashMap<>(usersStateUnmodifiable) );

                    System.out.println("-USERS ONLINE-");
                    for(String s : this.listUsers()) {
                        System.out.println(s);
                    }
                }
                catch(RemoteException re)   { re.printStackTrace(); }

                // 3) Create Project
                this.createProject("Wander29", "ProjectWander", bos);
                System.out.println(bis.readLine());

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

            // RMI callback
    }

    // RMI operations

    private void register(String username, String password, ServerInterface stub)
            throws RemoteException, NotBoundException {

        String ret = stub.register(username, password);
        System.out.println(ret);
    }

    private Set<String> listUsers() {
        return this.clientStub.getAllUsers();
    }

    private Set<String> listOnlineUsers() {
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
    private void login(String username, String password, BufferedWriter stream) throws IOException {
        StringBuilder sbuild = new StringBuilder(CSOperations.LOGIN.toString());
        sbuild.append(";");
        sbuild.append(username);
        sbuild.append(";");
        sbuild.append(password);

        stream.write(sbuild.toString());
        stream.flush();
    }

    /* TCP operation
        protocol for LOGOUT operation:
        -   LOGOUT;username

        possible reponses:
        LOGOUT_OK               if everything is ok

        USERNAME_NOT_PRESENT    if the given username is not registered
        USERNAME_NOT_ONLINE     if the user related to this username is not online
     */
    private void logout(String username, BufferedWriter stream, ServerInterface stub)
            throws IOException {
        stub.unregisterForCallback(this.clientStub);

        String req = CSOperations.LOGOUT.toString() + ';' + username;

        stream.write(req);
        stream.flush();
    }

    /* TCP operation
        -   CREATE_PROJECT;username;projectName

        possible responses:
        CREATE_PROJECT_OK;ip            if everything is ok
        PROJECT_ALREADY_PRESENT         if a project with the same name is already present in the server
        SERVER_INTERNAL_NETWORK_ERROR   if server can't use a Multicast Ip
 */
    private void createProject(String username, String projName, BufferedWriter stream) throws IOException {
        String req = CSOperations.CREATE_PROJECT.toString()
                + ";" + username + ";" + projName;

        stream.write(req);
        stream.flush();
    }
}

