package client.model.rmi;

import protocol.CSProtocol;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ClientNotify extends UnicastRemoteObject implements NotifyInterface {

    private Map<String, Boolean> usersOnlineState;
    private Set<String> usersOnline;

    public ClientNotify() throws RemoteException {
        super();
        this.usersOnline = new HashSet<>();
    }

    public synchronized void setUsersMap(Map<String, Boolean> map) {
        this.usersOnlineState = map;
        for(Map.Entry<String, Boolean> user : this.usersOnlineState.entrySet()) {
            if(user.getValue()) {
                this.usersOnline.add(user.getKey());
            }
        }
    }

    @Override
    public synchronized void userIsOnline(String username) throws RemoteException {
        this.usersOnlineState.replace(username, Boolean.TRUE);
        this.usersOnline.add(username);
    }

    @Override
    public synchronized void userIsOffline(String username) throws RemoteException {
        this.usersOnlineState.replace(username, Boolean.FALSE);
        this.usersOnline.remove(username);
    }

    @Override
    public synchronized void newUser(String username) throws RemoteException {
        this.usersOnlineState.put(username, Boolean.FALSE);
    }

    public synchronized Map<String, Boolean> getAllUsers() {
        return Collections.unmodifiableMap(this.usersOnlineState);
    }

    public synchronized Set<String> getOnlineUsers() {
        return Collections.unmodifiableSet(this.usersOnline);
    }
}
