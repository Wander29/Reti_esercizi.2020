package com;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class MainServerWT {

    private final static int MAX_CONNECTIONS_PER_THREAD = 4;

    public static void main(String[] args) {
        ServerManagerWT server = null;
        ServerManagerRMI serverRMI = null;

        // RMI
        try {
            // service start
            server = new ServerManagerWT();
            serverRMI = new ServerManagerRMI(server); // è gia uno stub

            // Publication RMI service
            LocateRegistry.createRegistry(CSProtocol.RMI_SERVICE_PORT());
            Registry r = LocateRegistry.getRegistry(CSProtocol.RMI_SERVICE_PORT());
            r.bind(CSProtocol.RMI_SERVICE_NAME(), serverRMI);

        }
        catch (AlreadyBoundException abe)   { System.out.println("Nome serivzio RMI già in uso"); }
        catch (RemoteException re)          { re.printStackTrace(); }

        // TCP Connection socket
        try( ServerSocketChannel serverSockCh = ServerSocketChannel.open() ) {
            ServerSocket serverSock = serverSockCh.socket();

            serverSock.bind(new InetSocketAddress(CSProtocol.SERVER_PORT()));
            // @todo NTHREADS da definire costante
            ThreadPoolExecutor tpe = (ThreadPoolExecutor) Executors.newCachedThreadPool();
            Selector sel = null;
            int counterConnectionPerWorker = 0;

            while(true) {
                SocketChannel client = serverSockCh.accept(); // single connection with client
                client.configureBlocking(false);
                TCPBuffersNIO bufs = new TCPBuffersNIO();

                if(counterConnectionPerWorker == 0) {
                    sel = Selector.open(); // open new Selector (each MAX_CONNECTIONS_PER_THREAD socket opened)
                    client.register(sel, SelectionKey.OP_READ, bufs);

                    tpe.submit(new ServerWorker(sel, server));
                } else {
                    client.register(sel, SelectionKey.OP_READ, bufs);
                }

                if(++counterConnectionPerWorker == MAX_CONNECTIONS_PER_THREAD) {
                    counterConnectionPerWorker = 0;
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

}
