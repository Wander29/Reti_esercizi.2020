package com;

/*
Scrivere un programma JAVA che implementa un server TCP che apre una listening socket su una porta e
resta in attesa di richieste di connessione.

    Quando arriva una richiesta di connessione, il server accetta la connessione, trasferisce
    al client un messaggio ("HelloClient") e poi chiude la connessione.
    Usare multiplexed NIO (canali non bloccanti e il selettore, e ovviamente i buffer di tipo
    ByteBuffer).
    Per il client potete usare un client telnet.
 */

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

public class Main {

    public static void main(String[] args) {

        final int port = 9999;

        ServerSocketChannel serverChannel;
        Selector selector;
        try {
            serverChannel = ServerSocketChannel.open();
            ServerSocket ss = serverChannel.socket();
            InetSocketAddress address = new InetSocketAddress(port);
            ss.bind(address);
            serverChannel.configureBlocking(false);
            selector = Selector.open();
            serverChannel.register(selector, SelectionKey.OP_ACCEPT); }
        catch (IOException ex) {
            ex.printStackTrace();
            return;
        }

        while (true) {

            try {
                if(selector.select() == 0)
                    continue;
            } catch (IOException ex) {
                ex.printStackTrace();
                break;
            }
            Set<SelectionKey> readyKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = readyKeys.iterator();

            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();
// rimuove la chiave dal Selected Set, ma non dal registered Set
                try {
                    if (key.isAcceptable()) {
                        ServerSocketChannel server = (ServerSocketChannel) key.channel();
                        SocketChannel client = server.accept();
                        System.out.println("Accepted connection from " + client);
                        client.configureBlocking(false);
                        SelectionKey key2 = client.register(selector,
                                SelectionKey.OP_WRITE);
                        ByteBuffer output = ByteBuffer.allocate(4096);
                        key2.attach(output);
                    } else if (key.isWritable()) {
                        SocketChannel client = (SocketChannel) key.channel();
                        ByteBuffer output = (ByteBuffer) key.attachment();
                        String msg = "HelloClient";
                        output.put(msg.getBytes());
                        output.flip();
                        client.write(output);
                    }
                } catch (IOException ex) {
                    key.cancel();
                    try {
                        key.channel().close();
                    } catch (IOException cex) {
                    }
                }
            }
        }
    }
}
