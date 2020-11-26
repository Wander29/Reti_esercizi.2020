package com;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;

public class MCServerWelcome extends Thread {
    private final String MC_ADDRESS;
    private final int PORT;

    private final int SLEEP_INTERVAL = 1500;
    private final String WELCOME_MSG = "Welcome all! Multicast test";

    private final int BUF_SIZE = WELCOME_MSG.length() + 1;

    public MCServerWelcome(String s, int p) {
        this.MC_ADDRESS = s;
        this.PORT = p;
    }

    public void run() {
        /* è il sender:
            invia ad intervalli regolari un msg di «welcome»
         */
        InetAddress mc_ia;
        DatagramPacket dp;

        // non è necessario un Multicast Socket per inviare un pacchetto ad un gruppo!
        try (DatagramSocket ms = new DatagramSocket(); ) {

            mc_ia = InetAddress.getByName(this.MC_ADDRESS);
            //  ms.joinGroup(mc_ia); anche se usassi un MulticastSocket NON serve per inviare!
            byte[] buf;

            while(true) {
                buf = WELCOME_MSG.getBytes();

                dp = new DatagramPacket(buf, buf.length, mc_ia, this.PORT);
                ms.send(dp);
                System.out.println("[SERVER] sent: " + WELCOME_MSG);

                try { Thread.sleep(SLEEP_INTERVAL); }
                catch (InterruptedException ie)
                { System.out.println("sleep interrotta, continue.."); }
            }

        }
        catch (BindException be)    { System.out.println("Porta già occupata"); }
        catch (IOException e)       { e.printStackTrace(); }
    }
}
