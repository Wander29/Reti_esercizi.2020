package server.logic;

/**
* @author      LUDOVICO VENTURI (UniPi)
* @date        2021/01/14
* @versione    1.0
*/

import protocol.CSProtocol;
import server.logic.rmi.ServerManagerRmi;
import protocol.classes.TCPBuffersNIO;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.*;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class WelcomingServer implements Runnable {

    private ServerManagerWT     managerWT   = null;
    private ServerManagerRmi    managerRMI  = null;
    private SerializeHelper     saveHelp;

    private final static int MAX_CONNECTIONS_PER_THREAD = 2;

    public WelcomingServer() {
        try {
            this.managerRMI = ServerManagerRmi.getInstance();
        } catch (RemoteException e)     { e.printStackTrace(); }
        this.managerWT  = ServerManagerWT.getInstance();
        this.managerRMI.setManagerWT();
    }

    public void run() {
        ServerManagerRmi serverRMI = null;
        ThreadPoolExecutor tpe = null;

        // RMI
        try {
            // RMI service publication
            LocateRegistry.createRegistry(CSProtocol.RMI_SERVICE_PORT());
            Registry r = LocateRegistry.getRegistry(CSProtocol.RMI_SERVICE_PORT());
            r.bind(CSProtocol.RMI_SERVICE_NAME(), serverRMI.getStub());
        }
        catch (AlreadyBoundException abe)   { System.out.println("Nome serivzio RMI già in uso"); }
        catch (RemoteException re)          { re.printStackTrace(); }

        // TCP Connection socket
        try( ServerSocketChannel serverSockCh = ServerSocketChannel.open() ) {
            ServerSocket serverSock = serverSockCh.socket();

            serverSock.bind(new InetSocketAddress(CSProtocol.WORTH_TCP_PORT()));

            tpe = (ThreadPoolExecutor) Executors.newCachedThreadPool();
            Selector sel = null;
            int counterConnectionPerWorker = 0;

            while(true) {
                SocketChannel client = serverSockCh.accept(); // single connection with client
                client.configureBlocking(false);
                TCPBuffersNIO bufs = new TCPBuffersNIO();

                // @todo gestire casi limite, tipo disconnessioni, chiusure precoci..
                if(counterConnectionPerWorker == 0) {
                    sel = Selector.open(); // open new Selector (each MAX_CONNECTIONS_PER_THREAD socket opened)
                    client.register(sel, SelectionKey.OP_READ, bufs);

                    tpe.submit(new ServerWorker(sel));
                } else {
                    client.register(sel, SelectionKey.OP_READ, bufs);
                }

                if(++counterConnectionPerWorker == MAX_CONNECTIONS_PER_THREAD) {
                    counterConnectionPerWorker = 0;
                }
            }
        }
        catch (ClosedByInterruptException ie) {
            if(Thread.interrupted()) {
                System.out.println("[WELCOME SERVER] chiusura");
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }


        for(tpe.shutdown(); !tpe.isTerminated(); tpe.shutdownNow()) {
            try {
                tpe.awaitTermination(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("[WELCOME SERVER] closing");
    }

}
