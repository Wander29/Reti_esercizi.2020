package server.logic;

import protocol.CSOperations;
import protocol.CSProtocol;
import utils.TCPBuffersNIO;
import utils.TerminationException;

import java.io.IOException;
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

        CSOperations cso = CSOperations.valueOf(sbuilder.toString());
        switch(cso) {
            case LOGIN:
                opLogin(bufs, cliCh);
                cliCh.register(key.selector(), SelectionKey.OP_READ, bufs);
                break;

            case CREATE_PROJECT:
                opCreateProject(bufs, cliCh);
                cliCh.register(key.selector(), SelectionKey.OP_READ, bufs);
                break;

            case LOGOUT:
                opLogout(bufs, cliCh);
                throw new TerminationException();

            case EXIT:
                throw new TerminationException();

            default:
                break;

        }
    }

    public void run() {

        if(CSProtocol.DEBUG()) {
            System.out.println("Worker creato, sto per runnare: " + Thread.currentThread().getName());
        }

        while(!this.selector.keys().isEmpty()) {
        // while(true) {
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
                            // break outer_loop;
                            // per avere un comportamento while(true) usare «break;»
                            break;
                        }
                        catch (IOException e) { System.err.println("errore durante la " +
                                                "lettura di una richiesta dal client"); }

                    }
                    // if(key.isWritable()) {}

                } //; end while(it.hasNExt())
                System.out.println("[SERVER WORKER] sto terminando");
            }
            catch (IOException e)           { e.printStackTrace(); }
        }
    }

    /*
    protocol for CREATE_PROJECT operation
        - CREATE_PROJECT;username;projectName
     */
    private void opCreateProject(TCPBuffersNIO bufs, SocketChannel cliCh) throws IOException {
        List<String> tokens = tokenizeRequest(bufs.inputBuf);
        bufs.inputBuf.clear();

        String username     = tokens.remove(0);
        String projectName  = tokens.remove(0);

        String ret = this.server.createProject(username, projectName);
        this.sendResponse(bufs.outputBuf, cliCh, ret);
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

        String ret = this.server.login(username, psw);
        sendResponse(bufs.outputBuf, cliCh, ret);
    }

    /*
    protocol for LOGOUT operation:
        -   LOGOUT;username
     */
    private void opLogout(TCPBuffersNIO bufs, SocketChannel cliCh) throws IOException {

        List<String> tokens = tokenizeRequest(bufs.inputBuf);
        bufs.inputBuf.clear();

        String username = tokens.remove(0);

        String ret = this.server.logout(username);
        sendResponse(bufs.outputBuf, cliCh, ret);
    }

    /**
     * UTILS
     */
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

    private void sendResponse(ByteBuffer buf, SocketChannel channel, String toSend)
            throws IOException
    {
        buf.clear();

        String s = toSend + "\n";
        byte[] tmp = s.getBytes();
        buf.put(tmp, 0, tmp.length);

        buf.flip();

        channel.write(buf);
        buf.clear();
    }
}
