package server.logic.rmi;

import client.model.rmi.NotifyInterface;
import protocol.CSReturnValues;
import server.logic.ServerManagerWT;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ServerManagerRMI extends UnicastRemoteObject implements ServerInterface {

    private ConcurrentMap<NotifyInterface, Boolean> clients;
    private ServerManagerWT server;

    public ServerManagerRMI(ServerManagerWT serverManager) throws RemoteException {
        super();
        this.server = serverManager;
        this.clients = new ConcurrentHashMap<>();
    }

    @Override
    public String register(String username, String psw) throws RemoteException, InvalidKeySpecException, NoSuchAlgorithmException {
        if(username.isEmpty())
            return CSReturnValues.USERNAME_INVALID.toString();

        return this.server.register(username, psw);
    }

    @Override
    public Map<String, Boolean> registerForCallback(NotifyInterface cli) throws RemoteException {
        this.clients.putIfAbsent(cli, Boolean.TRUE);

        return this.server.getStateUsers();
    }

    @Override
    public void unregisterForCallback(NotifyInterface cli) throws RemoteException {
        // Removes the key (and its corresponding value) from this map.
        // This method does nothing if the key is not in the map.
        this.clients.remove(cli);
    }

    public void userIsOfflineCallbacks(String username) {
        this.clients.forEach( (notifyInterface, aBoolean) -> {
                try {
                    notifyInterface.userIsOffline(username);
                }
                catch (RemoteException e) { e.printStackTrace(); }
            });
    }

    public void userIsOnlineCallbacks(String username) {
        this.clients.forEach( (notifyInterface, aBoolean) -> {
            try {
                notifyInterface.userIsOnline(username);
            }
            catch (RemoteException e) { e.printStackTrace(); }
        });
    }

    public void newUserCallbacks(String username) {
        this.clients.forEach( (notifyInterface, aBoolean) -> {
            try {
                notifyInterface.newUser(username);
            }
            catch (RemoteException e) { e.printStackTrace(); }
        });
    }
}
