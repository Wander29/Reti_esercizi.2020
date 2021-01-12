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

public class ServerManagerRMI extends UnicastRemoteObject implements ServerInterface {

    private List<NotifyInterface> clients;
    private ServerManagerWT server;

    public ServerManagerRMI(ServerManagerWT serverManager) throws RemoteException {
        super();
        this.server = serverManager;
        this.clients = new ArrayList<>();
    }

    @Override
    public String register(String username, String psw) throws RemoteException, InvalidKeySpecException, NoSuchAlgorithmException {
        if(username.isEmpty())
            return CSReturnValues.USERNAME_INVALID.toString();

        return this.server.register(username, psw);
    }

    @Override
    public Map<String, Boolean> registerForCallback(NotifyInterface cli) throws RemoteException {
        if(!this.clients.contains(cli)) {
            this.clients.add(cli);

            return this.server.getStateUsers();
        }

        return null;
    }

    @Override
    public int unregisterForCallback(NotifyInterface cli) throws RemoteException {
        if(!this.clients.contains(cli)) {
            return -1;
        }

        this.clients.remove(cli);
        return 0;
    }

    public void userIsOfflineCallbacks(String username) throws RemoteException {
        Iterator i = clients.iterator();

        while(i.hasNext()) {
            NotifyInterface cli = (NotifyInterface) i.next();
            cli.userIsOffline(username);
        }
    }

    public void userIsOnlineCallbacks(String username) throws RemoteException {
        Iterator i = clients.iterator();

        while(i.hasNext()) {
            NotifyInterface cli = (NotifyInterface) i.next();
            cli.userIsOnline(username);
        }
    }

    public void newUserCallbacks(String username) throws RemoteException {
        Iterator i = clients.iterator();

        while(i.hasNext()) {
            NotifyInterface cli = (NotifyInterface) i.next();
            cli.newUser(username);
        }
    }
}
