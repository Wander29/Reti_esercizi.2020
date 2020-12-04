package Ass09;

/**
 * @author      LUDOVICO VENTURI 578033
 * @date        2020/11/21
 * @version     1.0
 */

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
    /**
     * classe wrapper per le statistiche
     */
    class RTTStats {
        long min = Long.MAX_VALUE;
        long max = -1;

        int num_sent_packets        = 0;
        int num_received_packets    = 0;

        long sum    = 0;
        double avg  = 0;
    }

    private final static int TIMEOUT_SOCKET = 2000;  // ms

    private final static int BUF_SIZE   = 1024;      // dimensione buffer invio & ricezione UDP
    private final static int NUM_PINGS  = 10;        // iterazioni di invio
    private final static String PING    = "PING";    // stringa da inviare

    private final InetAddress serverAddress;
    private final int serverPort;

    public ClientPing(InetAddress server, int servPort) {
        this.serverAddress  = server;
        this.serverPort     = servPort;
    }

    public void run() {

        byte[] sendBuffer;
        DatagramPacket sendPacket;
        String sentData;
        long rtt = 0L;

        try(DatagramSocket clientSock = new DatagramSocket()) {
            clientSock.setSoTimeout(TIMEOUT_SOCKET);
            // preparazione all'invio del PING e alle ricezioni
            sendBuffer = new byte[BUF_SIZE];


            byte[] receiveBuffer = new byte[BUF_SIZE];
            DatagramPacket rcvdPacket;
            String byteToString = null;
            boolean received = false;

            RTTStats stat = new RTTStats();
            stat.num_sent_packets = 0;
            stat.num_received_packets = 0;

            // invio 10 messaggi
            for (int i = 0; i < NUM_PINGS; i++) {
                // elaborazione messaggio da inviare
                sentData = computeMsgToSend(i);
                sendBuffer = sentData.getBytes();

                // invio messaggio
                sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, this.serverAddress, this.serverPort);
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

            // print PING Statistics, at the end
            printStats(stat);

        }
        catch (BindException e1) {
                System.err.printf("every port is in use!\n");
        }
        catch (SocketException | UnsupportedEncodingException | CorruptedUDPDataException e) {
            e.printStackTrace();}
        catch (IOException e) {
            e.printStackTrace();
        }

        System.out.print("[CLIENT] bye!");
    }

    /**
     * stampa le statistiche UNIX del PING, es:
     * * ---- PING Statistics ----
     * 10 packets transmitted, 7 packets received, 30% packet loss
     * round-trip (ms) min/avg/max = 63/190.29/290
     *
     * @param stat      wrapper per le statistiche
     */
    private void printStats(RTTStats stat) {
        System.out.println("\n            ---------- Ping Statistics ----------");
        double lossPercentage = 100 - (stat.num_received_packets * 100 / stat.num_sent_packets);
        stat.avg = (double) stat.sum / (double) stat.num_received_packets;

        System.out.printf("%d Packets transmitted, %d Packets received, %.2f%c Packet Loss\n",
                stat.num_sent_packets, stat.num_received_packets, lossPercentage, '%');

        System.out.printf("RTT(Round Trip Time) [ms] min/avg/max: %d/%.2f/%d\n\n",
                stat.min, stat.avg, stat.max);
    }

    /**
     * costruisce la stringa che verrà inviata nel datagramma come DATA
     * "PING i currentTimeMillis"
     * @param seq_num   numero di sequenza del messaggio PING
     * @return stringa da inviare come data
     */
    private String computeMsgToSend(int seq_num) {
        StringBuilder sbuilder = new StringBuilder(PING);
        sbuilder.append(" ");
        sbuilder.append(seq_num);
        sbuilder.append(" ");
        sbuilder.append(System.currentTimeMillis());

        return sbuilder.toString();
    }

    /**
     * costruisce la stringa di Output per poterla stampare, es:
     * PING 0 1605952731059 RTT: *
     * PING 1 1605952733065 RTT: 64 ms
     *
     * @param data      stringa del campo DATA del pacchetto ricevuto
     * @param rtt       valore rtt per quel messaggio -> usato sse received == true
     * @param received  indica se la risposta è stata ricevuta o no
     * @return stringa contenente l'output da stampare
     */
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

    /**
     * calcola RTT leggendo tempo dal Data del pacchetto
     * @param data      stringa del campo DATA del pacchetto ricevuto, ci si aspetta sia di questo formato:
     *
     * @param stat      wrapper delle statistiche
     * @return rtt (long) per quel messaggio
     * @throws CorruptedUDPDataException se non riesce a leggere 3 token
     */
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

        if(stat.min > rtt)          stat.min = rtt;
        else if(stat.max < rtt)     stat.max = rtt;

        stat.sum += rtt;

        return rtt;
    }
}
