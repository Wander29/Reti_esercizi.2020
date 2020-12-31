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

public class ServerWorker implements Runnable {

    private Selector selector;

    public ServerWorker(Selector s) {
        this.selector = s;
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

    public void readMsgAndComputeRequest(SelectionKey key) throws IOException, TerminationException {
        SocketChannel cliCh     = (SocketChannel)   key.channel();
        ByteBuffer buf          = (ByteBuffer)      key.attachment();


        // (1) non-blocking read, writing into buffer
        cliCh.read(buf);
        // since i want to read the buffer, i "flip" it to reading mode
        buf.flip();

        int opCode = -1;
        try {
            opCode = buf.getInt();
        } catch (BufferUnderflowException bue) {
            System.out.println("Non ci sono 4 byte da leggere nel buffer per comporre un «int»");
        }
        /*
        // aggiunta header al msg, costruisco la stringa da inviare
        StringBuilder sbuilder = new StringBuilder();

        // (2) lettura dal buffer
        while(buf.hasRemaining())
            sbuilder.append((char) buf.get());

        String receivedString = sbuilder.toString();
        System.out.println("\n[Server received] " + receivedString);

        // controllo Terminazione, secondo il protocollo
        if(receivedString.equals("LOGOUT"))
           throw new TerminationException();
        */
        if(opCode == ClientServerOperations.LOGOUT.OP_CODE)
            throw new TerminationException();
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
}
