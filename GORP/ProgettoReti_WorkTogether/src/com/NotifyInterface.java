package com;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface NotifyInterface extends Remote {

    public void notifyEvent() throws RemoteException;
}
