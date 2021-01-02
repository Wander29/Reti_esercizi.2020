package client.model.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface NotifyInterface extends Remote {

    public void userIsOnline(String username) throws RemoteException;

    public void userIsOffline(String username) throws RemoteException;

    public void newUser(String username) throws RemoteException;
}
