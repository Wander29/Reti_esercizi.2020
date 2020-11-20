package PingPongUDP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.io.IOException;
import java.net.*;
import java.nio.channels.DatagramChannel;

public class ClientPing extends Thread {
    private static final String PING = new String("Ping");
    private static final byte[] PING_IN_BYTES = PING.getBytes();

    /**
     * Un client si connette al server ed invia un messaggio di "Ping".
     *
     * Il client sta in attesa n secondi di ricevere il messaggio dal server (timeout) e poi
     * termina.
     */

    private static final int    SERV_PORT = 60029;

    public ClientPing() {
    }

    public void run() {
        try (DatagramSocket clientSock = new DatagramSocket()){
            // timeout
            clientSock.setSoTimeout(5000);

            // invio richiesta
            InetAddress client_IP = InetAddress.getLocalHost();

            // Constructs a datagram packet for sending packets of length I
            DatagramPacket sendPacket = new DatagramPacket(PING_IN_BYTES, PING_IN_BYTES.length, client_IP, SERV_PORT);
            clientSock.send(sendPacket);

            // ricezione pacchetto
            byte[] buffer = new byte[PING.length() + 1];
            DatagramPacket receivedPacket = new DatagramPacket(buffer, buffer.length);
            clientSock.receive(receivedPacket);

            // elaborazione richiesta
            String byteToString = new String(receivedPacket.getData(),
                    0, receivedPacket.getLength(), "US-ASCII");
            System.out.println("[CLIENT] Length " + receivedPacket.getLength() +
                    " data " + byteToString);


        }
        catch (SocketTimeoutException e) {
            System.out.println("Client TIMEOUT");
        }
        catch (IOException e1) {
            e1.printStackTrace();
        }
    }
}
