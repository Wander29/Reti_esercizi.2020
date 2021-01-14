package server.logic.rmi;

/**
 * @author      LUDOVICO VENTURI (UniPi)
 * @date        2021/01/14
 * @versione    1.0
 */

import client.model.rmi.NotifyInterface;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;

public interface ServerInterfaceRmi extends Remote {
    public String register(String username, String psw) throws RemoteException, InvalidKeySpecException, NoSuchAlgorithmException;

    public Map<String, Boolean> registerForCallback(NotifyInterface cli)     throws RemoteException;

    public void unregisterForCallback(NotifyInterface cli)   throws RemoteException;

}
