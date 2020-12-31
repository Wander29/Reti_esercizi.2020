package com;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

public interface ServerInterface extends Remote {
    public int register(String username, byte[] psw) throws RemoteException;

    public Map<String, Boolean> registerForCallback(NotifyInterface cli)     throws RemoteException;

    public int unregisterForCallback(NotifyInterface cli)   throws RemoteException;

}
