package server.logic;

import protocol.CSOperations;
import protocol.CSProtocol;
import protocol.CSReturnValues;
import utils.StringUtils;
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
        String operationString = sbuilder.toString();
        System.out.println(operationString);
        CSOperations cso = CSOperations.valueOf(operationString);
        switch(cso) {
            case LOGIN:
                opLogin(bufs, cliCh);
                break;

            case CREATE_PROJECT:
                opCreateProject(bufs, cliCh);
                break;

            case LIST_PROJECTS:
                opListProject(bufs, cliCh);
                break;

            case LOGOUT:
                opLogout(bufs, cliCh);
                throw new TerminationException();

            case EXIT:
                throw new TerminationException();

            default:
                break;

        }
        cliCh.register(key.selector(), SelectionKey.OP_READ, bufs);
    }

    public void run() {

        if(CSProtocol.DEBUG()) {
            System.out.println("Worker creato, sto per runnare: " + Thread.currentThread().getName());
        }

        //while(!this.selector.keys().isEmpty()) {
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
                            // break outer_loop;
                            // per avere un comportamento while(true) usare «break;»
                            break;
                        }
                        catch (IOException e) { System.err.println("errore durante la " +
                                                "lettura di una richiesta dal client"); }

                    }
                    // if(key.isWritable()) {}

                } //; end while(it.hasNExt())

            }
            catch (IOException e)           { e.printStackTrace(); }
        }
        //System.out.println("[SERVER WORKER] sto terminando");
    }

    /*
        -LIST_PROJECTS
     */
    private void opListProject(TCPBuffersNIO bufs, SocketChannel cliCh) throws IOException {
        bufs.inputBuf.clear();

        Set<String> list = this.server.listProjects();

        StringBuilder builder = new StringBuilder(CSReturnValues.LIST_PROJECTS_OK.toString());
        for(String s : list) {
            builder.append(";");
            System.out.println(s);
            builder.append(s);
        }
        String ret = builder.toString();
        if(CSProtocol.DEBUG()) {
            System.out.println("response: " + ret);
        }
        this.sendResponse(bufs.outputBuf, cliCh, ret);
    }

    /*
    protocol for CREATE_PROJECT operation
        - CREATE_PROJECT;projectName
     */
    private void opCreateProject(TCPBuffersNIO bufs, SocketChannel cliCh) throws IOException {
        List<String> tokens = StringUtils.tokenizeRequest(bufs.inputBuf);
        bufs.inputBuf.clear();

        String projectName  = tokens.remove(0);

        String ret = this.server.createProject(bufs.getUsername(), projectName);
        this.sendResponse(bufs.outputBuf, cliCh, ret);
    }

    /*
    protocol for LOGIN operation:
        -   LOGIN;username;pswInBytes
     */
    private void opLogin(TCPBuffersNIO bufs, SocketChannel cliCh) throws IOException {

        List<String> tokens = StringUtils.tokenizeRequest(bufs.inputBuf);
        bufs.inputBuf.clear();

        String username = tokens.remove(0);
        String psw = tokens.remove(0);

        String ret = this.server.login(username, psw);
        if(CSReturnValues.valueOf(ret) == CSReturnValues.LOGIN_OK) {
            bufs.setUsername(username);
        }

        sendResponse(bufs.outputBuf, cliCh, ret);
    }

    /*
    protocol for LOGOUT operation:
        -   LOGOUT
     */
    private void opLogout(TCPBuffersNIO bufs, SocketChannel cliCh) throws IOException {
        bufs.inputBuf.clear();

        String ret = this.server.logout(bufs.getUsername());
        sendResponse(bufs.outputBuf, cliCh, ret);
    }

    /**
     * UTILS
     */
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
