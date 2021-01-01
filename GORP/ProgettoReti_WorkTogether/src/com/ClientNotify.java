package com;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ClientNotify extends UnicastRemoteObject implements NotifyInterface {

    private Map<String, Boolean> usersOnlineState;
    private Set<String> usersOnline;

    protected ClientNotify() throws RemoteException {
        super();
        this.usersOnline = new HashSet<>();
    }

    public void setUsersMap(Map<String, Boolean> map) {
        this.usersOnlineState = map;
        for(Map.Entry<String, Boolean> user : this.usersOnlineState.entrySet()) {
            if(user.getValue()) {
                this.usersOnline.add(user.getKey());
            }
        }
    }

    @Override
    public void userIsOnline(String username) throws RemoteException {
        if(CSProtocol.DEBUG()) {
            System.out.println("CALLBACK ricevuta [online]: " + username);
        }

        this.usersOnlineState.replace(username, Boolean.TRUE);
        this.usersOnline.add(username);
    }

    @Override
    public void userIsOffline(String username) throws RemoteException {
        if(CSProtocol.DEBUG()) {
            System.out.println("CALLBACK ricevuta [offline]: " + username);
        }
        this.usersOnlineState.replace(username, Boolean.FALSE);
        this.usersOnline.remove(username);
    }

    @Override
    public void newUser(String username) throws RemoteException {
        if(CSProtocol.DEBUG()) {
            System.out.println("CALLBACK ricevuta [new]: " + username);
        }
        this.usersOnlineState.put(username, Boolean.FALSE);
    }

    public Set<String> getAllUsers() {
        return Collections.unmodifiableSet(this.usersOnlineState.keySet());
    }

    public Set<String> getOnlineUsers() {
        return Collections.unmodifiableSet(this.usersOnline);
    }
}
