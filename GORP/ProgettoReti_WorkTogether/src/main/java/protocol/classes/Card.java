package protocol.classes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Card implements Serializable {
    private static final long serialVersionUID = 01L;

    public String getCardName() {
        return cardName;
    }

    public String getDescription() {
        return description;
    }

    public List<CardMovement> getCardHistory() {
        return cardHistory;
    }

    public CardStatus getStatus() {
        return status;
    }

    protected String cardName;
    protected String description;
    protected CardStatus status;
    protected List<CardMovement> cardHistory;

    public Card(String name, String descr, CardStatus status, List<CardMovement> history) {
        this.cardName       = name;
        this.description    = descr;
        this.status         = status;
        this.cardHistory    = history;
    }

    public Card(String name, String descr, String username) {
        this.cardName       = name;
        this.description    = descr;
        this.cardHistory    = new ArrayList<>();
        this.status         = CardStatus.TO_DO;

        this.addMovement(CardStatus.NEW,
                CardStatus.TO_DO, username);
    }

    public void addMovement(CardStatus from, CardStatus to, String user) {

        this.cardHistory.add(
                new CardMovement(
                        from,
                        to,
                        user
                ));
        this.status = to;
    }
}
