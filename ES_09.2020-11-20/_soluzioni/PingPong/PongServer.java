import java.io.IOException;
import java.net.*;
import java.util.Random;

/**
 * PongServer modella il server del servizio di "Ping Pong"
 *
 * @author Samuel Fabrizi
 * @version 1.1
 */
public class PongServer {
    private final int BUFFER_SIZE = 4;
    private final String REPLY = "PONG";
    private final byte[] replyBuffer;
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
    public PongServer(int port, String serverName){
        this.port = port;
        this.serverName = serverName;
        this.replyBuffer = REPLY.getBytes();
    }

    /**
     * avvia il server
     */
    public void start(){
        Random rand = new Random();
        try (DatagramSocket serverSocket = new DatagramSocket(port) ){
            byte[] buffer = new byte[BUFFER_SIZE];
            DatagramPacket receivedPacket = new DatagramPacket(buffer, buffer.length);
            System.out.println("Il server è in attesa di un PING");

            while (true){
                serverSocket.receive(receivedPacket);
                String msg = new String(receivedPacket.getData());

                System.out.printf("Il server ha ricevuto %s\n" , msg);

                if (msg.equals("PING") && rand.nextBoolean()){          // invia una risposta con probabilità 1/2
                    DatagramPacket packetToSend = new DatagramPacket(
                            replyBuffer,
                            replyBuffer.length,
                            InetAddress.getByName(this.serverName),
                            receivedPacket.getPort()
                    );
                    serverSocket.send(packetToSend);
                }
            }
        }
        catch (BindException e){
            System.out.println("Porta già occupata");
        }
        catch (IOException e) {         // NB: SocketException è una sottoclasse di IOException
            e.printStackTrace();
        }

    }

}

