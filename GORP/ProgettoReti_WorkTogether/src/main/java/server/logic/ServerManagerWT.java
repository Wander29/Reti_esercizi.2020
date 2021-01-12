package server.logic;

import protocol.CSReturnValues;
import protocol.classes.Card;
import protocol.classes.CardStatus;
import protocol.classes.ListProjectEntry;
import protocol.classes.Project;
import protocol.exceptions.IllegalOperation;
import server.data.WorthData;
import server.logic.rmi.ServerManagerRMI;
import protocol.exceptions.IllegalProjectException;
import protocol.exceptions.IllegalUsernameException;

import java.io.IOException;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

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

    public String register(String username, String psw)
            throws RemoteException, InvalidKeySpecException, NoSuchAlgorithmException
    {
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
    public String login(String username, String psw) throws RemoteException, InvalidKeySpecException, NoSuchAlgorithmException {
        String ret = this.server.login(username, psw).toString();
        this.manager.userIsOnlineCallbacks(username);
        return ret;
    }

    public synchronized String createProject(String username, String projectName) {
        try {
            String ret = this.server.createProject(username, projectName).toString();
            String ip = this.server.getProjectMulticasIp(projectName);

            return ret + ";" + ip;
        }
        catch(UnknownHostException | NoSuchElementException e) {
            return CSReturnValues.SERVER_INTERNAL_NETWORK_ERROR.toString();
        }

    }

    public synchronized List<ListProjectEntry> listProjects(String username)
            throws IllegalUsernameException
    {
        return this.server.listProjects(username);
    }

    public synchronized Project showProject(String username, String projectName) throws IllegalProjectException, IllegalUsernameException {
        return this.server.showProject(username, projectName);
    }

    public synchronized String moveCard(String username, String projectName,
                                        String cardName, CardStatus from, CardStatus to)
    {
        try {
            return this.server.moveCard(username, projectName, cardName, from ,to).toString();
        }
        catch (IllegalOperation ill) {
            return ill.retval.toString();
        }
    }

    public synchronized String addCard(String username, String projectName,
                                       String cardName, String description)
    {
        return this.server.addCard(username, projectName, cardName, description).toString();
    }

    public synchronized String deleteProject(String username, String projectName) {
        return this.server.deleteProject(username, projectName).toString();
    }

    public synchronized List<String> showMembers(String username, String projName)
            throws IllegalProjectException, IllegalUsernameException
    {
        return this.server.showMembers(username, projName);
    }

    public synchronized String addMember(String username, String projectName, String newMember) throws IOException {
        return this.server.addMember(username, projectName, newMember).toString();
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
    public WorthData getWorthData() { return this.server.getWorthData(); }
}

/*
public Map<String, Project> getProjects() {
    return this.server.getProjects();
}

public Map<String, UserInfo> getUsers() { return this.server.getUsers(); }
 */
