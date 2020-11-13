import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Samuel Fabrizi
 * @version 1.2
 */

class HelloServer {
    /**
     * porta su cui aprire il listening socket
     */
    protected final int port;
    /**
     * messaggio di risposta
     */
    protected final String ANSWER = "HelloClient\n";

    /**
     *
     * @param port porta su cui aprire il listening socket
     */
    public HelloServer(int port){
        this.port = port;
    }

    /**
     * avvia l'esecuzione del server
     */
    public void start() {
        try (
                ServerSocketChannel s_channel = ServerSocketChannel.open();
        ){
            s_channel.socket().bind(new InetSocketAddress(this.port));
            s_channel.configureBlocking(false);
            Selector sel = Selector.open();
            s_channel.register(sel, SelectionKey.OP_ACCEPT);
            System.out.printf("Il server attende le connessioni sulla porta %d\n", this.port);
            while(true){
                if (sel.select() == 0)
                    continue;
                // insieme delle chiavi corrispondenti a canali pronti
                Set<SelectionKey> selectedKeys = sel.selectedKeys();
                // iteratore dell'insieme sopra definito
                Iterator<SelectionKey> iter = selectedKeys.iterator();
                while (iter.hasNext()) {
                    SelectionKey key = iter.next();
                    iter.remove();
                    if (key.isAcceptable()) {
                        this.answerWithHello(key);
                    }
                }
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }


    /**
     * scrive il buffer sul canale del client
     *
     * @param key chiave di selezione
     * @throws IOException se si verifica un errore di I/O
     */
    private void answerWithHello(SelectionKey key) throws IOException {
        ServerSocketChannel server = (ServerSocketChannel) key.channel();
        SocketChannel c_channel = server.accept();
        ByteBuffer myBuffer = ByteBuffer.wrap(ANSWER.getBytes());
        while (myBuffer.hasRemaining()) {
            c_channel.write(myBuffer);
        }
        System.out.println("Messaggio inviato al client: " + c_channel.getRemoteAddress());
        System.out.println(myBuffer); //debug
        c_channel.close();
    }
}