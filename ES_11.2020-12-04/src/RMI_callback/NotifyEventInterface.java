package RMI_callback;

import java.rmi.*;
public interface NotifyEventInterface extends Remote {
    /* Metodo invocato dal server per notificare un evento ad un client remoto. */
    public void notifyEvent(int value) throws RemoteException;
}
