package client.model;

import utils.StringUtils;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class ChatManager extends Thread {

    private final Pipe pipe;
    private final Pipe.SourceChannel pipeReadChannel;
    private final Selector selector;

    public ChatManager(Pipe pipe) throws IOException {
        this.pipe = pipe;

        this.pipeReadChannel = this.pipe.source();
        this.selector = Selector.open();
    }

    public void run() {
        System.out.println("[THREAD CHAT-MANAGER] avviato");


        try {
            this.pipeReadChannel.configureBlocking(false);
            this.pipeReadChannel.register(this.selector, SelectionKey.OP_READ);

            while(true) {
                if(this.selector.select() == 0)
                    continue;

                Set<SelectionKey> selectedKeys = this.selector.selectedKeys();
                Iterator<SelectionKey> it = selectedKeys.iterator();

                while(it.hasNext()) {
                    SelectionKey key = it.next();
                    it.remove();

                    if(key.isReadable()) {
                        // try first reading from pipe
                        if(key.channel() instanceof Pipe.SourceChannel) {
                            System.out.println("sto per leggere");
                            ByteBuffer bb = ByteBuffer.allocate(512);
                            try {
                                System.out.println("prima");
                                while (pipeReadChannel.read(bb) > 0) {
                                    System.out.println("dopo");
                                    bb.flip();

                                    String ret = StringUtils.byteToString(bb);
                                    System.out.println(ret);
                                    List<String> tokens = StringUtils.tokenizeRequest(ret);
                                    bb.clear();

                                    String ip = tokens.remove(0);
                                    int port = Integer.parseInt(tokens.remove(0));

                                    registerMulticastConnection(ip, port);
                                }
                            } catch (IOException e) { e.printStackTrace(); }
                        }
                        // else it's a datagramSocketChannel
                        else {
                            System.out.println("ELSE");
                            DatagramChannel udpChannel = (DatagramChannel) key.channel() ;
                            ByteBuffer buffer = ByteBuffer.allocate(8192);
                            System.out.println("before receiving");
                            while (udpChannel.receive(buffer)==null);
                            buffer.flip();
                            System.out.println(StringUtils.byteToString(buffer));
                            buffer.clear();
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void registerMulticastConnection(String ip, int port)
            throws UnknownHostException, IOException {
        // establishes connection then registers to selector
        InetAddress multicast_address;
        System.out.println("start registerMulticast");
        multicast_address = InetAddress.getByName(ip);

        if (!multicast_address.isMulticastAddress())
            throw new IllegalArgumentException();

        DatagramChannel udpChannel = DatagramChannel.open();
        System.out.println("udpChannel aperto");
        udpChannel.configureBlocking(false);
        // this?

        try {
            udpChannel.socket().setReuseAddress(true);
        }
        catch (SocketException se) { se.printStackTrace(); }
        udpChannel.socket().bind(new InetSocketAddress(port));

        System.out.println("bindato");
        // or this?
        NetworkInterface ni;
        try (MulticastSocket s = new MulticastSocket()) {
            ni = s.getNetworkInterface();
        }
        MembershipKey key = udpChannel.join(multicast_address, ni);
        System.out.println("joinato");

        udpChannel.register(this.selector, SelectionKey.OP_READ);
        System.out.println("registrato");
    }
}
