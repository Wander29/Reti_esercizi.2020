package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

import protocol.CSProtocol;
import server.data.WorthData;
import server.logic.SerializeHelper;
import server.logic.WelcomingServer;
import server.logic.rmi.ServerManagerRMI;
import server.logic.ServerManagerWT;
import server.logic.ServerWorker;
import utils.TCPBuffersNIO;

public class WorthServer {
    private final static String EXIT_STRING = "exit";
    private final static int UPDATE_INTERVAL = 30; // seconds

    public static void main(String args[]) {
        WorthData data = null;
        try {
            data = SerializeHelper.recoverData();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ServerManagerWT server = new ServerManagerWT(data);
        Thread serverThread = new Thread( new WelcomingServer(server) );
        serverThread.start();

        // daemon thread updating database
        DaemonSaver daemonSaver = new DaemonSaver(UPDATE_INTERVAL * 1000, server);
        daemonSaver.start();

        // waiting for closure: manual handling
        System.out.println("Enter \"" + EXIT_STRING + "\" to stop server");

        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        String lineRead;
        try {
            while( !(lineRead = input.readLine()).equals(EXIT_STRING) ) { }

            daemonSaver.interrupt();
            SerializeHelper.saveData(server.getWorthData());
        } catch (IOException e) { e.printStackTrace(); }

        System.out.println("[MAIN SERVER] bye");
        System.exit(0);
    }

}
