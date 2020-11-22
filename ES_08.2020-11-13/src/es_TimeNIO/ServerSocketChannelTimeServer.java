package es_TimeNIO;

import java.io.*; import java.nio.channels.*; import java.net.*;
import java.util.*; import java.nio.*;
public class ServerSocketChannelTimeServer {
    public static void main(String[] args) throws Exception {
        System.out.println("Time Server started");
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().bind(new InetSocketAddress(5000));
        serverSocketChannel.configureBlocking(false);
        while (true) {
            System.out.println("Waiting for request ...");
            SocketChannel socketChannel = serverSocketChannel.accept();
            if (socketChannel != null) {
                String dateAndTimeMessage = "Date: " + new
                        Date(System.currentTimeMillis());
                ByteBuffer buf = ByteBuffer.allocate(64);
                buf.put(dateAndTimeMessage.getBytes());
                buf.flip();
                while (buf.hasRemaining()) {
                    socketChannel.write(buf);
                }
                System.out.println("Sent: " + dateAndTimeMessage);
                Thread.sleep(10000);
            } else {
                System.out.println("nessuna connessione rilevata");
                Thread.sleep(1000);
            }
        }
    }
}
