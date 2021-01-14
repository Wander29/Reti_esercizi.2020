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

// singleton
public class ServerManagerWT {
    private static volatile ServerManagerWT instance;

    private static ServerWT server;
    private static ServerManagerRMI manager;

    private ServerManagerWT( ) {
        server = ServerWT.getInstance();
    }

    public static synchronized ServerManagerWT getInstance() {
        if(instance == null) {
            instance = new ServerManagerWT();
        }

        return instance;
    }
/*
RMI
 */
    public static void setRMIManager(ServerManagerRMI man) {
        manager = man;
    }

    public static String register(String username, String psw)
            throws RemoteException, InvalidKeySpecException, NoSuchAlgorithmException
    {
        CSReturnValues ret = this.server.register(username, psw);
        if(CSReturnValues.REGISTRATION_OK == ret)
            this.manager.newUserCallbacks(username);

        return ret.toString();
    }

    public static Map<String, Boolean> getStateUsers() {
        return this.server.getStateUsers();
    }
/*
TCP
 */
    public static String login(String username, String psw) throws RemoteException, InvalidKeySpecException, NoSuchAlgorithmException {
        CSReturnValues ret = this.server.login(username, psw);
        if(CSReturnValues.LOGIN_OK == ret)
            this.manager.userIsOnlineCallbacks(username);

        return ret.toString();
    }

    public static synchronized String createProject(String username, String projectName) {
        return this.server.createProject(username, projectName).toString();
    }

    public static synchronized List<ListProjectEntry> listProjects(String username)
            throws IllegalUsernameException
    {
        return this.server.listProjects(username);
    }

    public static synchronized Project showProject(String username, String projectName)
            throws IllegalProjectException, IllegalUsernameException
    {
        return this.server.showProject(username, projectName);
    }

    public static synchronized String moveCard(String username, String projectName,
                                        String cardName, CardStatus from, CardStatus to)
    {
        try {
            return this.server.moveCard(username, projectName, cardName, from ,to).toString();
        }
        catch (IllegalOperation ill) {
            return ill.retval.toString();
        }
    }

    public static synchronized String addCard(String username, String projectName,
                                       String cardName, String description)
    {
        return this.server.addCard(username, projectName, cardName, description).toString();
    }

    public static synchronized String deleteProject(String username, String projectName) {
        return this.server.deleteProject(username, projectName).toString();
    }

    public static synchronized List<String> showMembers(String username, String projName)
            throws IllegalProjectException, IllegalUsernameException
    {
        return this.server.showMembers(username, projName);
    }

    public static synchronized String addMember(String username, String projectName, String newMember) throws IOException {
        return this.server.addMember(username, projectName, newMember).toString();
    }

/*
 ******************************************** EXIT OPERATIONS
 */
    public static String logout(String username) throws RemoteException {
        CSReturnValues ret = this.server.logout(username);
        if(CSReturnValues.LOGOUT_OK == ret)
            this.manager.userIsOfflineCallbacks(username);

        return ret.toString();
    }

    public static String getProjectMulticasIp(String projectName) {
        return this.server.getProjectMulticasIp(projectName);
    }

    public static int getProjectMulticastPort(String projectName) {
        return this.server.getProjectMulticastPort(projectName);
    }

/*
    SERIALIZATION
 */
    public static WorthData getWorthData() { return this.server.getWorthData(); }
}

/*
public static Map<String, Project> getProjects() {
    return this.server.getProjects();
}

public static Map<String, UserInfo> getUsers() { return this.server.getUsers(); }
 */
