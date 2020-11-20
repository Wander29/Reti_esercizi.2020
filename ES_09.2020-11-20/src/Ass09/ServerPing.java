package Ass09;

/*
 * il server deve introdurre un ritardo artificiale ed
 * ignorare alcune richieste per simulare la perdita di pacchetti
 *
 *  rimanda al mittente qualsiasi dato riceve
 *
 * ELABORAZIONE
 *  [#] dopo aver ricevuto un PING, il server determina se ignorare il pacchetto
 *      (simulandone la perdita) o effettuarne l'eco. La probabilità di perdita di
 *      pacchetti di default è del 25%.
 * [#] se decide di effettuare l'eco del PING, il server attende un intervallo di tempo
 *      casuale per simulare la latenza di rete
 *
 *
 * COMUNICAZIONE
 *
 * OUTPUT
 * [#]  stampa l'indirizzo IP e la porta del client, il messaggio di PING e l'azione
 *      intrapresa dal server in seguito alla sua ricezione (PING non inviato,oppure
 *      PING ritardato di x ms).
 */

import javax.xml.crypto.Data;
import java.io.IOException;
import java.net.*;
import java.util.Arrays;
import java.util.StringTokenizer;
import java.util.concurrent.ThreadLocalRandom;

public class ServerPing extends Thread {

    class AuxComputeResponse {
        DatagramPacket dp; // if dp == null => not_sent
        int delay; // if delay == -1 => not_sent
    }

    private final static int BUF_SIZE = 256;
    private final static int NUM_PINGS = 10;
    private final static int PROBABILITA = 4; // 1/4 -> denominatore (per il modulo)

    private final int SERV_PORT;

    public ServerPing(int port) {
        this.SERV_PORT = port;
    }

    public void run() {
       try(DatagramSocket serverSocket = new DatagramSocket(SERV_PORT)) {

           // preprazione alla ricezione dei datagrammi
           byte[] buffer = new byte[BUF_SIZE];
           DatagramPacket rcvdPacket;
           DatagramPacket sendPacket;
           boolean sent = true; // se risponde o meno al datagramma corrente, si con 3/4 di probabilità
           int delay = 0;

           while(true) {
               Arrays.fill(buffer, (byte) 0);
               rcvdPacket = new DatagramPacket(buffer, buffer.length);
               // ricezione del datagramma
               serverSocket.receive(rcvdPacket);

                // elaborazione risposta
               AuxComputeResponse answer = computeResponse(rcvdPacket.getData(), rcvdPacket.getLength(), rcvdPacket.getPort());
               sendPacket = answer.dp;
               delay = answer.delay;

               if(sendPacket == null)   sent = false;
               else                     sent = true;

               // output
               String byteToString = new String(
                       rcvdPacket.getData(),
                       0,
                       rcvdPacket.getLength(),
                       "US-ASCII");

               String output = buildOutput(rcvdPacket, delay, byteToString, sent);
               System.out.println(output);

               // invio messaggio
               if (sendPacket != null) { serverSocket.send(sendPacket); } // invio PING al client

               // controllo terminazione
               try {
                   if(terminationCheck(byteToString) == true)
                       break;
               } catch (CorruptedUDPDataException e) {
                   e.printStackTrace();
                   return;
               }
           }
       } catch (IOException | InterruptedException e) {
           e.printStackTrace();
           return;
       }

       System.out.print("[SERVER] bye!");
    }

    private AuxComputeResponse computeResponse(byte[] arrData, int lenData, int dest_port) throws UnknownHostException, InterruptedException {
        AuxComputeResponse sendAux = new AuxComputeResponse();
        DatagramPacket sendPacket;
        int rand;
        /*
            [#] dopo aver ricevuto un PING, il server determina se ignorare il pacchetto
 *              (simulandone la perdita) o effettuarne l'eco. La probabilità di perdita di
 *              pacchetti di default è del 25%.
 *          [#] se decide di effettuare l'eco del PING, il server attende un intervallo di tempo
 *              casuale per simulare la latenza di rete
                */
        rand = ThreadLocalRandom.current().nextInt(1, 5);
        if((rand % PROBABILITA) != 0) {
            sendAux.delay = ThreadLocalRandom.current().nextInt(20, 100);
            Thread.sleep(sendAux.delay);

            sendPacket = new DatagramPacket(
                    arrData,
                    lenData,
                    InetAddress.getLocalHost(),
                    dest_port);

            sendAux.dp = sendPacket;
            return sendAux;
        }
        sendAux.delay = -1; // not_sent
        sendAux.dp = null;
        return sendAux;
    }

    private boolean terminationCheck(String data) throws CorruptedUDPDataException {
        StringTokenizer st = new StringTokenizer(data);

        String pingNumStr = null;
        // es data: PING 0 1360792326564
        for(int i = 0; i < 2; i++) {
            if(!st.hasMoreTokens()) {
                throw new CorruptedUDPDataException();
            }
            pingNumStr = st.nextToken(); // 1:PING, 2:NUMBER!
        }

        if((NUM_PINGS-1) == Integer.parseInt(pingNumStr))
            return true;

        return false;
    }

    private String buildOutput(DatagramPacket dp, int delay, String data, boolean sent) {
        /* stampa l'indirizzo IP e la porta del client, il messaggio di PING e l'azione
         *      intrapresa dal server in seguito alla sua ricezione (PING non inviato,oppure
         *      PING ritardato di x ms).
         */
        StringBuilder sbuilder = new StringBuilder();

        sbuilder.append(dp.getAddress());
        sbuilder.append(":");
        sbuilder.append(dp.getPort());
        sbuilder.append("> ");
        sbuilder.append(data);
        sbuilder.append(" ACTION: ");
        if(sent == true) {
            sbuilder.append("delayed ");
            sbuilder.append(delay);
            sbuilder.append(" ms");
        } else {
            sbuilder.append("not sent");
        }

        return sbuilder.toString();
    }
}
