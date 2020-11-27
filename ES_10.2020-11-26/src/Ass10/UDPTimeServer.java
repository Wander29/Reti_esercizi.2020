package Ass10;

/*
    SENDER del gruppo multicast
    ha bisogno della porta di ascoto e del multicast IP
 */

import java.io.IOException;
import java.net.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.DateFormat;
import java.util.concurrent.ThreadLocalRandom;

public class UDPTimeServer extends Thread {
    private static final int BUF_SIZE = 1024;

    private final int port;
    private InetAddress multicast_address;

    public UDPTimeServer(int port, String name) throws UnknownHostException, IllegalArgumentException {
        this.port = port;
        this.multicast_address = InetAddress.getByName(name);

        if(!this.multicast_address.isMulticastAddress())
            throw new IllegalArgumentException();
    }

    public void run() {
        try (DatagramSocket ds = new DatagramSocket()) {
            // data e orario
            DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date dateObj;

            // buffer per inviare
            byte[] buf;
            DatagramPacket dp_to_send;

            // per output
            String to_send;

            while (true) {
                // prepara messaggio
                dateObj = new Date();
                to_send = df.format(dateObj);
                buf = to_send.getBytes();
                dp_to_send = new DatagramPacket(buf, buf.length, multicast_address, this.port);
                System.out.println("[SERVER] " + to_send);

                // lo invia
                ds.send(dp_to_send);

                // attende un tempo random
                try { Thread.sleep(ThreadLocalRandom.current().nextInt(300, 1200)); }
                catch (InterruptedException ie)   { System.err.println("Sleep interrotta --#Let's go#"); }
            }
        }
        catch (BindException be)          { System.err.println("porte occupate --#Exiting#"); }
        catch (UnknownHostException ue)   { System.err.println("Indirizzo sconosciuto --#Exiting#"); }
        catch (IOException e)             { System.err.println("IOException --#Exiting#"); }
    }
}