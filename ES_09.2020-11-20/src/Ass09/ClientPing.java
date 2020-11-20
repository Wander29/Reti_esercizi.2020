package Ass09;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.util.Arrays;
import java.util.StringTokenizer;

/**
 * COMUNICAZIONE
 * il Client  utilizza una comunicazione UDP per comunicare con il server ed
 *  [#]     invia 10 messaggi al server, con il seguente formato:
 *                          PING seqno timestamp
 *             (in cui seqno è il numero di sequenza del PING (tra 0-9) ed il timestamp (in
 *             millisecondi) indica quando il messaggio è stato inviato)
 *
 *  [#]     non invia un nuovo PING fino che non ha ricevuto l'eco del PING precedente,
 *          oppure è scaduto un timeout.
 *
 *   #### OUTPUT
 *   [#]  Stampa ogni messaggio spedito al server ed il RTT del ping oppure un * se la
 *        risposta non è stata ricevuta entro 2 secondi
 *
 *   [#]  Dopo che ha ricevuto la decima risposta (o dopo il suo timeout), il client
 *        stampa un riassunto simile a quello stampato dal PING UNIX
 *        ---- PING Statistics ----
 *        10 packets transmitted, 7 packets received, 30% packet loss
 *        round-trip (ms) min/avg/max = 63/190.29/290
 *        • il RTT medio è stampato con 2 cifre dopo la virgola
 */

public class ClientPing extends Thread {
    class RTTStats {
        long min = Long.MAX_VALUE;
        long max = 0;

        int num_sent_packets = 0;
        int num_received_packets = 0;

        long sum;
        double avg = 0;
    }

    private final static int SERV_PORT = 9999;
    private final static int TIMEOUT_SOCKET = 2000;

    private final static int BUF_SIZE = 256;
    private final static int NUM_PINGS = 10;
    private final static String PING = "PING";


    public ClientPing() {

    }

    public void run() {
        try(DatagramSocket clientSock = new DatagramSocket()) {
            clientSock.setSoTimeout(TIMEOUT_SOCKET);
            // preparazione all'invio del PING e alle ricezioni
            byte[] sendBuffer = new byte[BUF_SIZE];
            DatagramPacket sendPacket;
            String sentData;
            long rtt = 0L;

            byte[] receiveBuffer = new byte[BUF_SIZE];
            DatagramPacket rcvdPacket;
            String byteToString = null;
            boolean received = false;

            RTTStats stat = new RTTStats();
            stat.num_sent_packets = 0;
            stat.num_received_packets = 0;

            for (int i = 0; i < NUM_PINGS; i++) {
                // elaborazione messaggio da inviare
                sentData = computeMsgoToSend(i);
                sendBuffer = sentData.getBytes();

                // invio messaggio
                sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, InetAddress.getLocalHost(), SERV_PORT);
                clientSock.send(sendPacket);
                stat.num_sent_packets++;

                // ricezione del datagramma
                Arrays.fill(receiveBuffer, (byte) 0);
                rcvdPacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                try {
                    clientSock.receive(rcvdPacket);

                    // se lo riceve prima del TIMEOUT
                    received = true;
                    stat.num_received_packets++;
                    byteToString = new String(
                            rcvdPacket.getData(),
                            0,
                            rcvdPacket.getLength(),
                            "US-ASCII");

                    // calcolo RTT
                    rtt = computeRTT(byteToString, stat);

                } catch (SocketTimeoutException e) {
                    // pacchetto non ricevuto entro il TIMEOUT => vado avanti
                    received = false;
                }

                // output
                String output = buildOutput(sentData, rtt, received);
                System.out.println(output);

                received = false;
            }

            /**
             * ---- PING Statistics ----
             * 10 packets transmitted, 7 packets received, 30% packet loss
             * round-trip (ms) min/avg/max = 63/190.29/290
             */
            printStats(stat);

        } catch (SocketException socketException) {
            socketException.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CorruptedUDPDataException e) {
            e.printStackTrace();
        }

        System.out.print("[CLIENT] bye!");
    }

    private void printStats(RTTStats stat) {
        System.out.println("\n            ---------- Ping Statistics ----------");
        double lossPercentage = 100 - (stat.num_received_packets * 100 / stat.num_sent_packets);
        stat.avg = (double) stat.sum / (double) stat.num_received_packets;

        System.out.printf("%d Packets transmitted, %d Packets received, %.2f%c Packet Loss\n",
                stat.num_sent_packets, stat.num_received_packets, lossPercentage, '%');

        System.out.printf("RTT(Round Trip Time) [ms] min/avg/max: %d/%.2f/%d\n\n",
                stat.min, stat.avg, stat.max);
    }

    private String computeMsgoToSend(int i) {
        StringBuilder sbuilder = new StringBuilder(PING);
        sbuilder.append(" ");
        sbuilder.append(i);
        sbuilder.append(" ");
        sbuilder.append(System.currentTimeMillis());

        return sbuilder.toString();
    }

    private String buildOutput(String data, long rtt, boolean received) {
        StringBuilder sbuilder = new StringBuilder(data);

        sbuilder.append(" RTT: ");
        if(received == true) {
            sbuilder.append(rtt);
            sbuilder.append(" ms");
        } else {
            sbuilder.append("*");
        }


        return sbuilder.toString();
    }

    private long computeRTT(String data, RTTStats stat) throws CorruptedUDPDataException {
        long end = System.currentTimeMillis();

        String timeStart = null;
        StringTokenizer st = new StringTokenizer(data);
        for (int i = 0; i < 3; i++) {
            if (!st.hasMoreTokens()) {
                throw new CorruptedUDPDataException();
            }
            timeStart = st.nextToken(); // 1:PING, 2:NUMBER, 3:TIME!
        }
        long start = Long.parseLong(timeStart);
        long rtt = end - start;

        if(stat.min > rtt) {
            stat.min = rtt;
        }
        else if(stat.max < rtt) {
            stat.max = rtt;
        }
        stat.sum += rtt;

        return rtt;
    }
}
