package com;

import com.sun.jdi.connect.TransportTimeoutException;

import java.io.IOException;
import java.net.Socket;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;

public class ServerWorker implements Runnable {

    private final static int BUF_SIZE = 2048;
    private Selector selector;
    private ServerManagerWT server;

    public ServerWorker(Selector sel, ServerManagerWT server) {
        this.selector = sel;
        this.server = server;
    }

    public void run() {

        if(ClientServerProtocol.DEBUG()) {
            System.out.println("Worker creato, sto per runnare: " + Thread.currentThread().getName());
        }

        while(!this.selector.keys().isEmpty()) {
            try {
                // @todo change timeuout to constant
                if(this.selector.select(2000) == 0)
                    continue;

                // check selected keys
                Set<SelectionKey> selectedSet = this.selector.selectedKeys();
                Iterator<SelectionKey> it = selectedSet.iterator();

                while(it.hasNext()) {
                    SelectionKey key = it.next();
                    it.remove();

                    if(key.isReadable()) {
                        // try yo read client request
                        try { readMsgAndComputeRequest(key); }
                        catch (BufferUnderflowException bue) {
                            System.out.println("Non ci sono 4 byte da leggere nel " +
                                    "buffer per comporre un «int»");}
                        catch (TerminationException e) {
                            key.cancel();
                            System.err.println("Connessione con 1 client terminata!");
                            //break outer_loop;
                            break;
                            // per avere un comportamento while(true) usare «break;»
                        }
                        catch (IOException e) { System.err.println("errore durante la " +
                                                "lettura di una richiesta dal client"); }

                    }
                    if(key.isWritable()) {
                        // answer client request
                    }
                }
            }
            catch (IOException e)           { e.printStackTrace(); }
        }
    }

    public void readMsgAndComputeRequest(SelectionKey key)
            throws IOException, BufferUnderflowException, TerminationException {
        SocketChannel cliCh     = (SocketChannel)   key.channel();
        ByteBuffer buf          = (ByteBuffer)      key.attachment();


        // non-blocking read, writing into buffer
        cliCh.read(buf);
        // since i want to read the buffer, "flip" it to reading mode
        buf.flip();
        // get the operation code to distinguish the operation
        StringBuilder sbuilder = new StringBuilder();

        while(buf.hasRemaining()) {
            char charRead = (char) buf.get();
            if(charRead == ';')     // delimiter
                break;
            sbuilder.append(charRead);
        }

        ClientServerOperations cso = ClientServerOperations.valueOf(sbuilder.toString());
        switch(cso) {
            case LOGIN:
                opLogin(buf);
                break;

            case LOGOUT:
                throw new TerminationException();

            default:
                break;

        }

        /*
        // aggiunta header al msg, costruisco la stringa da inviare
        StringBuilder sbuilder = new StringBuilder();

        // (2) lettura dal buffer
        while(buf.hasRemaining())
            sbuilder.append((char) buf.get());

        String receivedString = sbuilder.toString();
        System.out.println("\n[Server received] " + receivedString);

        */
        /*
        String answerToSend = ECHO_STRING + " " + receivedString;
        System.out.println("[Server is sending] " + answerToSend);

        byte[] answerInBytes = answerToSend.getBytes();
        buf.clear();
        // (3) scrittura sul buffer
        buf.put(answerInBytes);
        buf.flip();

        clientCh.register(key.selector(), SelectionKey.OP_WRITE, buf);

         */
    }

    /*
    protocol for LOGIN operation:
        -   LOGIN_OP_CODE: int, 4 bytes -> già letto
        -   length username: int 4 bytes
        -   username: string [variable] bytes
        -   length password: in 4 bytes
        -   password: byte[]
     */
    public void opLogin(ByteBuffer buf) {
        StringBuilder sbuilder = new StringBuilder();
        while(buf.hasRemaining()) {
            char charRead = (char) buf.get();
            if(charRead == ';')
                break;
            sbuilder.append(charRead);
        }

        String username = sbuilder.toString();
        byte[] psw = new byte[ ( buf.limit() - buf.position() ) ];

        buf.get(psw, buf.position(), buf.limit());

        this.server.register(username, psw);
    }

    /*
    protocol for LOGOUT operation:
        -   LOGOUT;username
     */
    public void opLogout(ByteBuffer buf) {
        StringBuilder sbuilder = new StringBuilder();
        while(buf.hasRemaining()) {
            sbuilder.append((char) buf.get());
        }

        String username = sbuilder.toString();

        this.server.logout(username);
    }
}
