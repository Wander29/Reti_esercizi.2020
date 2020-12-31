package com;

import java.awt.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
    public int login(String username, byte[] psw) throws RemoteException {
        if(username.isEmpty())
            return ClientServerErrorCodes.USERNAME_EMPTY();

        return this.server.login(username, psw);
    }

    @Override
    public int logout(String username) throws RemoteException {
        if(username.isEmpty())
            return ClientServerErrorCodes.USERNAME_EMPTY();

        return this.server.logout(username);
    }

    @Override
    public Set<String> registerForCallback(NotifyInterface cli) throws RemoteException {

        return this.server.getOnlineUsers();
    }

    @Override
    public int unregisterForCallback(NotifyInterface cli) throws RemoteException {
        return 0;
    }

    private int doCallbacks() throws RemoteException {
        return 0;
    }
}
