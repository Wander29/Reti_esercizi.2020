package RMI_callback;

import java.rmi.*;
import java.rmi.server.*;
import java.util.*;

public class ServerImpl extends UnicastRemoteObject implements ServerInterface {
    /* lista dei client registrati */
    private List<NotifyEventInterface> clients;

    /* crea un nuovo servente */
    public ServerImpl() throws RemoteException {
        super();
        clients = new ArrayList<NotifyEventInterface>();
    }

    public synchronized void registerForCallback(NotifyEventInterface ClientInterface) throws RemoteException {
        if (!clients.contains(ClientInterface)) {
            clients.add(ClientInterface);
            System.out.println("New client registered.");
        }
    }

    /* annulla registrazione per il callback */
    public synchronized void unregisterForCallback(NotifyEventInterface Client) throws RemoteException {
        if (clients.remove(Client))
            System.out.println("Client unregistered");
        else
            System.out.println("Unable to unregister client");
    }

    /* notifica di una variazione di valore dell'azione
    /* quando viene richiamato, fa il callback a tutti i client
    registrati */
    public void update(int value) throws RemoteException {
        doCallbacks(value);
    }

    private synchronized void doCallbacks(int value) throws RemoteException {
        System.out.println("Starting callbacks.");
        Iterator i = clients.iterator( );
//int numeroClienti = clients.size( );
        while (i.hasNext()) {
            NotifyEventInterface client = (NotifyEventInterface) i.next();
            client.notifyEvent(value);
        }
        System.out.println("Callbacks complete.");
    }

}
