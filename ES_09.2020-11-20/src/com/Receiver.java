package com;
import java.io.IOException; import java.net.*;
public class Receiver {
    public static void main(String args[])
    {
        try (DatagramSocket serverSock = new DatagramSocket(40000)){
            serverSock.setSoTimeout(20000);
            byte[] buffer = new byte[100];
            DatagramPacket receivedPacket = new DatagramPacket(buffer, buffer.length);
            while (true) {
                serverSock.receive(receivedPacket);
                String byteToString = new String(receivedPacket.getData(),
                        0, receivedPacket.getLength(), "US-ASCII");
                System.out.println("Length " + receivedPacket.getLength() +
                        " data " + byteToString);
            }
        }
        catch (SocketTimeoutException e) { System.out.println("Bye Bye");
        }
        catch (IOException e) { //e.printStackTrace();
            }
    }
}
