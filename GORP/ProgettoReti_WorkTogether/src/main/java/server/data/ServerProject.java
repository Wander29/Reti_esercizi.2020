package server.data;

import protocol.classes.Card;
import protocol.classes.Project;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;

public class ServerProject extends Project implements Serializable {

    private final static int FIRST_FREE_PORT = 1024;
    private final static int MAX_PORT_NUM = 65535;
    private final static int START_MULTICAST_FIRST_OCTET = 239;
    private final static int END_MULTICAST_FIRST_OCTET = 239;

    private List<String> members;

    private InetAddress chatMulticastIP;
    private int chatMulticastPort;

    private static List<InetAddress> ipFree = new ArrayList<>();

    public ServerProject(String s, String creator) throws UnknownHostException, NoSuchElementException {
        this.projectName = s;

        // assign a MulticastIP and port to this project
        if(ipFree.isEmpty())
            this.chatMulticastIP = this.generateMulticastIP();
        else
            this.chatMulticastIP = ipFree.remove(0);

        this.chatMulticastPort = this.portScannerFindFreePort(this.chatMulticastIP);

        this.members            = new ArrayList<>();
        this.toDoCards          = new ArrayList<>();
        this.inProgressCards    = new ArrayList<>();
        this.toBeRevisedCards   = new ArrayList<>();
        this.doneCards          = new ArrayList<>();
        // add creator as member
        this.members.add(creator);
    }

    /*** TEST
     */
    public void addCard(String name, String descr) {
        this.toDoCards.add(new Card(name, descr));
    }

    public void addInProgressCard(String name, String descr) {
        this.inProgressCards.add(new Card(name, descr));
    }

    public void addToBeRevisedCard(String name, String descr) {
        this.toBeRevisedCards.add(new Card(name, descr));
    }

    public void addDoneCard(String name, String descr) {
        this.doneCards.add(new Card(name, descr));
    }
    /*

     */

    public void addMember(String username) {
        this.members.add(username);
    }

    public void delete() {
        ipFree.add(this.getChatMulticastIP());
    }

/*
    Multicast IP related
 */
    private static int
            a = START_MULTICAST_FIRST_OCTET,
            b = 0,
            c = 0,
            d = 0;

    private InetAddress generateMulticastIP() throws UnknownHostException {

        InetAddress currIP;

        String currentIPString;

        do {
            StringBuilder sbuild = new StringBuilder(Integer.toString(a));
            sbuild.append(".");
            sbuild.append(Integer.toString(b));
            sbuild.append(".");
            sbuild.append(Integer.toString(c));
            sbuild.append(".");
            sbuild.append(Integer.toString(d));

            currentIPString = sbuild.toString();

            System.out.println(currentIPString);
            currIP = InetAddress.getByName(currentIPString);

            d++;
            if(d > 255) {
                d = 0;
                c++;
            }
            if (c > 255) {
                c = 0;
                b++;
            }
            if (b > 255) {
                b = 0;
                a++;
            }
            if(a > END_MULTICAST_FIRST_OCTET) {
                throw new NoSuchElementException();
            }
        } while(!currIP.isMulticastAddress());

        return currIP;
    }

    private int portScannerFindFreePort(InetAddress ip) {
        for (int i = FIRST_FREE_PORT; i <= MAX_PORT_NUM; i++) {
            try {
                Socket s = new Socket(ip, i);
                System.out.println("Esiste un servizio sulla porta "+i);
            }
            catch (IOException ex) {
                // System.out.println("Non esiste un servizio sulla porta");
                return i;
            }
        }

        return -1;
    }

/*
    GETTERs
 */
    public String getProjectName() {
        return projectName;
    }

    public List<String> getMembers() {
        return members;
    }


    public List<Card> getToDoCards() {
        return toDoCards;
    }

    public List<Card> getInProgressCards() {
        return inProgressCards;
    }

    public List<Card> getToBeRevisedCards() {
        return toBeRevisedCards;
    }

    public List<Card> getDoneCards() {
        return doneCards;
    }

    public InetAddress getChatMulticastIP() {
        return chatMulticastIP;
    }

    public int getChatMulticastPort() {
        return chatMulticastPort;
    }
}
