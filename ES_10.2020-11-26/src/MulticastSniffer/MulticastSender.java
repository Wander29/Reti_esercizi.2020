package MulticastSniffer;

import java.io.*;
import java.net.*;

public class MulticastSender {
    public static void main (String args[]) {
        try{
            InetAddress ia=InetAddress.getByName("228.5.6.7");
            byte[] data;

            data = "hello".getBytes();
            int port = 6789;
            DatagramPacket dp = new
                    DatagramPacket(data,data.length,ia,
                    port);
            DatagramSocket ms = new DatagramSocket(6800);
            ms.send(dp);
            Thread.sleep(80000);
        }
        catch(Exception ex){
            System.out.println(ex);}
    }
}

