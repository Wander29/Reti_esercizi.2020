package server.data;

/**
 * @author      LUDOVICO VENTURI (UniPi)
 * @date        2021/01/14
 * @versione    1.0
 */

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

public class ServerProject implements Serializable {

    private final transient int FIRST_FREE_PORT = 1024;
    // also transient because i serialize static fields for ServerProject
    private final transient static int MAX_PORT_NUM = 65535;
    private final transient static int START_MULTICAST_FIRST_OCTET = 239;
    private final transient static int END_MULTICAST_FIRST_OCTET = 239;

    private String projectName;
        // they are transient because of project specifics
    private transient Map<String,Card> toDoCards;
    private transient Map<String,Card> inProgressCards;
    private transient Map<String,Card> toBeRevisedCards;
    private transient Map<String,Card> doneCards;

    private List<String> members;

    private InetAddress chatMulticastIP;
    private int chatMulticastPort;
    private static List<InetAddress> ipFree = new ArrayList<>(); // it will be serialized too

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

    /**
     * checks if the status passed as argument is the same as the card one
     * @param cardName
     * @param fromStatus
     * @return
     */
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

    /**
     * allows moving card
     *
     * @REQUIRES    from status of request and from status of card are the same status
     *
     * @param name  cardName
     * @param from  from status of card
     * @param to    to status, where user wants to move card
     * @param username  username who asked operation
     * @throws IllegalOperation if status
     */
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

            // if it's from: done -> illegal operation
            default:
                throw new IllegalOperation();
        }
    }

    public void addMember(String username) {
        this.members.add(username);
    }

    public void delete() {
        InetAddress ia = this.getChatMulticastIP();

        if(ipFree.contains(ia)) {
            System.out.println("ERRORE: collisione IP");
        }

        ipFree.add(ia);
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
        // try first default port
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

    public Card getCardFromAnyList(String name) {
        if(this.toDoCards.containsKey(name)) {
            return this.toDoCards.get(name);
        }
        else if(this.inProgressCards.containsKey(name)) {
            return this.inProgressCards.get(name);
        }
        else if(this.toBeRevisedCards.containsKey(name)) {
            return this.toBeRevisedCards.get(name);
        }
        else if(this.doneCards.containsKey(name)) {
            return this.doneCards.get(name);
        }

        return null;
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

/*
    SETTERs
 */
    public void setToDoCards(Map<String, Card> toDoCards) {
        this.toDoCards = toDoCards;
    }

    public void setInProgressCards(Map<String, Card> inProgressCards) {
        this.inProgressCards = inProgressCards;
    }

    public void setToBeRevisedCards(Map<String, Card> toBeRevisedCards) {
        this.toBeRevisedCards = toBeRevisedCards;
    }

    public void setDoneCards(Map<String, Card> doneCards) {
        this.doneCards = doneCards;
    }
}
