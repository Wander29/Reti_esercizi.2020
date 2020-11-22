package com;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

/**
 1. Opzione più semplice:
 come primo esercizio potete sviluppare un programma in cui
 quando la serverSocketChannel ha connessioni da accettare
 (key.isAcceptable() è vera) il server scrive subito sulla socketChannel
 restituita dall'operazione di accept() e chiude la connessione.

 2. Opzione più completa (ma un po' più complicata - vedi esempio
 IntGenServer sulle slide):
 Se key.isAcceptable() è verificata, la socketChannel restituita
 dall'operazione di acceptistrata sul selettore (con
 interesse all'operazione di WRITE) e il messaggio viene inviato
 quando il canale è pronto per la scrittura (key.isW viene regritable è true).
 */

public class ServerHello extends Thread {

    private final int serverPort;
    private static final String ANS_STRING = "Hello Client\n";
    private static final byte[] ANS_BYTES = ANS_STRING.getBytes();

    public ServerHello(int port) {
        this.serverPort = port;
    }

    public void run() {
        try (   // apertura ServerSocket e relativo channel
                ServerSocketChannel ssCh = ServerSocketChannel.open();
            ){
            ServerSocket ss = ssCh.socket();
            ss.bind(new InetSocketAddress(this.serverPort));
            ssCh.configureBlocking(false); // non-blocking Channel

            // selettore
            Selector selector = Selector.open();
            ssCh.register(selector, SelectionKey.OP_ACCEPT);

            // vita del server
            while(true) {
                if (selector.select() == 0)
                    continue;

                // una volta sbloccato: controlliamo le chiavi!
                Set<SelectionKey> selectedSet = selector.selectedKeys();
                Iterator<SelectionKey> keyIterator = selectedSet.iterator();

                while (keyIterator.hasNext()) {
                    SelectionKey key = keyIterator.next();
                    keyIterator.remove(); // rimuove la chiave dal selectedSet

                    if (key.isAcceptable()) {
                        registerWrite(key);
                    }
                    if (key.isWritable()) {
                        writeAnswer(key);

                        key.cancel();
                        break;
                    }
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // System.out.println("[MainServer] terminazione corretta");
    }

    /**
     *
     * @param key       chiave con canale pronto per l'accept()
     * @throws IOException
     */
    private void registerWrite(SelectionKey key) throws IOException {
        // accetto la nuova connessione
        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
        SocketChannel clientChannel = serverChannel.accept();

        clientChannel.configureBlocking(false);

        // allego alla chiave il buffer con la frase da inviare
        ByteBuffer buf = ByteBuffer.wrap(ANS_BYTES);

        // mi registro per la scrittura
        clientChannel.register(key.selector(), SelectionKey.OP_WRITE, buf);
    }

    /**
     *scrive HelloClient sul canale di comunicazione associato alla chiave
     * @param key       chiave selezionata con canale pronto per la scrittura
     * @throws IOException
     */
    private void writeAnswer(SelectionKey key) throws IOException {
        SocketChannel clientChannel = (SocketChannel) key.channel();

       ByteBuffer buf = (ByteBuffer) key.attachment();

        while (buf.hasRemaining())
            clientChannel.write(buf);

        System.out.println("Messaggio inviato al client: " + clientChannel.getRemoteAddress());
        clientChannel.close();
    }
}
