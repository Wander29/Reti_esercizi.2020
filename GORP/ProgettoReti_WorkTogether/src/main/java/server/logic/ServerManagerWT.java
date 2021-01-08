package server.logic;

import protocol.CSReturnValues;
import server.data.WorthData;
import server.logic.rmi.ServerManagerRMI;
import utils.exceptions.IllegalProjectException;
import utils.exceptions.IllegalUsernameException;

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
/*
RMI
 */
    public void setRMIManager(ServerManagerRMI manager) {
    this.manager = manager;
}

    public String register(String username, String psw) throws RemoteException {
        String ret = this.server.register(username, psw).toString();
        this.manager.newUserCallbacks(username);

        return ret;
    }

    public Map<String, Boolean> getStateUsers() {
        return this.server.getStateUsers();
    }
/*
TCP
 */
    public String login(String username, String psw) throws RemoteException {
        String ret = this.server.login(username, psw).toString();
        this.manager.userIsOnlineCallbacks(username);
        return ret;
    }

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

    public synchronized Set<String> listProjects(String username) throws IllegalUsernameException {
        return this.server.listProjects(username);
    }

    public synchronized List<String> showMembers(String username, String projName)
            throws IllegalProjectException, IllegalUsernameException
    {
        return this.server.showMembers(username, projName);
    }

/*
 ******************************************** EXIT OPERATIONS
 */
    public String logout(String username) throws RemoteException {
        String ret = this.server.logout(username).toString();
        this.manager.userIsOfflineCallbacks(username);
        return ret;
    }

/*
    SERIALIZATION
 */
    public synchronized WorthData getWorthData() { return this.server.getWorthData(); }
}

/*
    public Map<String, Project> getProjects() {
        return this.server.getProjects();
    }

    public Map<String, UserInfo> getUsers() { return this.server.getUsers(); }
     */