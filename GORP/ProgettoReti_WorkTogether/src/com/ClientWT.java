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

public class ClientWT implements Runnable {

    MessageDigest md;

    public ClientWT() throws NoSuchAlgorithmException {
        md = MessageDigest.getInstance("SHA-256");
    }

    public void run() {
        Registry r;
        ServerInterface serverStub;

        // RMI
        try {
            r = LocateRegistry.getRegistry(ClientServerProtocol.RMI_SERVICE_PORT());
            serverStub = (ServerInterface) r.lookup(ClientServerProtocol.RMI_SERVICE_NAME());
            this.register("Wander29", "Ludo1234", serverStub);
        }
        catch(RemoteException re)           { re.printStackTrace(); }
        catch (NotBoundException e)         { System.out.println("Servizio" +
                ClientServerProtocol.RMI_SERVICE_NAME() + " non presente"); }

        // TCP connection
        try ( Socket cliSock = new Socket() )
        {
            cliSock.connect(new InetSocketAddress(ClientServerProtocol.SERVER_PORT()));

            if(ClientServerProtocol.DEBUG()) {
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

                //2) Create Project
                this.createProject("Wander29", "ProjectWander", bos);
                System.out.println(bis.readLine());

                // 3)LOGOUT
                this.logout("Wander29", bos);
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

    /* OPERATIONS
    . register (RMI)

    - LOGIN
    - LOGOUT
    - CREATE PROJECT
     */

    /* TCP operation
    -   CREATE_PROJECT;username;projectName

    possible responses:
    CREATE_PROJECT_OK           if everything is ok
    PROJECT_ALREADY_PRESENT     if a project with the same name is already present in the server
     */
    public void createProject(String username, String projName, BufferedWriter stream) throws IOException {
        String req = ClientServerOperations.CREATE_PROJECT.toString()
                + ";" + username + ";" + projName;

        stream.write(req);
        stream.flush();
    }

    /* RMI operation
     */
    public void register(String username, String password, ServerInterface stub)
            throws RemoteException, NotBoundException {

        int ret = stub.register(username, md.digest(password.getBytes()));
        ClientServerErrorCodes.printError(ret);
    }

    /* TCP operation
    protocol for LOGIN operation:
    -   LOGIN;username;psw

    possible responses:
    LOGIN_OK                if everything is ok

    USERNAME_NOT_PRESENT    if the username is not registered
    PSW_INCORRECT           if the password is incorrect for the given username
     */
    public void login(String username, String password, BufferedWriter stream) throws IOException {
        StringBuilder sbuild = new StringBuilder(ClientServerOperations.LOGIN.toString());
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
    public void logout(String username, BufferedWriter stream) throws IOException {
        String req = ClientServerOperations.LOGOUT.toString() + ';' + username;

        stream.write(req);
        stream.flush();
    }
}

