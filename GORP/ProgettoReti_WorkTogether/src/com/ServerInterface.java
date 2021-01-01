package com;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

public interface ServerInterface extends Remote {
    public String register(String username, String psw) throws RemoteException;

    public Map<String, Boolean> registerForCallback(NotifyInterface cli)     throws RemoteException;

    public int unregisterForCallback(NotifyInterface cli)   throws RemoteException;

}
