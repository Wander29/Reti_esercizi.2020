package com;

import com.sun.jdi.connect.TransportTimeoutException;

import java.io.IOException;
import java.net.Socket;
import java.nio.Buffer;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.*;

public class ServerWorker implements Runnable {

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

        // while(!this.selector.keys().isEmpty()) {
        while(true) {
            try {
                // @todo change timeuout to constant
                if(this.selector.select(5000) == 0)
                    continue;

                // check selected keys
                Set<SelectionKey> selectedSet = this.selector.selectedKeys();
                Iterator<SelectionKey> it = selectedSet.iterator();

                while(it.hasNext()) {
                    SelectionKey key = it.next();
                    it.remove();

                    if(key.isReadable()) {
                        try {
                            // try yo read client request
                            readMsgAndComputeRequest(key);
                        }
                        catch (BufferUnderflowException bue) {
                            System.err.println("Non ci sono 4 byte da leggere nel " +
                                    "buffer per comporre un «int»"); }
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

    private void readMsgAndComputeRequest(SelectionKey key)
            throws IOException, BufferUnderflowException, TerminationException {
        SocketChannel cliCh     = (SocketChannel)   key.channel();
        TCPBuffersNIO bufs      = (TCPBuffersNIO)   key.attachment();


        // non-blocking read, writing into buffer
        cliCh.read(bufs.inputBuf);
        // since i want to read the buffer, "flip" it to reading mode
        bufs.inputBuf.flip();
        // get the operation code to distinguish the operation
        StringBuilder sbuilder = new StringBuilder();

        while(bufs.inputBuf.hasRemaining()) {
            char charRead = (char) bufs.inputBuf.get();
            if(charRead == ';')     // delimiter
                break;
            sbuilder.append(charRead);
        }

        ClientServerOperations cso = ClientServerOperations.valueOf(sbuilder.toString());
        switch(cso) {
            case LOGIN:
                opLogin(bufs, cliCh);
                cliCh.register(key.selector(), SelectionKey.OP_READ, bufs);
                break;

            case CREATE_PROJECT:
                opCreateProject(bufs, cliCh);
                cliCh.register(key.selector(), SelectionKey.OP_READ, bufs);

            case LOGOUT:
                opLogout(bufs, cliCh);
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

    private void opCreateProject(TCPBuffersNIO bufs, SocketChannel cliCH) throws IOException {
        List<String> tokens = tokenizeRequest(bufs.inputBuf);
        String projectName  = tokens.remove(0);

        CSReturnValues ret = this.server.createProject();
    }

    private void sendResponse(ByteBuffer buf, SocketChannel channel, String toSend) throws IOException {
        buf.clear();

        String s = toSend + "\n";
        byte[] tmp = s.getBytes();
        buf.put(tmp, 0, tmp.length);

        buf.flip();

        channel.write(buf);
        buf.clear();
    }


    /*
        tokenize request string
     */
    private List<String> tokenizeRequest(ByteBuffer buf) {
        StringBuilder sbuilder = new StringBuilder();

        while(buf.hasRemaining()) {
            char charRead = (char) buf.get();
            sbuilder.append(charRead);
        }
        String received = sbuilder.toString();

        ArrayList<String> tokens = new ArrayList<>();
        StringTokenizer tokenizer = new StringTokenizer(received, ";");

        while(tokenizer.hasMoreTokens()) {
            tokens.add(tokenizer.nextToken());
        }

        return tokens;
    }

    /*
    protocol for LOGIN operation:
        -   LOGIN;username;pswInBytes
     */
    private void opLogin(TCPBuffersNIO bufs, SocketChannel cliCh) throws IOException {

        List<String> tokens = tokenizeRequest(bufs.inputBuf);
        bufs.inputBuf.clear();

        String username = tokens.remove(0);
        String psw = tokens.remove(0);

        CSReturnValues ret = this.server.login(username, psw);
        sendResponse(bufs.outputBuf, cliCh, ret.toString());
    }

    /*
    protocol for LOGOUT operation:
        -   LOGOUT;username
     */
    private void opLogout(TCPBuffersNIO bufs, SocketChannel cliCh) throws IOException {

        StringBuilder sbuilder = new StringBuilder();
        while(bufs.inputBuf.hasRemaining()) {
            sbuilder.append((char) bufs.inputBuf.get());
        }
        String username = sbuilder.toString();
        bufs.inputBuf.clear();

        CSReturnValues ret = this.server.logout(username);
        sendResponse(bufs.outputBuf, cliCh, ret.toString());
    }
}
