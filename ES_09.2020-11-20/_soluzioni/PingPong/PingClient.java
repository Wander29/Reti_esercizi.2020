import java.io.IOException;
import java.net.*;

/**
 * PingClient modella il client del servizio di "Ping Pong"
 *
 * @author Samuel Fabrizi
 * @version 1.2
 */
public class PingClient {
    private final int BUFFER_SIZE = 4;
    private final String PING = "PING";
    private final byte[] pingBuffer;
    /**
     * porta su cui è in ascolto il server
     */
    private final int serverPort;
    /**
     * hostname del server
     */
    private final String serverName;
    /**
     * attesa per la ricezione di una risposta da parte del server(ms)
     */
    private final int timeout;

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
        this.pingBuffer = PING.getBytes();
    }

    /**
     * avvia il client
     */
    public void start() {
        try (DatagramSocket client = new DatagramSocket() ){
            DatagramPacket packetToSend = new DatagramPacket(
                    pingBuffer,
                    pingBuffer.length,
                    InetAddress.getByName(this.serverName),
                    this.serverPort
            );
            System.out.printf("Invio %s al server\n", new String(pingBuffer));
            client.send(packetToSend);
            // imposta il timeout
            client.setSoTimeout(timeout);

            byte[] buffer = new byte[BUFFER_SIZE];
            DatagramPacket receivedPacket = new DatagramPacket(buffer, buffer.length);

            // rimane in attesa della risposta del server
            client.receive(receivedPacket);
            System.out.printf("Il client ha ricevuto %S\n" , new String(receivedPacket.getData()));
        }
        catch (SocketTimeoutException e){
            System.out.printf("Il client non ha ricevuto una risposta in %d ms\n", this.timeout);
        }
        catch (BindException e){
            System.out.println("Porta già occupata");
        }
        catch (IOException e) {         // NB: SocketException è una sottoclasse di IOException
            e.printStackTrace();
        }
    }

}
