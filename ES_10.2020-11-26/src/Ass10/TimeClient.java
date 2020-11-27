package Ass10;

import java.io.IOException;
import java.net.*;

public class TimeClient extends Thread {
    private static final int BUF_SIZE       = 1024;
    private static final int NUM_ITERAZIONI = 10;

    private final int port;
    private final String group_name;

    public TimeClient(int port, String name) {
        this.port = port;
        this.group_name = name;
    }

    public void run() {
        // comunicazione
        InetSocketAddress socketIA;
        InetAddress ia;

        // ricezione
        DatagramPacket dp_received;
        byte[] buf = new byte[BUF_SIZE];

        // output
        String data_received;

        try (MulticastSocket ms = new MulticastSocket(this.port)) {
            // socketIA = new InetSocketAddress(this.port);
            // ms.joinGroup(socketIA, NetworkInterface.getByName(this.group_name));
            ia = InetAddress.getByName(this.group_name);
            if(!ia.isMulticastAddress()) {
                System.err.println("non un Multicast address");
            }
            ms.joinGroup(ia);

            for(int i = 1; i <= NUM_ITERAZIONI ; i++) {
                dp_received = new DatagramPacket(buf, buf.length);
                ms.receive(dp_received);

                data_received = new String(
                        dp_received.getData(),
                        dp_received.getOffset(),
                        dp_received.getLength());

                System.out.println("[CLIENT i=" + i + "] " + data_received);
            }

        }
        catch (BindException be)          { System.err.println("porta occupata --#Exiting#"); }
        catch (UnknownHostException ue)   { System.err.println("Indirizzo sconosciuto --#Exiting#"); }
        catch (IOException e)             { System.err.println("IOException --#Exiting#");
        e.printStackTrace();}
    }
}
