package protocol.classes;

import java.io.Serializable;
import java.util.Date;

public class CardMovement implements Serializable {
    private static final long serialVersionUID = 01L;

    public Date movementTime;
    public CardStatus fromStatus;
    public CardStatus toStatus;
    public String user; // user who moved card

    public CardMovement(CardStatus from, CardStatus to, String user) {
        this.movementTime = new Date();
        this.fromStatus = from;
        this.toStatus = to;
        this.user = user;
    }

}
