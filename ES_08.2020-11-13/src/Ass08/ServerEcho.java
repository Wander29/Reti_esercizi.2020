package Ass08;

/**
 * @author              VENTURI LUDOVICO 578033
 * @date                2020/11/22
 * @version             1.0
 */

/**
 * scrivere un programma echo server usando :
 *  * - la libreria java NIO e, in particolare
 *  * - il Selector e
 *  * - canali in modalità non bloccante
 *
 *  Il server accetta richieste di connessioni dai client, riceve messaggi inviati dai client e li
 *      rispedisce (eventualmente aggiungendo "echoed by server" al messaggio ricevuto).
 */

import java.io.IOException;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeoutException;

public class ServerEcho extends Thread {
    // private final static int TIMEOUT_SELECT = 5000; //ms

    private final static int BUF_SIZE = 256;
    private final static String ECHO_STRING = "echoed by server [Ludo]: ";
    private final static byte[] ECHO_BYTES = ECHO_STRING.getBytes();

    private final int SERV_PORT;

    public ServerEcho(int p) {
        this.SERV_PORT = p;
    }

    public void run() {
        /**
         *  crea un serversocket channel e selettore
         *  si mette in attesa sulla select per la accept
         *  quando isAcceptable
         *      -> registra socketChannel per la comunicazione
     *      quando isReadable
         *      -> legge e prepara la risposta
     *      quando isWritable
         *      -> scrive la risposta
         */

        try ( ServerSocketChannel ssCh = ServerSocketChannel.open(); ) {
            // canale creato, lo setto NON_BLOCKING assegnandogli la porta
            ServerSocket ss = ssCh.socket();
            ss.bind(new InetSocketAddress(this.SERV_PORT));
            ssCh.configureBlocking(false);

            // selettore, in attesa della accept sul canale serverSocketChannel
            Selector sel = Selector.open();
            ssCh.register(sel, SelectionKey.OP_ACCEPT);

outer_loop: // label per identificare i 2 loop. Con un successivo «break outer_loop» salto a dopo i cicli
            while(true) {
                // bloccante
                if(sel.select() == 0)
                    continue;

                // controllo chiavi selezionate
                Set<SelectionKey> selectedSet = sel.selectedKeys();
                Iterator<SelectionKey> it = selectedSet.iterator();

                while(it.hasNext()) {
                    SelectionKey key = it.next();
                    it.remove(); // chiave da rimuovere dal selectedSet (gestione esplicita)

                    if(key.isAcceptable()) {
                        try { connectAndRegisterReadable(key); }
                        catch (IOException e) { System.err.println("errore durante la connessione col client"); }

                        System.out.println("Connessione con 1 client stabilita!");
                    }
                    if(key.isReadable()) {
                        try { readMessageAndRegisterWritable(key); }
                        catch (TerminationException e) {
                            key.cancel();
                            System.out.println("Connessione con 1 client terminata!");
                            //break outer_loop;
                            break;
                                // per avere un comportamento while(true) usare «break;»
                        }
                        catch (IOException e) { System.err.println("errore durante la lettura"); }
                    }
                    if(key.isWritable()) {
                        try { writeMessageAndRegisterReadable(key); }
                        catch (IOException e) { System.err.println("errore durante la scrittura"); }
                    }
                }
            }

        }
        catch (ClosedChannelException cce)  { System.err.println("Canale chiuso, Exiting"); }
        catch (BindException be)            { System.err.println("Porta occupata, Exiting"); }
        catch (IOException e)               { System.err.println("IOException, Exiting"); }

        System.out.println("[SERVER] Correct exiting");
    }

    /**
     * scrive il messaggio già presente nel buffer associato alla key sul canale Writable
     * lascia il buffer in modalità lettura
     * @requires        il buffer associato alla comunicazione deve essere già in modalità LETTURA
     * @param key       chiave con associato un canale Writable ed un ByteBuffer
     * @throws IOException
     */
    private void writeMessageAndRegisterReadable(SelectionKey key) throws IOException {
        SocketChannel clientCh = (SocketChannel) key.channel();
        ByteBuffer buf = (ByteBuffer) key.attachment();

        // write non-bloccante
        while(buf.hasRemaining())
            clientCh.write(buf);

        buf.clear();
        clientCh.register(key.selector(), SelectionKey.OP_READ, buf);
    }

    /**
     * leggo il messaggio, lasciandolo nel buffer associato alla chiave
     * legge prima il messaggio, aggiunge "echoed by server: " e lo reinserisce nel buffer
     *
     * @param key       chiave con associato un canale Readable ed un ByteBuffer
     * @throws IOException
     * @throws TerminationException  quando il client comunica il codice di terminazione
     */
    private void readMessageAndRegisterWritable(SelectionKey key) throws IOException, TerminationException {
        SocketChannel clientCh = (SocketChannel) key.channel();
        ByteBuffer buf = (ByteBuffer) key.attachment();

        // read non-bloccante => scrittura sul buffer (1)
        int numBytesRead = 0;
        int totBytesRead = 0;
        while( (numBytesRead = clientCh.read(buf)) > 0)
            totBytesRead += numBytesRead;

        if(totBytesRead == 0) {
            clientCh.close();
            throw new IOException();
        }

        buf.flip();

        // aggiunta header al msg, costruisco la stringa da inviare
        StringBuilder sbuilder = new StringBuilder();

        // (2) lettura dal buffer
        while(buf.hasRemaining())
            sbuilder.append((char) buf.get());

        String receivedString = sbuilder.toString();
        System.out.println("\n[Server received] " + receivedString);

        // controllo Terminazione, secondo il protocollo
        if(receivedString.equals(CSProtocol.EXIT_STRING()))
            throw new TerminationException();

        String answerToSend = ECHO_STRING + " " + receivedString;
        System.out.println("[Server is sending] " + answerToSend);

        byte[] answerInBytes = answerToSend.getBytes();
        buf.clear();
        // (3) scrittura sul buffer
        buf.put(answerInBytes);
        buf.flip();

        clientCh.register(key.selector(), SelectionKey.OP_WRITE, buf);
    }

    /**
     * prende una chiave con associato un canale pronto per la accept
     * crea un socket per questa comunicazione
     * lo registra come da leggere sullo stesso selettore collegando un ByteBuffer
     *
     * @param key       chiave con associato un canale pronto per la accept()
     * @throws IOException
     */
    private void connectAndRegisterReadable(SelectionKey key) throws IOException {
        ServerSocketChannel serverCh = (ServerSocketChannel) key.channel();
        SocketChannel client = serverCh.accept();

        client.configureBlocking(false);
        ByteBuffer buf = ByteBuffer.allocate(BUF_SIZE);

        client.register(key.selector(), SelectionKey.OP_READ, buf);
    }
}
