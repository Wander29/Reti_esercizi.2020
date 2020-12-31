package com;

import java.io.BufferedInputStream;
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

public class MainClientWT {
    public static void main(String args[]) {

        try{
            // RMI
            Registry r = LocateRegistry.getRegistry(ClientServerProtocol.RMI_SERVICE_PORT());
            ServerInterface serverStub = (ServerInterface) r.lookup(ClientServerProtocol.RMI_SERVICE_NAME());

            String username = "Wander";
            String psw = "Ludo1234";
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            int ret = serverStub.register(username, md.digest(psw.getBytes()));
            ClientServerErrorCodes.printError(ret);

            // TCP connection
            try( Socket cliSock = new Socket(); ) {
                cliSock.connect(new InetSocketAddress(ClientServerProtocol.SERVER_PORT()));

                if(ClientServerProtocol.DEBUG()) {
                    System.out.println("Connessione TCP instaurata");
                }

                try(DataOutputStream dos = new DataOutputStream(
                        new BufferedOutputStream(
                                cliSock.getOutputStream())))
                {
                    //StringBuilder builder = new StringBuilder(ClientServerOperations.LOGOUT.OP_CODE);
                    //String toSend = builder.toString();
                    dos.writeInt(ClientServerOperations.LOGOUT.OP_CODE);
                    //byte[] buf = "LOGOUT".getBytes();
                    //dos.write(buf, 0, buf.length);
                    dos.flush();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            // RMI callback

        }
        catch(RemoteException re)           { re.printStackTrace(); }
        catch (NoSuchAlgorithmException e)  { e.printStackTrace(); }
        catch (NotBoundException e)         { System.out.println("Servizio" +
                ClientServerProtocol.RMI_SERVICE_NAME() + " non presente"); }
    }
}
