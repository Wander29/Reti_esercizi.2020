package server.logic.rmi;

/**
 * @author      LUDOVICO VENTURI (UniPi)
 * @date        2021/01/14
 * @versione    1.0
 */

import client.model.rmi.NotifyInterface;
import protocol.CSReturnValues;
import server.logic.ServerManagerWT;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

// singleton
public class ServerManagerRMIRmi {
    private static volatile ServerManagerRMIRmi instance;

    private static ServerInterfaceRmiImpl stub;
    private static ServerManagerWT server = null;
        // concurrentHashMap to enhance (little) performance, since operations
            // are simple
    private static ConcurrentMap<NotifyInterface, Boolean> clients;

    private ServerManagerRMIRmi() throws RemoteException {
        super();
        this.clients    = new ConcurrentHashMap<>();
        this.server     = ServerManagerWT.getInstance();
        this.stub       = new ServerInterfaceRmiImpl(clients, server);
    }

    public static synchronized ServerManagerRMIRmi getInstance() throws RemoteException {
        if(instance == null) {
            instance = new ServerManagerRMIRmi();
        }

        return instance;
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
