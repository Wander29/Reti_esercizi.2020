package com;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ServerManagerRMI extends UnicastRemoteObject implements ServerInterface {

    private List<NotifyInterface> clients;
    private ServerManagerWT server;

    protected ServerManagerRMI(ServerManagerWT serverManager) throws RemoteException {
        super();
        this.server = serverManager;
        this.clients = new ArrayList<>();
    }

    @Override
    public int register(String username, byte[] psw) throws RemoteException {
        if(username.isEmpty())
            return ClientServerErrorCodes.USERNAME_EMPTY();

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

    public void doCallbacks() throws RemoteException {
        Iterator i = clients.iterator();

        while(i.hasNext()) {
            NotifyInterface cli = (NotifyInterface) i.next();
            cli.notifyEvent();
            // notifica inviata ad 1 client
        }
        // notificati tutti i client
    }
}
