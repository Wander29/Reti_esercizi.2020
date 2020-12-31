package com;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerInterface extends Remote {
    public int register(String username, byte[] psw) throws RemoteException;

    public int login(String username, byte[] psw)           throws RemoteException;

    public int logout(String username)                      throws RemoteException;

    public int registerForCallback(NotifyInterface cli)     throws RemoteException;

    public int unregisterForCallback(NotifyInterface cli)   throws RemoteException;

}
