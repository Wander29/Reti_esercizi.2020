package es_TimeNIO;

import java.nio.channels.*; import java.net.*;
import java.io.*; import java.nio.*;
public class SocketChannelTimeClient {
    public static void main(String[] args) throws Exception {
        SocketAddress address = new InetSocketAddress("127.0.0.1", 5000);
        SocketChannel socketChannel;
        socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        socketChannel.connect(address);
        System.out.println(socketChannel.finishConnect());
        while(!socketChannel.finishConnect()){
//wait, or do something else...
            System.out.println("Non terminata la connessione");
        }
        System.out.println("Terminata la fase di instaurazione della connessione");
    }
}