package server.data;

import protocol.CSProtocol;
import protocol.classes.Card;
import protocol.classes.CardStatus;
import protocol.classes.Project;
import protocol.exceptions.IllegalOperation;

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
        this.toDoCards          = new HashMap<>();
        this.inProgressCards    = new HashMap<>();
        this.toBeRevisedCards   = new HashMap<>();
        this.doneCards          = new HashMap<>();
        // add creator as member
        this.members.add(creator);
    }

    /*** TEST
     */
    public void addCard(String cardName, String descr, String username) {
        this.toDoCards.put(cardName, new Card(cardName, descr, username));
    }

    public boolean isCardInFromStatus(String cardName, CardStatus fromStatus) {

        switch(fromStatus) {

            case TO_DO:
                if(toDoCards.containsKey(cardName))           return true;
                else        return false;

            case IN_PROGRESS:
                if(inProgressCards.containsKey(cardName))     return true;
                else        return false;

            case TO_BE_REVISED:
                if(toBeRevisedCards.containsKey(cardName))     return true;
                else        return false;

            case DONE:
                if(doneCards.containsKey(cardName))            return true;
                else        return false;
        }

        return false;
    }

    public void moveCard(String name, CardStatus from, CardStatus to, String username)
            throws IllegalOperation
    {
        Card movingCard;

        switch(from) {
            case TO_DO:
                if(to != CardStatus.IN_PROGRESS)
                    throw new IllegalOperation();

                movingCard = this.toDoCards.remove(name);

                this.inProgressCards.put(name, movingCard);
                movingCard.addMovement(
                        from,
                        to,
                        username
                );

                break;

            case IN_PROGRESS:
                if(to != CardStatus.TO_BE_REVISED && to != CardStatus.DONE)
                    throw new IllegalOperation();

                movingCard = this.inProgressCards.remove(name);

                switch(to) {
                    case TO_BE_REVISED:
                        toBeRevisedCards.put(name, movingCard);
                        break;

                    case DONE:
                        doneCards.put(name, movingCard);
                        break;

                    default:
                        return;
                }
                movingCard.addMovement(
                        from,
                        to,
                        username
                );

                break;

            case TO_BE_REVISED:
                if(to != CardStatus.IN_PROGRESS && to != CardStatus.DONE)
                    throw new IllegalOperation();

                movingCard = this.toBeRevisedCards.remove(name);

                switch(to) {
                    case IN_PROGRESS:
                        inProgressCards.put(name, movingCard);
                        break;

                    case DONE:
                        doneCards.put(name, movingCard);
                        break;

                    default:
                        return;
                }
                movingCard.addMovement(
                        from,
                        to,
                        username
                );

                break;

            // if is from: done -> illegal operation
            default:
                throw new IllegalOperation();
        }
    }

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
        Socket s;
        try {
            s = new Socket(ip, CSProtocol.WORTH_DEFAULT_CHAT_SERVICE_PORT());
        } catch (IOException e) {
            return CSProtocol.WORTH_DEFAULT_CHAT_SERVICE_PORT();
        }

        for (int i = FIRST_FREE_PORT; i <= MAX_PORT_NUM; i++) {
            try {
                s = new Socket(ip, i);
                System.out.println("Esiste un servizio sulla porta "+i);
            }
            catch (IOException ex) {
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


    public Map<String, Card> getToDoCards() {
        return toDoCards;
    }

    public Map<String, Card> getInProgressCards() {
        return inProgressCards;
    }

    public Map<String, Card> getToBeRevisedCards() {
        return toBeRevisedCards;
    }

    public Map<String, Card> getDoneCards() {
        return doneCards;
    }

    public InetAddress getChatMulticastIP() {
        return chatMulticastIP;
    }

    public int getChatMulticastPort() {
        return chatMulticastPort;
    }
}
