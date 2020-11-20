package PingPongUDP;

import java.io.IOException;
import java.net.*;
import java.nio.channels.DatagramChannel;

/**
 * L'esercizio consiste nella scrittura di un server che offre il servizio di "Ping Pong" e del
 * relativo programma client.
 *
 * Un client si connette al server ed invia un messaggio di "Ping".
 * Il server, se riceve il messaggio, risponde con un messaggio di "Pong".
 *
 * Il client sta in attesa n secondi di ricevere il messaggio dal server (timeout) e poi
 * termina.
 *
 * Client e Server usano il protocollo UDP per lo scambio di messaggi.
 */

public class ServerPong extends Thread {
    private static final String PONG = new String("Pong");
    private static final byte[] PONG_IN_BYTES = PONG.getBytes();
    private static final int    SERV_PORT = 60029;

    public ServerPong() {
    }

    public void run() {
        try (DatagramSocket serverSock = new DatagramSocket(SERV_PORT)) {

            // serverSock.setSoTimeout(20000);
            byte[] buffer = new byte[PONG.length() + 1];

            // ricezione pacchetto
            DatagramPacket receivedPacket = new DatagramPacket(buffer, buffer.length);
            serverSock.receive(receivedPacket);

            // elaborazione richiesta
            String byteToString = new String(receivedPacket.getData(),
                    0, receivedPacket.getLength(), "US-ASCII");
            System.out.println("[SERVER] Length " + receivedPacket.getLength() +
                    " data " + byteToString);

            // elaborazione risposta
            InetAddress client_IP = receivedPacket.getAddress();
            int client_port = receivedPacket.getPort();

            // Constructs a datagram packet for sending packets of length I
            DatagramPacket sendPacket = new DatagramPacket(PONG_IN_BYTES, PONG_IN_BYTES.length, client_IP, client_port);
            serverSock.send(sendPacket);
        }
        //catch (SocketTimeoutException e) { System.out.println("Bye Bye"); }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
