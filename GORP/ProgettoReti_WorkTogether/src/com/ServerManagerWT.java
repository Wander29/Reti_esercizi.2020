package com;

import java.rmi.RemoteException;
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

    public int login(String username, byte[] psw) {
        return this.server.login(username, psw);
    }

    public int logout(String username) {
        return this.server.logout(username);
    }

    public Set<String> getUsers() {
        return this.server.getUsers();
    }

    public Set<String> getOnlineUsers() {
        return this.server.getOnlineUsers();
    }
}
