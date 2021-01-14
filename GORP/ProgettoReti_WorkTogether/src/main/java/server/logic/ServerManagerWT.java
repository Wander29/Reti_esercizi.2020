package server.logic;

/**
 * @author      LUDOVICO VENTURI (UniPi)
 * @date        2021/01/14
 * @versione    1.0
 */

import protocol.CSReturnValues;
import protocol.classes.CardStatus;
import protocol.classes.ListProjectEntry;
import protocol.classes.Project;
import protocol.exceptions.IllegalOperation;
import server.data.WorthData;
import server.logic.rmi.ServerManagerRmi;
import protocol.exceptions.IllegalProjectException;
import protocol.exceptions.IllegalUsernameException;

import java.io.IOException;
import java.rmi.RemoteException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;
import java.util.Map;

/*
    this class manages concurrency for server data.
    It's the unique class that can directly acccess its data
 */

// singleton
public class ServerManagerWT {
    private static volatile ServerManagerWT instance = null;

    private static ServerWT server              = null;
    private static ServerManagerRmi managerRMI  = null;

    private ServerManagerWT( ) {
        server      = ServerWT.getInstance();
        try {
            managerRMI  = ServerManagerRmi.getInstance();
        }
        catch (RemoteException e)   { e.printStackTrace(); }
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
    public static synchronized String register(String username, String psw)
            throws RemoteException, InvalidKeySpecException, NoSuchAlgorithmException
    {
        CSReturnValues ret = server.register(username, psw);
        if(CSReturnValues.REGISTRATION_OK == ret)
            managerRMI.newUserCallbacks(username);

        return ret.toString();
    }

    public static synchronized Map<String, Boolean> getStateUsers() {
        return server.getStateUsers();
    }
/*
TCP
 */
    public static synchronized String login(String username, String psw)
            throws RemoteException, InvalidKeySpecException, NoSuchAlgorithmException
    {
        CSReturnValues ret = server.login(username, psw);
        if(CSReturnValues.LOGIN_OK == ret)
            managerRMI.userIsOnlineCallbacks(username);

        return ret.toString();
    }

    public static synchronized String createProject(String username, String projectName) {
        return server.createProject(username, projectName).toString();
    }

    public static synchronized List<ListProjectEntry> listProjects(String username)
            throws IllegalUsernameException
    {
        return server.listProjects(username);
    }

    public static synchronized Project showProject(String username, String projectName)
            throws IllegalProjectException, IllegalUsernameException
    {
        return server.showProject(username, projectName);
    }

    public static synchronized String moveCard(String username, String projectName,
                                        String cardName, CardStatus from, CardStatus to)
    {
        try {
            return server.moveCard(username, projectName, cardName, from ,to).toString();
        }
        catch (IllegalOperation ill)    { return ill.retval.toString(); }
    }

    public static synchronized String addCard(String username, String projectName,
                                       String cardName, String description)
    {
        return server.addCard(username, projectName, cardName, description).toString();
    }

    public static synchronized String deleteProject(String username, String projectName) {
        return server.deleteProject(username, projectName).toString();
    }

    public static synchronized List<String> showMembers(String username, String projName)
            throws IllegalProjectException, IllegalUsernameException
    {
        return server.showMembers(username, projName);
    }

    public static synchronized String addMember(String username, String projectName, String newMember) throws IOException {
        return server.addMember(username, projectName, newMember).toString();
    }

/*
 ******************************************** EXIT OPERATIONS
 */
    public static String logout(String username) throws RemoteException {
        CSReturnValues ret = server.logout(username);
        if(CSReturnValues.LOGOUT_OK == ret)
            managerRMI.userIsOfflineCallbacks(username);

        return ret.toString();
    }

/*
    utils
 */
    public static String getProjectMulticasIp(String projectName) {
        return server.getProjectMulticasIp(projectName);
    }

    public static int getProjectMulticastPort(String projectName) {
        return server.getProjectMulticastPort(projectName);
    }

/*
    SERIALIZATION
 */
    public static WorthData getWorthData() { return server.getWorthData(); }
}
