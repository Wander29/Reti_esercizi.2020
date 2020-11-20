package com;

import java.net.*;

public class Sender {
    public static void main (String args[]) {
        try (DatagramSocket clientSocket = new DatagramSocket()) {
            byte[] buffer="1234567890abcdefghijklmnopqrstuvwxyz".getBytes("US-ASCII");
            InetAddress address = InetAddress.getByName("127.0.0.1");
            for (int i = 1; i < buffer.length; i++) {
//Constructs a datagram packet for sending packets of length I
                DatagramPacket mypacket = new DatagramPacket(buffer,i,address,
                        40000);
                clientSocket.send(mypacket);
                Thread.sleep(200);
            }
        }
        catch (Exception e) { e.printStackTrace(); }
    }
}
