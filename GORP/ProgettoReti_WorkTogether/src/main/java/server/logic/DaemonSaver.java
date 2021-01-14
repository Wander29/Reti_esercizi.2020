package server.logic;

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
            try { Thread.sleep(updateInterval); }
            catch (InterruptedException e) {
                if(Thread.interrupted())
                    return;
            }

            try {
                System.out.println("[DAEMON] going to save");
                SerializeHelper.saveData(manager.getWorthData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
