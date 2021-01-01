package com;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ClientNotify extends UnicastRemoteObject implements NotifyInterface {

    protected ClientNotify() throws RemoteException {
        super();
    }

    @Override
    public void notifyEvent() throws RemoteException {
        
    }
}
