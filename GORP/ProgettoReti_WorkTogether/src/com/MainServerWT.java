package com;

import java.io.IOException;
import java.net.ServerSocket;
import java.nio.channels.ServerSocketChannel;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class MainServerWT {

    public static void main(String[] args) {
        try {
            // service start
            ServerManagerWT server = new ServerManagerWT();
            ServerManagerRMI serverRMI = new ServerManagerRMI(server); // è gia uno stub

            // Publication RMI service
            LocateRegistry.createRegistry(ClientServerProtocol.RMI_SERVICE_PORT());
            Registry r = LocateRegistry.getRegistry(ClientServerProtocol.RMI_SERVICE_PORT());
            r.bind(ClientServerProtocol.RMI_SERVICE_NAME(), serverRMI);

        }
        catch (AlreadyBoundException abe)   { System.out.println("Nome serivzio RMI già in uso"); }
        catch (RemoteException re)          { re.printStackTrace(); }

        try(ServerSocket serverSock = new ServerSocket() ) {

        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

}
