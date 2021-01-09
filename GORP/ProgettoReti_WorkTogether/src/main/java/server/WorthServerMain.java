/*
    NOTE on language I used:
    every sentence relative to specific rows of code is intended to be read with the the initial
    sentence, ex: «[the code below] opens an unnamed pipe»
 */

package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import server.data.WorthData;
import server.logic.SerializeHelper;
import server.logic.WelcomingServer;
import server.logic.ServerManagerWT;

public class WorthServerMain {
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
        //daemonSaver.start();

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
