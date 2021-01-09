package client.data;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import protocol.classes.ChatMsg;
import protocol.classes.ListProjectEntry;

public class ChatMsgObservable {
    private final SimpleStringProperty username;
    private final SimpleStringProperty message;
    private final SimpleStringProperty timeSent;

    public ChatMsgObservable(String  user, String msg, String time) {
        this.username = new SimpleStringProperty(user);
        this.message = new SimpleStringProperty(msg);
        this.timeSent = new SimpleStringProperty(time);
    }

    public final String getUsername() {
        return username.get();
    }
    public final StringProperty usernameProperty() {
        return username;
    }

    public final String getMessage() {
        return message.get();
    }
    public final StringProperty messageProperty() {
        return message;
    }

    public final String getTimeSent() {
        return timeSent.get();
    }
    public final StringProperty timeSentProperty() {
        return timeSent;
    }
}
