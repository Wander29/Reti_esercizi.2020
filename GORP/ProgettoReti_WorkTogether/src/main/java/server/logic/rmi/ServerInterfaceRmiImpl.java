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
import java.util.concurrent.ConcurrentMap;

public class ServerInterfaceRmiImpl extends UnicastRemoteObject implements ServerInterfaceRmi {

    private ServerManagerWT server = null;
    // concurrentHashMap to enhance (little) performance, since operations
    // are simple
    private ConcurrentMap<NotifyInterface, Boolean> clients = null;

    public ServerInterfaceRmiImpl(ConcurrentMap<NotifyInterface, Boolean> cl, ServerManagerWT serv )
            throws RemoteException
    {
        super();
        this.clients = cl;
        this.server = serv;
    }

    @Override
    public String register(String username, String psw) throws RemoteException, InvalidKeySpecException, NoSuchAlgorithmException {
        if(username.isEmpty())
            return CSReturnValues.USERNAME_INVALID.toString();

        return this.server.register(username, psw);
    }

    @Override
    public Map<String, Boolean> registerForCallback(NotifyInterface cli) throws RemoteException {
        this.clients.putIfAbsent(cli, Boolean.TRUE);

        return this.server.getStateUsers();
    }

    @Override
    public void unregisterForCallback(NotifyInterface cli) throws RemoteException {
        // Removes the key (and its corresponding value) from this map.
        // This method does nothing if the key is not in the map.
        this.clients.remove(cli);
    }

}
