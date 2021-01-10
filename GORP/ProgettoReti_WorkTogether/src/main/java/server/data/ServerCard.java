package server.data;

import protocol.classes.Card;
import protocol.classes.CardMovement;
import protocol.classes.CardStatus;

public class ServerCard extends Card {

    public ServerCard(String name, String descr, String username) {
        super(name, descr, username);

        this.addMovement(System.currentTimeMillis(), CardStatus.NEW,
                CardStatus.TO_DO, username);
    }
    public void addMovement(long time, CardStatus from, CardStatus to, String user) {
        this.cardHistory.add(
                new CardMovement(
                        time,
                        CardStatus.NEW,
                        CardStatus.TO_DO,
                        user
                ));
    }
}
