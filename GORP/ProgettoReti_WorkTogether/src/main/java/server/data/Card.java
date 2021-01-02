package server.data;

import java.sql.Date;
import java.util.List;

public class Card {
    String cardName; // univoco in tutto il progetto
    String description;

    public class CardMovement {
        Date movementDate;
        CardStatus fromStatus;
        CardStatus toStatus;
    }

    List<CardMovement> cardHistory;

    public Card() {

    }
}
