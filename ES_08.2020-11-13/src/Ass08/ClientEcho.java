package Ass08;

/**
 * @author              VENTURI LUDOVICO 578033
 * @date                2020/11/22
 * @version             1.0
 */

import java.io.IOException;
import java.net.BindException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

/**
 *  client, usando NIO (va bene anche con modalità bloccante).
 *
 *   Il client legge il messaggio da inviare da console, lo invia al server e visualizza quanto
 *      ricevuto dal server.
 */

public class ClientEcho extends Thread {
    private final static int BUF_SIZE       = 256;
    private final static int NUM_ITERAZIONI = 3;

    private static int SERV_PORT;
    private static InetAddress SERV_ADDRESS;

    public ClientEcho(int p, InetAddress servAddr) {
        this.SERV_PORT = p;
        this.SERV_ADDRESS = servAddr;
    }

    public void run() {
        /**
         * CLIENT: NON_BLOCKING
         *
         *  crea un socketchannel
         *  prova a connettersi al server
         *
         *  una volta connesso scrive 3 messaggi, si aspetta 3 risposte
         *  prima di mandare il messaggio successivo attende l'arrivo del precedente
         */

        try( SocketChannel clientCh = SocketChannel.open(); ) {
            // setto il canale di comunicazione non bloccante
            clientCh.configureBlocking(false);
            InetSocketAddress serverAddress = new InetSocketAddress(this.SERV_ADDRESS, this.SERV_PORT);

            // selettore
            Selector sel = Selector.open();
            clientCh.register(sel, SelectionKey.OP_CONNECT);

            int iterazioni = 0; // necessario per la terminazione

outer_loop:
            while(true) {
                if(sel.select() == 0)
                    continue;

                Set<SelectionKey> selectedSet = sel.selectedKeys();
                Iterator<SelectionKey> it = selectedSet.iterator();

                while(it.hasNext()) {
                    SelectionKey key = it.next();
                    it.remove();

                    if(key.isConnectable()) {
                        this.connectAndRegisterWritable(key, serverAddress);
                        System.out.println("Connesso al server");
                    }
                    if(key.isWritable()) {
                        this.writeAndRegisterReadable(key);
                    }
                    if(key.isReadable()) {
                        this.readAnswer(key);

                        if( ++iterazioni < NUM_ITERAZIONI )
                            registerWritable(key);
                        else {
                            sayByeToServer(key);
                            break outer_loop; // esce dai cicli, la label li wrappa
                        }
                    }
                }
            }
        }
        catch (BindException be)            { System.err.println("Porta occupata, Exiting"); }
        catch (IOException e)               { System.err.println("IOException, Exiting"); }
        catch (InterruptedException e)      { System.err.println("Sleep interrotta! Exiting"); }

        System.out.println("[CLIENT] Correct exiting");
    }

    /**
     * saluta il server per terminare, inviano il codice di uscita già definito
     * Chiude il socketChannel
     * elimina la key dal selector
     *
     * @param key       chiave su cui è associato un canale scrivibile
     * @throws IOException
     */
    private void sayByeToServer(SelectionKey key) throws IOException {
        SocketChannel clientCh = (SocketChannel) key.channel();
        ByteBuffer buf = (ByteBuffer) key.attachment();
        buf.clear();

        System.out.println("\n[Client is exiting, sending] " + CSProtocol.EXIT_STRING());
        buf.put(CSProtocol.EXIT_STRING().getBytes());
        buf.flip();

        while(buf.hasRemaining())
            clientCh.write(buf);

        clientCh.close();
        key.cancel();
    }

    /**
     * registra il canale associato alla chiave key con interessa alla WRITE, associando il bytebuffer
     * @param key       chiave SelectionKey di un selector
     * @throws ClosedChannelException
     */
    private void registerWritable(SelectionKey key) throws ClosedChannelException {
        SocketChannel clientCh = (SocketChannel) key.channel();
        ByteBuffer buf = (ByteBuffer) key.attachment();
        buf.clear();

        clientCh.register(key.selector(), SelectionKey.OP_WRITE, buf);
    }

    /**
     * legge la risposta del server e la stampa
     * @param key       chiave cui è associato il canale di comunicazione col Server ed è Readable
     * @throws IOException
     */
    private void readAnswer(SelectionKey key) throws IOException {
        SocketChannel clientCh = (SocketChannel) key.channel();
        ByteBuffer buf = (ByteBuffer) key.attachment();

        // leggo tutta la risposta
        while(clientCh.read(buf) > 0);
        buf.flip();

        // la ricostruisco byte x byte
        StringBuilder sbuilder = new StringBuilder();
        while(buf.hasRemaining())
            sbuilder.append((char) buf.get());

        System.out.println("[Client received]" + sbuilder.toString());
    }

    /**
     * scrive sul canale il contenuto del buffer e registra il canale per la lettura
     * @requires        buffer associato già in modalità scrittura
     * @param key       chiave con canale associato leggibile senza interruzioni
     * @throws IOException
     */
    private void writeAndRegisterReadable(SelectionKey key) throws IOException {
        SocketChannel clientCh = (SocketChannel) key.channel();
        ByteBuffer buf = (ByteBuffer) key.attachment();

        String str = pickRandomString();
        System.out.println("\n[Client is sending] " + str);
        buf.put(str.getBytes());
        buf.flip();

        // scrittura sul canale
        while(buf.hasRemaining())
            clientCh.write(buf);

        buf.clear();
        clientCh.register(key.selector(), SelectionKey.OP_READ, buf);
    }

    /**
     * si connette al server
     * registra il canale per la scrittura
     *
     * @param key       chiave con canale associato sul quale si può fare connect() correttamente
     * @param add
     * @throws IOException
     * @throws InterruptedException
     */
    private void connectAndRegisterWritable(SelectionKey key, InetSocketAddress add)
            throws IOException, InterruptedException
    {
        SocketChannel clientCh = (SocketChannel) key.channel();

        clientCh.connect(add);
        while(clientCh.finishConnect() == false) {
            Thread.sleep(200);
            clientCh.connect(add);
        }

        ByteBuffer buf = ByteBuffer.allocate(BUF_SIZE);
        clientCh.register(key.selector(), SelectionKey.OP_WRITE, buf);
    }

    /**
     * @source          https://www.baeldung.com/java-random-string
     */
    // public void givenUsingJava8_whenGeneratingRandomAlphabeticString_thenCorrect() {
    public String pickRandomString() {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        Random random = new Random();

        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        return generatedString;
    }
}
