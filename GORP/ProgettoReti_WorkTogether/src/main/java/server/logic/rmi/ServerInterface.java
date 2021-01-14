package server.logic.rmi;

import client.model.rmi.NotifyInterface;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;

public interface ServerInterface extends Remote {
    public String register(String username, String psw) throws RemoteException, InvalidKeySpecException, NoSuchAlgorithmException;

    public Map<String, Boolean> registerForCallback(NotifyInterface cli)     throws RemoteException;

    public void unregisterForCallback(NotifyInterface cli)   throws RemoteException;

}
