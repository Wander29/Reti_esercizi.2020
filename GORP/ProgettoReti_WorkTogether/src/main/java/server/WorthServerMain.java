/*
    NOTE on language I used:
    in every sentence relative to specific rows of code is intended to be read with the the initial
    sentence, ex: «[the code below] opens an unnamed pipe»
 */

package server;

/**
 * @author      LUDOVICO VENTURI (UniPi)
 * @date        2021/01/14
 * @versione    1.0
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import server.data.WorthData;
import server.logic.DaemonSaver;
import server.logic.SerializeHelper;
import server.logic.WelcomingServer;
import server.logic.ServerManagerWT;

public class WorthServerMain {
    private final static String EXIT_STRING = "exit";
    private final static int SAVE_INTERVAL = 30; // seconds

    public static void main(String args[]) {
        // start welcoming server for TCP and RMI
        Thread welcomeServer = new Thread(new WelcomingServer());
        welcomeServer.start();

        // daemon thread updating database
        DaemonSaver daemonSaver = new DaemonSaver(SAVE_INTERVAL * 1000);
        daemonSaver.setDaemon(true);
        daemonSaver.start();

        // waiting for closure: manual handling
        System.out.println("Enter \"" + EXIT_STRING + "\" to stop server");

        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        String lineRead;
        try {
            while( !(lineRead = input.readLine()).equals(EXIT_STRING) ) { }

            // first stop daemonSaver in order to save state of server
            daemonSaver.interrupt();
            daemonSaver.join();

            welcomeServer.interrupt();
            welcomeServer.join();
        }
        catch (IOException e)               { e.printStackTrace(); }
        catch (InterruptedException e)      { e.printStackTrace(); }

        System.out.println("[MAIN SERVER] closing");
    }

}
