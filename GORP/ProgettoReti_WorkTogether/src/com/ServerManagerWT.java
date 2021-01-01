package com;

import java.rmi.RemoteException;
import java.util.Map;
import java.util.Set;

/*
    gestisce la concorrenza ecc.. del SERVER REALE
 */

public class ServerManagerWT {

    private ServerWT server;

    public ServerManagerWT() {
        this.server = new ServerWT();
    }

    public int register(String username, byte[] psw) {
        return this.server.register(username, psw);
    }

    public CSReturnValues login(String username, String psw) {
        return this.server.login(username, psw);
    }

    public CSReturnValues logout(String username) {
        return this.server.logout(username);
    }

    public Map<String, Boolean> getStateUsers() {
        return this.server.getStateUsers();
    }
}
