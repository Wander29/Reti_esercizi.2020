package server.logic.rmi;

/**
 * @author      LUDOVICO VENTURI (UniPi)
 * @date        2021/01/14
 * @versione    1.0
 */

import client.model.rmi.NotifyInterface;
import server.logic.ServerManagerWT;

import java.rmi.RemoteException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

// singleton
public class ServerManagerRmi {
    private static volatile ServerManagerRmi instance;

    private static ServerInterfaceRmiImpl stub;
    private static ServerManagerWT server = null;
        // concurrentHashMap to enhance (a little) performance, since operations are simple
    private static ConcurrentMap<NotifyInterface, Boolean> clients;

    private ServerManagerRmi() throws RemoteException {
        super();
        this.clients    = new ConcurrentHashMap<>();
        this.stub       = new ServerInterfaceRmiImpl(clients, server);
    }

    public static synchronized ServerManagerRmi getInstance() throws RemoteException {
        if(instance == null) {
            instance = new ServerManagerRmi();
        }

        return instance;
    }

    // to avoid dependency cycle
    public static synchronized void setManagerWT() {
        server = ServerManagerWT.getInstance();
    }

    public static ServerInterfaceRmi getStub() {
        return stub;
    }

    public static void userIsOfflineCallbacks(String username) {
        clients.forEach( (notifyInterface, aBoolean) -> {
                try {
                    notifyInterface.userIsOffline(username);
                }
                catch (RemoteException e) { e.printStackTrace(); }
            });
    }

    public void userIsOnlineCallbacks(String username) {
        this.clients.forEach( (notifyInterface, aBoolean) -> {
            try {
                notifyInterface.userIsOnline(username);
            }
            catch (RemoteException e) { e.printStackTrace(); }
        });
    }

    public void newUserCallbacks(String username) {
        this.clients.forEach( (notifyInterface, aBoolean) -> {
            try {
                notifyInterface.newUser(username);
            }
            catch (RemoteException e) { e.printStackTrace(); }
        });
    }
}
