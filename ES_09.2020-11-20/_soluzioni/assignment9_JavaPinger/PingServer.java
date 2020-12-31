import java.io.IOException;
import java.net.*;
import java.util.Random;

/**
 * PingServer modella il server del servizio di ping
 *
 * @author Samuel Fabrizi
 * @version 1.0
 */
public class PingServer {
    /**
     * dimensione del buffer
     */
    private final int BUFFER_SIZE = 100;
    /**
     * porta su cui è in ascolto il server
     */
    private final int port;
    /**
     * hostname del server
     */
    private final String serverName;

    /**
     *
     * @param port porta su cui è in ascolto il server
     * @param serverName hostname del server
     */
    public PingServer(int port, String serverName){
        this.port = port;
        this.serverName = serverName;
    }

    /**
     * avvia il server
     */
    public void start(){
        Random rand = new Random();

        try (DatagramSocket serverSocket = new DatagramSocket(port) ){
            byte[] buffer = new byte[BUFFER_SIZE];
            DatagramPacket receivedPacket = new DatagramPacket(buffer, buffer.length);

            while (true){
                serverSocket.receive(receivedPacket);
                // messaggio di ping del client
                String msg = new String(receivedPacket.getData());

                System.out.printf("%s:%d %s ACTION: ",
                        receivedPacket.getAddress(),
                        receivedPacket.getPort(),
                        msg
                );

                // simula la latenza di rete
                int delay = 50 * (rand.nextInt(20) + 1);

                if (rand.nextInt(100) > 25){          // probabilita' di perdita dei pacchetti = 1/4
                    // crea l'echo message
                    DatagramPacket packetToSend = new DatagramPacket(
                            msg.getBytes(),
                            msg.length(),
                            InetAddress.getByName(this.serverName),
                            receivedPacket.getPort()
                    );
                    // Latenza rete
                    Thread.sleep(delay);

                    serverSocket.send(packetToSend);

                    System.out.printf("delayed %d ms\n", delay);
                }
                else {
                    System.out.printf("not sent\n");
                }
            }
        }
        catch (BindException e){
            System.out.println("Porta già occupata");
        }
        catch (IOException | InterruptedException e) {         // NB: SocketException è una sottoclasse di IOException
            e.printStackTrace();
        }

    }

}

