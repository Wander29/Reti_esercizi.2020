package com;

import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

/*
    gestisce la concorrenza ecc.. del SERVER REALE
 */

public class ServerManagerWT {

    private ServerWT server;
    private ServerManagerRMI manager;

    public ServerManagerWT() {
        this.server = new ServerWT();
    }

    public String register(String username, String psw) throws RemoteException {
        String ret = this.server.register(username, psw).toString();
        this.manager.newUserCallbacks(username);

        return ret;
    }

    public String login(String username, String psw) throws RemoteException {
        String ret = this.server.login(username, psw).toString();
        this.manager.userIsOnlineCallbacks(username);
        return ret;
    }

    public String logout(String username) throws RemoteException {
        String ret = this.server.logout(username).toString();
        this.manager.userIsOfflineCallbacks(username);
        return ret;
    }

    /*
        SYNCHRONIZED operations
     */
    public synchronized String createProject(String username, String projectName) {
        try {
            String ret = this.server.createProject(username, projectName).toString();
            String ip = this.server.getProjectMulticasIp(projectName).substring(1);

            return ret + ";" + ip;
        }
        catch(UnknownHostException | NoSuchElementException e) {
            return CSReturnValues.SERVER_INTERNAL_NETWORK_ERROR.toString();
        }

    }

    public Map<String, Boolean> getStateUsers() {
        return this.server.getStateUsers();
    }

    protected void setRMIManager(ServerManagerRMI manager) {
        this.manager = manager;
    }
}
