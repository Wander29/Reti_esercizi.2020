package client.model;

import client.data.DbHandler;
import protocol.CSOperations;
import protocol.CSProtocol;
import protocol.CSReturnValues;
import protocol.exceptions.TerminationException;
import utils.StringUtils;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.sql.SQLException;
import java.sql.Time;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/*
    this thread will listen on PIPE from
         - UserSceneController which will write
                into the pipe every time user is added to a project and
                for every project the user is member to
         - EVERY datagramChannel for UDP Multicast Chats
 */

public class ChatManager extends Thread {

    private final Pipe pipe;
    private final Selector selector;
    private DbHandler dbHandler = null; // used to store and retrieve chats

    public ChatManager(Pipe pipe, String username) throws IOException {
        this.pipe = pipe;
        this.selector = Selector.open();

        // register the sourceChannel (aka read channel) of pipe into selector
        Pipe.SourceChannel pipeReadChannel = this.pipe.source();
        pipeReadChannel.configureBlocking(false); // set non-blocking
        ByteBuffer bb = ByteBuffer.allocate(CSProtocol.BUF_SIZE_CLIENT());

        pipeReadChannel.register(this.selector, SelectionKey.OP_READ, bb);

        try {
            this.dbHandler = new DbHandler(username);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void run() {
        System.out.println("[THREAD CHAT-MANAGER] avviato");

        try {
            while(true) {
                if(this.selector.select() == 0)
                    continue;

                Set<SelectionKey> selectedKeys = this.selector.selectedKeys();
                Iterator<SelectionKey> it = selectedKeys.iterator();

                while(it.hasNext()) {
                    SelectionKey key = it.next();
                    it.remove();

                    if(key.isReadable()) {
                            // first trying to read from pipe
                        if(key.channel() instanceof Pipe.SourceChannel) {
                            try {
                                readFromPipeNewMulticast(key);
                            } catch (IOException e) { e.printStackTrace(); }
                        }
                        else { // it's a datagramSocketChannel
                            try {
                                readChatMsg(key);
                            }
                            catch (SQLException t)  { t.printStackTrace(); }
                            catch (IOException e)   { e.printStackTrace(); }
                            catch (TerminationException e) {
                                System.out.println("Chat Terminata, chiusura Socket");
                                DatagramChannel udpChannel = (DatagramChannel) key.channel();
                                udpChannel.socket().close();

                                key.cancel();
                            }
                        }
                    }
                    key.channel().register(this.selector, SelectionKey.OP_READ, key.attachment());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
    read from messages of this form:
        IP;port, for example 239.21.21.21:9999

    then calls function to register as new Multicas connection
     */
    private void readFromPipeNewMulticast(SelectionKey key) throws IOException {
        Pipe.SourceChannel pipeReadEnd = (Pipe.SourceChannel) key.channel();
        ByteBuffer bb = (ByteBuffer) key.attachment();

        StringBuilder builder = new StringBuilder();
        while (pipeReadEnd.read(bb) > 0) {
            bb.flip();

            String ret = StringUtils.byteBufferToString(bb);
            bb.clear();

            builder.append(ret);
        }
        List<String> tokens = StringUtils.tokenizeRequest(builder.toString());

        if(tokens.size() != 2)
            throw new IOException();

        String ip = tokens.remove(0);
        int port = Integer.parseInt(tokens.remove(0));

        registerMulticastConnection(ip, port);
    }

    /*
        given IP and PORT of a Multicast communication
            create a socket to listen on multicast messages
            and registers it to selector
     */
    private void registerMulticastConnection(String ip, int port)
            throws UnknownHostException, IOException, SocketException
    {
        // establishes connection
        InetAddress multicastAddress;
        multicastAddress = InetAddress.getByName(ip);

        if (!multicastAddress.isMulticastAddress())
            throw new IllegalArgumentException();

        // UDP channel
        DatagramChannel udpChannel = DatagramChannel.open();
        udpChannel.configureBlocking(false); // non-blocking
            // reusable in order to allow more connections from same JVM
        udpChannel.socket().setReuseAddress(true);

        // binds channel to a Socket on specific port
        udpChannel.socket().bind(new InetSocketAddress(port));
        // needed to have Multicast NIO
            // see: @https://stackoverflow.com/questions/59718752/java-nio-join-to-multicast-channel-on-the-default-network-interface
        NetworkInterface ni;
        try (MulticastSocket s = new MulticastSocket()) {
            ni = s.getNetworkInterface();
        }
        // join on Multicast group
        MembershipKey key = udpChannel.join(multicastAddress, ni);

        // registers to selector
        ByteBuffer buf = ByteBuffer.allocate(CSProtocol.BUF_SIZE_CLIENT());
        udpChannel.register(this.selector, SelectionKey.OP_READ, buf);
    }

    /*
        reads a chat message from UDP channel and stores it into local DB
       CHAT_MSG;username;project;timeSent;msg
    */
    private void readChatMsg(SelectionKey key) throws IOException, SQLException, TerminationException {
        DatagramChannel udpChannel = (DatagramChannel) key.channel();
        ByteBuffer buf = (ByteBuffer) key.attachment();

        // read message
        buf.clear();
        while (udpChannel.receive(buf) == null) { }

        // translate into String
        buf.flip();
        String udpMessageRead = StringUtils.byteBufferToString(buf);
        buf.clear();

        System.out.println("UDP CHAT MSG: " + udpMessageRead);
        List<String> tokens = StringUtils.tokenizeRequest(udpMessageRead);

        // analyze it
        String firstToken = tokens.remove(0); // throw header
        if(firstToken.equals(CSReturnValues.CHAT_STOP.toString()))
            throw new TerminationException();

        String username = tokens.remove(0);
        String project = tokens.remove(0);
        String timeSentString = tokens.remove(0);
        Long timeSent = Long.parseLong(timeSentString);
        String msg = tokens.remove(0);

        // save into local DB
        this.dbHandler.saveChat(username, project, timeSent, msg);
    }
}
