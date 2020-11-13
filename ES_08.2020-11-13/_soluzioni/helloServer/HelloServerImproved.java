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

class HelloServerImproved extends HelloServer{

    /**
     *
     * @param port porta su cui aprire il listening socket
     */
    public HelloServerImproved(int port) {
        super(port);
    }

    /**
     * avvia l'esecuzione del server
     */
    @Override
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
                        this.registerWrite(sel, key);
                    }
                    if (key.isWritable()) {

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
     * registra l'interesse all'operazione di WRITE sul selettore
     *
     * @param sel selettore utilizzato dal server
     * @param key chiave di selezione
     * @throws IOException se si verifica un errore di I/O
     */
    private void registerWrite(Selector sel, SelectionKey key) throws IOException {
        /*
         * accetta una nuova connessione creando un SocketChannel per la comunicazione con il client che
         * la richiede
         */
        ServerSocketChannel server = (ServerSocketChannel) key.channel();
        SocketChannel c_channel = server.accept();
        c_channel.configureBlocking(false);
        // crea il buffer
        ByteBuffer myBuffer = ByteBuffer.wrap(ANSWER.getBytes());
        // aggiunge il canale del client al selector con l'operazione OP_WRITE
        // e aggiunge il bytebuffer come attachment
        c_channel.register(sel, SelectionKey.OP_WRITE, myBuffer);
        System.out.println("Accettata nuova connessione dal client: " + c_channel.getRemoteAddress());
    }

    /**
     * scrive il buffer sul canale del client
     *
     * @param key chiave di selezione
     * @throws IOException se si verifica un errore di I/O
     */
    private void answerWithHello(SelectionKey key) throws IOException {
        SocketChannel c_channel = (SocketChannel) key.channel();
        ByteBuffer myBuffer= (ByteBuffer) key.attachment();
        c_channel.write(myBuffer);
        if (myBuffer.hasRemaining())
            return;
        System.out.println("Messaggio inviato al client: " + c_channel.getRemoteAddress());
        c_channel.close();
    }
}