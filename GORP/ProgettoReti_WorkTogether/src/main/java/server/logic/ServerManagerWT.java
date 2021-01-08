package server.logic;

import protocol.CSReturnValues;
import server.data.Project;
import server.data.UserInfo;
import server.data.WorthData;
import server.logic.rmi.ServerManagerRMI;

import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

/*
    gestisce la concorrenza ecc.. del SERVER REALE
 */

public class ServerManagerWT {

    private ServerWT server;
    private ServerManagerRMI manager;

    public ServerManagerWT(WorthData data) {
        this.server = new ServerWT(data);
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

    public synchronized Set<String> listProjects() {
        return this.server.listProjects();
    }

    public Map<String, Boolean> getStateUsers() {
        return this.server.getStateUsers();
    }

    public void setRMIManager(ServerManagerRMI manager) {
        this.manager = manager;
    }

    public Map<String, Project> getProjects() {
        return this.server.getProjects();
    }

    public Map<String, UserInfo> getUsers() { return this.server.getUsers(); }

    public synchronized WorthData getWorthData() { return this.server.getWorthData(); }
}
