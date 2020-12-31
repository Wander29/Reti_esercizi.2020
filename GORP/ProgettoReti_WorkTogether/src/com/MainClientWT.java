package com;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainClientWT {
    public static void main(String args[]) {

        try{
            Registry r = LocateRegistry.getRegistry(ClientServerProtocol.RMI_SERVICE_PORT());
            ServerInterface serverStub = (ServerInterface) r.lookup(ClientServerProtocol.RMI_SERVICE_NAME());

            String username = "Wander";
            String psw = "Ludo1234";
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            int ret = serverStub.register(username, md.digest(psw.getBytes()));
            ClientServerErrorCodes.printError(ret);

            ret = serverStub.login(username, md.digest("gatto".getBytes()));
            ClientServerErrorCodes.printError(ret);

            ret = serverStub.login(username, md.digest(psw.getBytes()));
            ClientServerErrorCodes.printError(ret);

            ret = serverStub.logout(username);
            ClientServerErrorCodes.printError(ret);

        }
        catch(RemoteException re)           { re.printStackTrace(); }
        catch (NoSuchAlgorithmException e)  { e.printStackTrace(); }
        catch (NotBoundException e)         { System.out.println("Servizio" +
                ClientServerProtocol.RMI_SERVICE_NAME() + " non presente"); }
    }
}
