package com;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
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

    private final static int BUF_SIZE = 2048;
    private final static int MAX_CONNECTIONS_PER_THREAD = 4;

    public static void main(String[] args) {
        // RMI
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

        // TCP Connection socket
        try( ServerSocketChannel serverSockCh = ServerSocketChannel.open() ) {
            ServerSocket serverSock = serverSockCh.socket();

            serverSock.bind(new InetSocketAddress(ClientServerProtocol.SERVER_PORT()));
            // @todo NTHREADS da definire costante
            ThreadPoolExecutor tpe = (ThreadPoolExecutor) Executors.newCachedThreadPool();
            Selector sel = null;
            int counterConnectionPerWorker = 0;

            while(true) {
                SocketChannel client = serverSockCh.accept(); // single connection with client
                client.configureBlocking(false);
                ByteBuffer buf = ByteBuffer.allocate(BUF_SIZE);

                if(counterConnectionPerWorker == 0) {
                    sel = Selector.open(); // open new Selector (each MAX_CONNECTIONS_PER_THREAD socket opened)
                    client.register(sel, SelectionKey.OP_READ, buf);

                    tpe.submit(new ServerWorker(sel));
                } else {
                    client.register(sel, SelectionKey.OP_READ, buf);
                }

                if(ClientServerProtocol.DEBUG()) {
                    System.out.println(tpe.getPoolSize());
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
