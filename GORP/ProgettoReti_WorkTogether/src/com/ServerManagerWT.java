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

    public ServerManagerWT() {
        this.server = new ServerWT();
    }

    public String register(String username, String psw) {
        return this.server.register(username, psw).toString();
    }

    public String login(String username, String psw) {
        return (this.server.login(username, psw).toString());
    }

    public String logout(String username) {
        return this.server.logout(username).toString();
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
}
