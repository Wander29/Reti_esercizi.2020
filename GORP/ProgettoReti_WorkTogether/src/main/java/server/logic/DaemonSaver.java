package server.logic;

/**
 * @author      LUDOVICO VENTURI (UniPi)
 * @date        2021/01/14
 * @versione    1.0
 */

import server.logic.SerializeHelper;
import server.logic.ServerManagerWT;

import java.io.IOException;

public class DaemonSaver extends Thread {

    private int updateInterval;
    private ServerManagerWT manager;

    public DaemonSaver(int interval) {
        updateInterval = interval;

        this.manager = ServerManagerWT.getInstance();
    }

    public void run() {
        // every "updateInterval" milliseconds
        while(true) {
            try {
                Thread.sleep(updateInterval);
            }
            catch (InterruptedException e) {

                System.out.println("[DAEMON] saving current state and exiting");
                SerializeHelper.saveData(manager.getWorthData());

                return;
            }
            if(Thread.interrupted()) {
                System.out.println("[DAEMON] saving current state and exiting");
                SerializeHelper.saveData(manager.getWorthData());
            }

            System.out.println("[DAEMON] going to save");
            SerializeHelper.saveData(manager.getWorthData());
        }
    }
}
