package RMI_callback;

import java.rmi.*;
public interface ServerInterface extends Remote
{
    /* registrazione per la callback */
    public void registerForCallback(NotifyEventInterface ClientInterface) throws RemoteException;

    /* cancella registrazione per la callback */
    public void unregisterForCallback (
            NotifyEventInterface ClientInterface) throws RemoteException;
}

