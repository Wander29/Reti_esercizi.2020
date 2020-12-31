package com;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
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
        try ( Socket cliSock = new Socket(); ) {
            cliSock.connect(new InetSocketAddress(ClientServerProtocol.SERVER_PORT()));

            if(ClientServerProtocol.DEBUG()) {
                System.out.println("Connessione TCP instaurata");
            }

            try(DataOutputStream dos = new DataOutputStream( new
                    BufferedOutputStream(cliSock.getOutputStream() )) )
            {
                // 1) LOGIN
                this.login("Wander29", "Ludo1234", dos);
                if(ClientServerProtocol.DEBUG()) {
                    System.out.println("login CORRETTO");
                }

                // 3)LOGOUT
                this.logout("Wander29", dos);
                if(ClientServerProtocol.DEBUG()) {
                    System.out.println("logout CORRETTO");
                }
            }

        } catch (IOException e) {
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

    public void register(String username, String password, ServerInterface stub)
            throws RemoteException, NotBoundException {

        int ret = stub.register(username, md.digest(password.getBytes()));
        ClientServerErrorCodes.printError(ret);
    }

    /*
    protocol for LOGIN operation:
        -   LOGIN;username;pswAsBytes
     */
    public void login(String username, String password, DataOutputStream stream) throws IOException {
        //stream.writeInt(ClientServerOperations.LOGIN.OP_CODE);
        StringBuilder sbuild = new StringBuilder(ClientServerOperations.LOGIN.toString());
        sbuild.append(";");
        sbuild.append(username);
        sbuild.append(";");

        byte[] tmp = sbuild.toString().getBytes();
        stream.write(tmp, 0, tmp.length);

        tmp = md.digest(password.getBytes());
        stream.write(tmp, 0, tmp.length);

        stream.flush();
    }

    /*
        protocol for LOGOUT operation:
        -   LOGOUT;username
     */
    public void logout(String username, DataOutputStream stream) throws IOException {
        String req = ClientServerOperations.LOGOUT.toString() + ';' + username;
        byte[] tmp = req.getBytes();

        stream.write(tmp, 0, tmp.length);
    }
}

