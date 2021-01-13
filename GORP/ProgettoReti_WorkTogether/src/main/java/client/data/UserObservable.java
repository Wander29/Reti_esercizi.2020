package client.data;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import protocol.classes.ChatMsg;

import java.io.PushbackReader;

public class UserObservable {
    private final SimpleStringProperty username = new SimpleStringProperty();
    private final SimpleStringProperty stato    = new SimpleStringProperty();

    public UserObservable(String  user, boolean state) {
        this.username.set(user);
        this.stato.set( state ? "ONLINE" : "offline" );
    }

    public final StringProperty usernameProperty() {
        return username;
    }
    public final String getUsername() {
        return username.get();
    }
    public final void setUsername(String username) {
        this.username.set(username);
    }

    public StringProperty statoProperty() {
        return stato;
    }
    public String getStato() {
        return stato.get();
    }
    public final void setStato(boolean state) {
        this.stato.set( state ? "online" : "offline" );
    }
}
