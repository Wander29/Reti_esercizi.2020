package Ass08;

import java.io.IOException;
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
    private final static int BUF_SIZE = 256;
    private final static int NUM_ITERAZIONI = 3;

    private final static String EXIT_STRING = "EXIT_029";

    private static int SERV_PORT;
    private static InetAddress SERV_ADDRESS;

    public ClientEcho(int p, String servName) throws UnknownHostException {
        this.SERV_PORT = p;
        this.SERV_ADDRESS = InetAddress.getByName(servName);
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
         *
         *
         */

        try(
                SocketChannel clientCh = SocketChannel.open();
        ) {
            clientCh.configureBlocking(false);
            InetSocketAddress serverAddress = new InetSocketAddress(this.SERV_ADDRESS, this.SERV_PORT);

            // selettore
            Selector sel = Selector.open();
            clientCh.register(sel, SelectionKey.OP_CONNECT);

            int iterazioni = 0;

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

                        if( ++iterazioni < NUM_ITERAZIONI)
                            registerWritable(key);
                        else {
                            sayByeToServer(key);
                            break outer_loop;
                        }
                    }
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("[CLIENT] Correct exiting");
    }

    /**
     * saluta il server per terminare
     * Chiude il socketChannel
     * elimina la key
     *
     * @param key
     * @throws IOException
     */
    private void sayByeToServer(SelectionKey key) throws IOException {
        SocketChannel clientCh = (SocketChannel) key.channel();
        ByteBuffer buf = (ByteBuffer) key.attachment();
        buf.clear();

        System.out.println("\n[Client is exiting, sending] " + EXIT_STRING);
        buf.put(EXIT_STRING.getBytes());
        buf.flip();

        while(buf.hasRemaining()) {
            clientCh.write(buf);
        }

        clientCh.close();
        key.cancel();
    }

    /**
     *
     * @param key
     * @throws ClosedChannelException
     */
    private void registerWritable(SelectionKey key) throws ClosedChannelException {
        SocketChannel clientCh = (SocketChannel) key.channel();
        ByteBuffer buf = (ByteBuffer) key.attachment();
        buf.clear();

        clientCh.register(key.selector(), SelectionKey.OP_WRITE, buf);
    }

    /**
     *
     * @param key
     * @throws IOException
     */
    private void readAnswer(SelectionKey key) throws IOException {
        SocketChannel clientCh = (SocketChannel) key.channel();
        ByteBuffer buf = (ByteBuffer) key.attachment();

        while(clientCh.read(buf) > 0);
        buf.flip();
        StringBuilder sbuilder = new StringBuilder();

        while(buf.hasRemaining())
            sbuilder.append((char) buf.get());

        System.out.println("[Client received]" + sbuilder.toString());
    }

    /**
     *
     * @param key
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
     *
     * @param key
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
