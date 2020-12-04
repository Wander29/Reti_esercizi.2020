package Ass09;

/**
 * @author      LUDOVICO VENTURI 578033
 * @date        2020/11/21
 * @version     1.0
 */

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
 * COMUNICAZIONE
 *  UDP
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

    // wrapper di ausilio
    class AuxComputeResponse {
        DatagramPacket dp; // if dp == null => not_sent
        int delay; // if delay == -1 => not_sent
    }

    private final static int BUF_SIZE   = 256;       // dimensioni buffer invio & ricezione
    private final static int NUM_PINGS  = 10;        // numero di messaggi ping aspettati
    private final static int MIN_WAIT   = 20;        // tempo di attesa minimo per rispedire un ping
    private final static int MAX_WAIT   = 250;       // tempo di attesa massimo per rispedire un ping
    private final static double PROBABILITA = 0.25;  // probabilità espressa in decimi

    private final int serverPort;                    // porta del servizio

    public ServerPing(int port) {
        this.serverPort = port;
    }

    public void run() {
       try(DatagramSocket serverSocket = new DatagramSocket(serverPort)) {

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
               String byteToString = new String( rcvdPacket.getData(),
                                                 rcvdPacket.getOffset(),
                                                 rcvdPacket.getLength(),
                                     "US-ASCII" );

               String output = buildOutput( rcvdPacket.getAddress(),
                                            rcvdPacket.getPort(),
                                            delay,
                                            byteToString,
                                            sent );
               System.out.println(output);

               // invio messaggio
               if (sendPacket != null) serverSocket.send(sendPacket);  // invio PING al client

               // controllo terminazione
               try { if(terminationCheck(byteToString) == true) break; }
               catch (CorruptedUDPDataException cude)  {
                   System.err.println("campo DATA di un datagramma non valido"); }
           }

       }
       catch (BindException b)        { System.err.printf("port %d già in uso\n", this.serverPort); }
       catch (InterruptedException i) { System.err.println(""); }
       catch (IOException e) {
           e.printStackTrace();
           return;
       }

       System.out.print("[SERVER] bye!");
    }

    /**
     * Elabora la risposta da inviare al Client
     * @param arrData   byte del DATA del pachetto
     * @param lenData   lunghezza del campo DATA
     * @param dest_port porta destinatario
     * @return wrapper del pacchetto e del ritardo simulante i ritardi di rete
     * @throws UnknownHostException
     * @throws InterruptedException
     */
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
        if((rand % (1 / PROBABILITA)) != 0) {
            sendAux.delay = ThreadLocalRandom.current().nextInt(MIN_WAIT, MAX_WAIT);
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

    /**
     * Controlla la terminazione del server. Deve terminare se sta servendo l'ultimo pacchetto
     *  della sequenza
     * @param data      stringa contenente il DATA del pacchetto ricevuto
     * @return true se il messaggio ricevuto è l'ultimo della sequenza
     * @throws CorruptedUDPDataException    se non ci sono almento 2 token nel campo data
     */
    private boolean terminationCheck(String data) throws CorruptedUDPDataException {
        StringTokenizer st = new StringTokenizer(data);
        // es data: PING 0 1360792326564

        String pingNumStr = null;
        for(int i = 0; i < 2; i++) {
            if(!st.hasMoreTokens()) { // non ci sono 2 token nel campo Data!
                throw new CorruptedUDPDataException();
            }
            pingNumStr = st.nextToken(); // 1:PING, 2:NUMBER!
        }

        if((NUM_PINGS-1) == Integer.parseInt(pingNumStr))
            return true;

        return false;
    }

    /**
     * stampa l'indirizzo IP e la porta del client, il messaggio di PING e l'azione
     *          intrapresa dal server in seguito alla sua ricezione (PING non inviato,oppure
     *          PING ritardato di x ms).
     * costruisce l'output da stampare ad ogni ricezione, es:
     *      127.0.0.1:57582>PING 1 1605954649613 ACTION: delayed 99 ms
     *      127.0.0.1:57582>PING 2 1605954649713 ACTION: not sent
     *
     * @param address   indirizzo del mittente
     * @param port      porta del mittente
     * @param delay     ritardo introdotto
     * @param data      sitrnga del campo DATA del pacchetto ricevuto
     * @param sent      indica se si risponderà o meno al client -> simulazione perdita pacchetto
     * @return stringa contenente l'otuput da stampare
     */
    private String buildOutput(InetAddress address, int port, int delay, String data, boolean sent) {
        StringBuilder sbuilder = new StringBuilder();

        sbuilder.append(address.toString().substring(1));
        sbuilder.append(":");
        sbuilder.append(port);
        sbuilder.append("> ");
        sbuilder.append(data);
        sbuilder.append(" ACTION: ");
        if(sent == true) {
            sbuilder.append("delayed ");
            sbuilder.append(delay);
            sbuilder.append(" ms");
        }
        else
            sbuilder.append("not sent");

        return sbuilder.toString();
    }
}
