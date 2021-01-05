package server.data;

import java.io.Serializable;
import java.sql.Date;
import java.util.List;

public class Card implements Serializable {
    public String getCardName() {
        return cardName;
    }

    public String getDescription() {
        return description;
    }

    public List<CardMovement> getCardHistory() {
        return cardHistory;
    }

    String cardName; // univoco in tutto il progetto
    String description;

    public class CardMovement {
        Date movementDate;
        CardStatus fromStatus;
        CardStatus toStatus;
        String user; // user who moved this card
    }

    List<CardMovement> cardHistory;

    public Card() {

    }
}
