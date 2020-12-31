package RMI_callback;

import java.rmi.*;
import java.rmi.server.*;

public class NotifyEventImpl extends RemoteObject implements NotifyEventInterface {

    /* crea un nuovo callback client */
    public NotifyEventImpl( ) throws RemoteException { super( ); }

    /* metodo che può essere richiamato dal servente per notificare una
     nuova quotazione del titolo */
    public void notifyEvent(int value) throws RemoteException {
        String returnMessage = "Update event received: " + value;
        System.out.println(returnMessage);
    }
}
