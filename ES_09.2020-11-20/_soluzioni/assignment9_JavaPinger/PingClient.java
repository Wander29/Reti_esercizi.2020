import java.io.IOException;
import java.net.*;

/**
 * PingClient modella il client del servizio di ping
 *
 * @author Samuel Fabrizi
 * @version 1.0
 */
public class PingClient {
    private final int BUFFER_SIZE = 100;
    private final String PING = "PING";
    /**
     * porta su cui è in ascolto il server
     */
    private final int serverPort;
    /**
     * hostname del server
     */
    private final String serverName;
    /**
     * attesa per la ricezione di una risposta da parte del server (ms)
     */
    private final int timeout;
    /**
     * numero di seqno da inviare
     */
    private final int N_SEQNO = 10;


    /**
     *
     * @param serverPort porta del server
     * @param serverName nome del server
     * @param timeout attesa per la ricezione di una risposta (ms)
     */
    public PingClient(int serverPort, String serverName, int timeout){
        this.timeout = timeout;
        this.serverPort = serverPort;
        this.serverName = serverName;
    }

    /**
     * avvia il client
     */
    public void start() {
        // numero di pacchetti trasmessi
        int packetsTransmitted = 0;
        // numero di pacchetti ricevuti
        int packetsReceived = 0;
        // RTT massimo
        long rttMax = 0;
        // RTT minimo
        long rttMin = 10000000;
        // somma degli RTT (utilizzato per il calcolo della media)
        long rttSum = 0;

        try (DatagramSocket client = new DatagramSocket() ) {
            for (int seqno = 0; seqno < N_SEQNO; seqno++) {
                // timestamp
                long initTime = System.currentTimeMillis();

                // messaggio di ping
                String msg = PING + " " + seqno + " " + initTime;

                byte[] pingBuffer = msg.getBytes();
                DatagramPacket packetToSend = new DatagramPacket(
                        pingBuffer,
                        pingBuffer.length,
                        InetAddress.getByName(this.serverName),
                        this.serverPort
                );
                // invia il ping
                client.send(packetToSend);

                packetsTransmitted++;
                // imposta il timeout
                client.setSoTimeout(timeout);

                byte[] buffer = new byte[BUFFER_SIZE];
                DatagramPacket receivedPacket = new DatagramPacket(buffer, buffer.length);

                // rimane in attesa della risposta del server
                try {
                    client.receive(receivedPacket);
                    long endTime = System.currentTimeMillis();

                    // Aggiorna statistiche
                    long RTT = endTime - initTime;
                    rttMin = Math.min(rttMin, RTT);
                    rttMax = Math.max(rttMax, RTT);
                    rttSum = rttSum + RTT;
                    packetsReceived++;
                    System.out.printf("%s RTT: %d ms\n", new String(pingBuffer), RTT);

                } catch (SocketTimeoutException e) {
                    System.out.printf("%s RTT: *\n", msg);

                }
            }
        }

        catch (BindException e){
            System.out.println("Porta già occupata");
        }
        catch (IOException e) {         // NB: SocketException è una sottoclasse di IOException
            e.printStackTrace();
        }
        finally {
            // stampo le diverse statistiche ottenute

            float percPacketLoss = (float) (packetsTransmitted - packetsReceived) / (float) packetsTransmitted;
            percPacketLoss *= 100;

            float rttAvg = (packetsTransmitted != 0) ? (float) rttSum / (float) packetsTransmitted : 0;

            System.out.println("---- PING Statistics ----");
            System.out.printf("%d packets transmitted, %d packets received, %.0f%% packet loss\n",
                        packetsTransmitted,
                        packetsReceived,
                        percPacketLoss
            );
            System.out.printf("round-trip (ms) min/avg/max = %d/%.2f/%d\n",
                    rttMin,
                    rttAvg,
                    rttMax
            );
        }
    }

}
