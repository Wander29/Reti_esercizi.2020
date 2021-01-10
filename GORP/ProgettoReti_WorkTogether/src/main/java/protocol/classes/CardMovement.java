package protocol.classes;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Time;

public class CardMovement implements Serializable {
    public Time movementTime;
    public CardStatus fromStatus;
    public CardStatus toStatus;
    public String user; // user who moved card

    public CardMovement(long time, CardStatus from, CardStatus to, String user) {
        this.movementTime = new Time(time);
        this.fromStatus = from;
        this.toStatus = to;
        this.user = user;
    }

}
