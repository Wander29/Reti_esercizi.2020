package Ass08;

/**
 * scrivere un programma echo server usando :
 *  * - la libreria java NIO e, in particolare
 *  * - il Selector e
 *  * - canali in modalità non bloccante
 *
 *  Il server accetta richieste di connessioni dai client, riceve messaggi inviati dai client e li
 *      rispedisce (eventualmente aggiungendo "echoed by server" al messaggio ricevuto).
 */

import javax.naming.CommunicationException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeoutException;

public class ServerEcho extends Thread {
    private final int SERV_PORT;
    private final static int BUF_SIZE = 256;
    private final static String EXIT_STRING = "EXIT_029";

    private final static int TIMEOUT_SELECT = 5000; //ms

    private final static String ECHO_STRING = "echoed by server [Ludo]: ";
    private final static byte[] ECHO_BYTES = ECHO_STRING.getBytes();

    public ServerEcho(int p) {
        this.SERV_PORT = p;
    }

    public void run() {
        /**
         *  crea un serversocket channel e selettore
         *  si mette in attesa sulla select x la accept
         *  quando isAcceptable
         *      -> registra socketChannel per la comunicazione
     *      quando isReadable
         *      -> legge e prepara la risposta
     *      quando isWritable
         *      -> scrive la risposta
         */

        try (
                ServerSocketChannel ssCh = ServerSocketChannel.open();
        ) {
            // canale creato, lo setto NON_BLOCKING assegnandogli la porta
            ServerSocket ss = ssCh.socket();
            ss.bind(new InetSocketAddress(this.SERV_PORT));
            ssCh.configureBlocking(false);

            // selettore, in attesa della accept sul canale serverSocketChannel
            Selector sel = Selector.open();
            SelectionKey myKey;
            ssCh.register(sel, SelectionKey.OP_ACCEPT);
outer_loop:
            while(true) {
                if(sel.select() == 0)
                    continue;

                Set<SelectionKey> selectedSet = sel.selectedKeys();
                Iterator<SelectionKey> it = selectedSet.iterator();

                while(it.hasNext()) {
                    SelectionKey key = it.next();
                    it.remove(); // chiave da rimuovere dal selectedSet (gestione esplicita)

                    if(key.isAcceptable()) {
                        connectAndRegisterReadable(key);
                        System.out.println("Connessione con 1 client stabilita!");
                    }
                    if(key.isReadable()) {
                        try {
                            readMessageAndRegisterWritable(key);
                        } catch (CommunicationException e) {
                            key.cancel();
                            break outer_loop;
                        }
                    }
                    if(key.isWritable()) {
                        writeMessageAndRegisterReadable(key);
                    }
                }
            }

        }
        catch (IOException e) {
            System.out.println("IOException, exiting..");
        }

        System.out.println("[SERVER] Correct exiting");
    }

    /**
     * scrive il messaggio già presente nel buffer associato alla key sul canale che è Writable
     *
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
     */
    private void readMessageAndRegisterWritable(SelectionKey key) throws IOException, CommunicationException {
        SocketChannel clientCh = (SocketChannel) key.channel();
        ByteBuffer buf = (ByteBuffer) key.attachment();

        // read non-bloccante
        int numBytesRead = 0;
        int totBytesRead = 0;
        while( (numBytesRead = clientCh.read(buf)) > 0)
            totBytesRead += numBytesRead;

        if(totBytesRead == 0) {
            clientCh.close();
            throw new IOException();
        }

        buf.flip();

        // aggiunta header al msg
        //StringBuilder sbuilder = new StringBuilder(ECHO_STRING);
        StringBuilder sbuilder = new StringBuilder();

        while(buf.hasRemaining()) {
            sbuilder.append((char) buf.get());
        }

        String receivedString = sbuilder.toString();
        System.out.println("\n[Server received] " + receivedString);

        if(receivedString.equals(EXIT_STRING)) {
            throw new CommunicationException();
        }

        String answerToSend = ECHO_STRING + " " + receivedString;
        System.out.println("[Server is sending] " + answerToSend);

        // scrittura sul buffer
        byte[] answerInBytes = answerToSend.getBytes();
        buf.clear();
        buf.put(answerInBytes);
        buf.flip();

        clientCh.register(key.selector(), SelectionKey.OP_WRITE, buf);
    }

    /**
     * prende una chiave con associato un canale pronto per la accept
     * crea un socket per questa comunicazione
     * lo registra come da leggere sullo stesso selettore
     *
     * @param key       chiave con associato un canale pronto
     */
    private void connectAndRegisterReadable(SelectionKey key) throws IOException {
        ServerSocketChannel serverCh = (ServerSocketChannel) key.channel();
        SocketChannel client = serverCh.accept();

        client.configureBlocking(false);
        ByteBuffer buf = ByteBuffer.allocate(BUF_SIZE);

        client.register(key.selector(), SelectionKey.OP_READ, buf);
    }
}
