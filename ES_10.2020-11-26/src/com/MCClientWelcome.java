package com;

import java.io.IOException;
import java.net.*;

public class MCClientWelcome extends Thread {
    private final String MC_ADDRESS;
    private final int PORT;

    private final int BUF_SIZE = 512;

    public MCClientWelcome(String s, int p) {
        this.MC_ADDRESS = s;
        this.PORT = p;
    }

    public void run() {
        byte[] buffer = new byte[BUF_SIZE];
        InetAddress     mc_ia;
        DatagramPacket  dpReceived;
        String          dataReceived;

        try (MulticastSocket ms = new MulticastSocket(PORT)) {
            mc_ia = InetAddress.getByName(this.MC_ADDRESS);
            ms.joinGroup(mc_ia);
            ms.setSoTimeout(20000);

            dpReceived = new DatagramPacket(buffer, buffer.length);
            ms.receive(dpReceived);

            dataReceived = new String(dpReceived.getData(), dpReceived.getOffset(), dpReceived.getLength());
            System.out.println(dataReceived);

        }
        catch (SocketTimeoutException se)   { System.out.println("Timeout scaduto"); }
        catch (BindException be)            { System.out.println("Porta già occupata"); }
        catch (IOException e)               { e.printStackTrace(); }
    }
}
