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
 * EchoServer modella il server del servizio di NIO Echo
 *
 * @author Samuel Fabrizi
 * @version 1.0
 */
class EchoServer {
    /**
     * dimensione del buffer utilizzato per la lettura
     */
    private final int BUFFER_DIMENSION = 1024;
    /**
     * comando utilizzato dal client per comunicare la fine della comunicazione
     */
    private final String EXIT_CMD = "exit";
    /**
     * porta su cui aprire il listening socket
     */
    private final int port;
    /**
     * messaggio di risposta
     */
    private final String ADD_ANSWER = "echoed by server";
    /**
     * numero di client con i quali è aperta una connessione
     */
    public int numberActiveConnections;

    /**
     *
     * @param port porta su cui aprire il listening socket
     */
    public EchoServer(int port){
        this.port = port;
    }

    /**
     * avvia l'esecuzione del server
     */
    public void start() {
        this.numberActiveConnections = 0;
        try (
                ServerSocketChannel s_channel = ServerSocketChannel.open();
        ){
            s_channel.socket().bind(new InetSocketAddress(this.port));
            s_channel.configureBlocking(false);
            Selector sel = Selector.open();
            s_channel.register(sel, SelectionKey.OP_ACCEPT);
            System.out.printf("Server: in attesa di connessioni sulla porta %d\n", this.port);
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
                    try {       // utilizzo la try-catch per gestire la terminazione improvvisa del client
                        if (key.isAcceptable()) {               // ACCETTABLE
                            /*
                             * accetta una nuova connessione creando un SocketChannel per la
                             * comunicazione con il client che la richiede
                             */
                            ServerSocketChannel server = (ServerSocketChannel) key.channel();
                            SocketChannel c_channel = server.accept();
                            c_channel.configureBlocking(false);
                            System.out.println("Server: accettata nuova connessione dal client: " + c_channel.getRemoteAddress());
                            System.out.printf("Server: numero di connessioni aperte: %d\n", ++this.numberActiveConnections);
                            this.registerRead(sel, c_channel);
                        } else if (key.isReadable()) {                  // READABLE
                            this.readClientMessage(sel, key);
                        }

                        if (key.isWritable()) {                 // WRITABLE
                            this.echoAnswer(sel, key);
                        }
                    }
                    catch (IOException e) {             // terminazione improvvisa del client
                        e.printStackTrace();
                        key.channel().close();
                        key.cancel();
                    }
                }
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }


    /**
     * registra l'interesse all'operazione di READ sul selettore
     *
     * @param sel selettore utilizzato dal server
     * @param c_channel socket channel relativo al client
     * @throws IOException se si verifica un errore di I/O
     */
    private void registerRead(Selector sel, SocketChannel c_channel) throws IOException {

        // crea il buffer
        ByteBuffer length = ByteBuffer.allocate(Integer.BYTES);
        ByteBuffer message = ByteBuffer.allocate(BUFFER_DIMENSION);
        ByteBuffer[] bfs = {length, message};
        // aggiunge il canale del client al selector con l'operazione OP_READ
        // e aggiunge l'array di bytebuffer [length, message] come attachment
        c_channel.register(sel, SelectionKey.OP_READ, bfs);
    }

    /**
     * legge il messaggio inviato dal client e registra l'interesse all'operazione di WRITE sul selettore
     *
     * @param sel selettore utilizzato dal server
     * @param key chiave di selezione
     * @throws IOException se si verifica un errore di I/O
     */
    private void readClientMessage(Selector sel, SelectionKey key) throws IOException {
        /*
         * accetta una nuova connessione creando un SocketChannel per la comunicazione con il client che
         * la richiede
         */
        SocketChannel c_channel = (SocketChannel) key.channel();
        // recupera l'array di bytebuffer (attachment)
        ByteBuffer[] bfs = (ByteBuffer[]) key.attachment();
        c_channel.read(bfs);
        if (!bfs[0].hasRemaining()){
            bfs[0].flip();
            int l = bfs[0].getInt();

            if (bfs[1].position() == l) {
                bfs[1].flip();
                String msg = new String(bfs[1].array()).trim();
                System.out.printf("Server: ricevuto %s\n", msg);
                if (msg.equals(this.EXIT_CMD)){
                    System.out.println("Server: chiusa la connessione con il client " + c_channel.getRemoteAddress());
                    c_channel.close();
                    key.cancel();
                }
                else {
                    /*
                     * aggiunge il canale del client al selector con l'operazione OP_WRITE
                     * e aggiunge il messaggio ricevuto come attachment (aggiungendo la risposta addizionale)
                     */
                    c_channel.register(sel, SelectionKey.OP_WRITE, msg + " " + this.ADD_ANSWER);
                }
            }
        }
    }

    /**
     * scrive il buffer sul canale del client
     *
     * @param key chiave di selezione
     * @throws IOException se si verifica un errore di I/O
     */
    private void echoAnswer(Selector sel, SelectionKey key) throws IOException {
        SocketChannel c_channel = (SocketChannel) key.channel();
        String echoAnsw= (String) key.attachment();
        ByteBuffer bbEchoAnsw = ByteBuffer.wrap(echoAnsw.getBytes());
        c_channel.write(bbEchoAnsw);
        System.out.println("Server: " + echoAnsw + " inviato al client " + c_channel.getRemoteAddress());
        if (!bbEchoAnsw.hasRemaining()) {
            bbEchoAnsw.clear();
            this.registerRead(sel, c_channel);
        }
    }
}