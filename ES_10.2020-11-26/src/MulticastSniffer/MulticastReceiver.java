package MulticastSniffer;

import java.net.*; import java.io.*;
import java.rmi.server.ExportException;

public class MulticastReceiver {
    public static void main(String[] args) throws IOException {
        InetAddress group = null;
        int port = 0;
        try {
            group = InetAddress.getByName(args[0]);
            port = Integer.parseInt(args[1]);
        } catch (Exception e) {
            System.out.println("Uso:java multicastsniffer multicast_address port");
        }
        MulticastSocket ms = null;
        try {
            ms = new MulticastSocket(port);
            ms.joinGroup(group);
            byte[] buffer = new byte[8192];
            while (true) {
                try {
                    DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
                    ms.receive(dp);
                    String s = new String(dp.getData());
                    System.out.println(s);
                } catch (IOException ex) {
                    System.out.println(ex);
                } finally {
                    if (ms != null) {
                        try {
                            ms.leaveGroup(group);
                            ms.close();
                        } catch (IOException ex) {
                        }
                    }
                }
            }
        } catch(Exception a) {}
    }
}
