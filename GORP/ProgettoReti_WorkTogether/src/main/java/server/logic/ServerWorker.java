package server.logic;

import com.google.gson.Gson;
import protocol.CSOperations;
import protocol.CSProtocol;
import protocol.CSReturnValues;
import protocol.classes.ListProjectEntry;
import protocol.classes.TCPBuffersNIO;
import utils.*;
import protocol.exceptions.IllegalProjectException;
import protocol.exceptions.IllegalUsernameException;
import protocol.exceptions.TerminationException;

import java.io.IOException;
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
            throws IOException, TerminationException
    {
        SocketChannel cliCh     = (SocketChannel)   key.channel();
        TCPBuffersNIO bufs      = (TCPBuffersNIO)   key.attachment();

        // non-blocking read, writing into buffer
        cliCh.read(bufs.inputBuf);

        // since i want to read the buffer, "flip" it to reading mode
        bufs.inputBuf.flip();

        // gets the operation code to distinguish the operation
        StringBuilder sbuilder = new StringBuilder();

        while(bufs.inputBuf.hasRemaining()) {
            char charRead = (char) bufs.inputBuf.get();
            if(charRead == ';')     // delimiter
                break;
            sbuilder.append(charRead);
        }

        String operationString = sbuilder.toString();
        CSOperations cso = CSOperations.valueOf(operationString);

        // switch between operations
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

            case SHOW_MEMBERS:
                opShowMembers(bufs, cliCh);
                break;

            case ADD_MEMBER:
                opAddMember(bufs, cliCh);
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
            System.out.println("Worker creato, sta per essere avviato: " + Thread.currentThread().getName());
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
                        catch (TerminationException e) {
                            key.cancel();
                            System.err.println("Connessione con 1 client terminata!");
                                // break outer_loop;
                                // per avere un comportamento while(true) usare «break;»
                            break;
                        }
                        catch (IOException e) {
                            System.err.println("errore durante la lettura di una richiesta dal client");
                            e.printStackTrace();
                        }

                    }
                    // if(key.isWritable()) {}

                } //; end while(it.hasNExt())

            }
            catch (IOException e)           { e.printStackTrace(); }
        }
        //System.out.println("[SERVER WORKER] sto terminando");
    }


    /**
     * - OPERATIONS -
     *     case LOGIN:
     *     case CREATE_PROJECT:
     *     case LIST_PROJECTS:
     *     case SHOW_MEMBERS:
     *     case LOGOUT:
     */

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
        -LIST_PROJECTS
    */
    private void opListProject(TCPBuffersNIO bufs, SocketChannel cliCh) throws IOException {
        bufs.inputBuf.clear();

        List<ListProjectEntry> list = null;
        try {
            list = this.server.listProjects(bufs.getUsername());
        } catch (IllegalUsernameException e) {
            this.sendResponse(bufs.outputBuf, cliCh, e.retval.toString());
        }
        /*
        StringBuilder builder = new StringBuilder(CSReturnValues.LIST_PROJECTS_OK.toString());
        for(ListProjectEntry proj : list) {
            builder.append(";");
            builder.append(proj.project);
            builder.append(";");
            builder.append(proj.ip);
            builder.append(";");
            builder.append(String.valueOf(proj.port));
        }
        String ret = builder.toString();

        if(CSProtocol.DEBUG()) {
            System.out.println("response: " + ret);
        }*/

        Gson gson = new Gson();
        String toSend = CSReturnValues.LIST_PROJECTS_OK.toString() + ";" + gson.toJson(list);

        this.sendResponse(bufs.outputBuf, cliCh, toSend);
    }

    /*
    protocol for SHOW MEMBERS:
        - SHOW_MEMBERS;projectName
     */
    private void opShowMembers(TCPBuffersNIO bufs, SocketChannel cliCh) throws IOException {
        List<String> tokens = StringUtils.tokenizeRequest(bufs.inputBuf);
        bufs.inputBuf.clear();

        String projectName = tokens.remove(0);
        StringBuilder builder = new StringBuilder();
        String ret = null;

        try {
            List<String> projects = this.server.showMembers(bufs.getUsername(), projectName);

            builder.append(CSReturnValues.SHOW_MEMBERS_OK.toString());
            for(String s : projects) {
                builder.append(";");
                builder.append(s);
            }
            ret = builder.toString();

        } catch (IllegalProjectException e) {
            ret = e.retval.toString();
        } catch (IllegalUsernameException e) {
            ret = e.retval.toString();
        }
        finally {
            sendResponse(bufs.outputBuf, cliCh, ret);
        }
    }

    /*
        protocol for ADD MEMBER operation
        -   ADD_MEMBER;projectName;newMember
     */
    private void opAddMember(TCPBuffersNIO bufs, SocketChannel cliCh) throws IOException {
        List<String> tokens = StringUtils.tokenizeRequest(bufs.inputBuf);
        bufs.inputBuf.clear();

        String projectName  = tokens.remove(0);
        String newMember    = tokens.remove(0);

        String ret = this.server.addMember(bufs.getUsername(), projectName, newMember);

        sendResponse(bufs.outputBuf, cliCh, ret);
    }


    /*
 ******************************************** EXIT OPERATIONS
 */
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
