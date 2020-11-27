import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * EchoClient modella il client del servizio di NIO Echo
 *
 * @author Samuel Fabrizi
 * @version 1.0
 */
public class EchoClient {
    /**
     * dimensione del buffer utilizzato per la lettura
     */
    private final int BUFFER_DIMENSION = 1024;
    /**
     * comando utilizzato dal client per comunicare la fine della comunicazione
     */
    private final String EXIT_CMD = "exit";
    /**
     * porta su cui il server è in ascolto
     */
    private final int port;
    /**
     * true se il client è in terminazione
     * false altrimenti
     */
    private boolean exit;

    /**
     *
     * @param port porta su cui il client è in ascolto
     */
    public EchoClient(int port){
        this.port = port;
        this.exit = false;
    }

    /**
     * avvia l'esecuzione del client
     */
    public void start(){

        try ( SocketChannel client = SocketChannel.open(new InetSocketAddress(InetAddress.getLocalHost(), port)); )
        {
            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));

            System.out.println("Client: connesso");
            System.out.println("Digita 'exit' per uscire, i messaggi scritti saranno inviati al server:");

            while (!this.exit) {

                String msg = consoleReader.readLine().trim();

                if (msg.equals(this.EXIT_CMD)){
                    this.exit = true;
                    continue;
                }

                // Creo il messaggio da inviare al server

                // la prima parte del messaggio contiene la lunghezza del messaggio
                ByteBuffer length = ByteBuffer.allocate(Integer.BYTES);
                length.putInt(msg.length());
                length.flip();
                client.write(length);
                length.clear();

                // la seconda parte del messaggio contiene il messaggio da inviare
                ByteBuffer readBuffer = ByteBuffer.wrap(msg.getBytes());

                client.write(readBuffer);
                readBuffer.clear();

                ByteBuffer reply = ByteBuffer.allocate(BUFFER_DIMENSION);
                client.read(reply);
                reply.flip();
                System.out.printf("Client: il server ha inviato %s\n", new String(reply.array()).trim());
                reply.clear();

            }
            System.out.println("Client: chiusura");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }


}

