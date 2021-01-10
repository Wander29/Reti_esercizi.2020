package protocol.classes;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
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

    private String cardName;
    private String description;
    protected List<CardMovement> cardHistory;

    public Card(String name, String descr, String username) {
        this.cardName       = name;
        this.description    = descr;

        this.cardHistory = new ArrayList<>();
    }
}
