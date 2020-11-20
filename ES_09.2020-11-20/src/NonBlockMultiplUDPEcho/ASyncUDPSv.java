package NonBlockMultiplUDPEcho;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.*;
import java.nio.charset.*;

public class ASyncUDPSv {
    static int BUF_SZ = 1024;
    class Con {
        ByteBuffer req;
        ByteBuffer resp;
        SocketAddress sa;
        public Con() {
            req = ByteBuffer.allocate(BUF_SZ);
        }}
    static int port = 8340;

    private void process() {
        try {
            /********************************************************
             * Selettore -> Multioplexing
             * DatagramChannel -> UDP with NIO, non bloccante
             */
            Selector selector = Selector.open();
            DatagramChannel channel = DatagramChannel.open();

            InetSocketAddress isa = new InetSocketAddress(port);
            channel.socket().bind(isa);
            channel.configureBlocking(false);

            // registra la chiave come da LEGGERE
            SelectionKey clientKey = channel.register(selector, SelectionKey.OP_READ);
            clientKey.attach(new Con());
            System.out.println("Server Pronto");
            while (true) {
                try {
                    selector.select();
                    Iterator selectedKeys = selector.selectedKeys().iterator();
                    // itero su tutte le chiavi selezionate
                    while (selectedKeys.hasNext()) {
                        try {
                            SelectionKey key = (SelectionKey) selectedKeys.next();
                            selectedKeys.remove(); // rimuove

                            if (!key.isValid()) {
                                continue;
                            }
                            if (key.isReadable()) {
                                read(key);
                                key.interestOps(SelectionKey.OP_WRITE);
                            } else if (key.isWritable()) {
                                write(key);
                                key.interestOps(SelectionKey.OP_READ);
                            }
                        } catch (IOException e) {
                            System.out.println(e);
                        }
                    }
                } catch (IOException e) {
                    System.out.println(e);
                }
            }
        } catch (IOException e) {

        }
    }

    private void read(SelectionKey key) throws IOException {
        DatagramChannel chan = (DatagramChannel) key.channel();
        Con con = (Con) key.attachment();
        con.sa = chan.receive(con.req);
        con.req.flip();
        System.out.println(new String(con.req.array(), "UTF-8"));
        con.resp = con.req;
    }

    private void write(SelectionKey key) throws IOException {
        DatagramChannel chan =(DatagramChannel)key.channel();
        Con con = (Con) key.attachment();
        chan.send(con.resp, con.sa);
        con.req.clear();
    }

    static public void main(String[] args) {
        ASyncUDPSv svr = new ASyncUDPSv();
        svr.process();
    }
}


