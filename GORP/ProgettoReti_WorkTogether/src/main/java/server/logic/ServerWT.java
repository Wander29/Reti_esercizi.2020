package server.logic;

import protocol.CSProtocol;
import protocol.CSReturnValues;
import server.data.Project;

import java.net.UnknownHostException;
import java.util.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ConcurrentHashMap;

/*
    this object class will be managed by a manager, which will use the object as private
 */

public class ServerWT {
    private class UserInfo {
        String          username;
        byte[]          psw;
        List<Project>   projects;   // projects of which he is a member

        private UserInfo(String us, byte[] psw) {
            this.username   = us;
            this.psw        = psw;
            this.projects   = new ArrayList<>();
        }
    }

    Map<String, UserInfo> users;    // users login info
    Map<String, Project> projects;  // project datas
    Map<String, Boolean> onlineStateUsers;  // (all) users online state

    MessageDigest md = null;        // useful to hash psws

    public ServerWT() {
        this.users              = new ConcurrentHashMap<String, UserInfo>();
        this.onlineStateUsers   = new ConcurrentHashMap<String, Boolean>();

        this.projects           = new HashMap<String, Project>();

        try { this.md = MessageDigest.getInstance("SHA-256"); }
        catch (NoSuchAlgorithmException e) { e.printStackTrace(); }
    }

    /**
     * registers a user into the service, if not already present
     *
     * @param username
     * @param psw
     * @return          USERNAME_ALREADY_PRESENT
     *                  REGISTRATION_OK
     */
    public CSReturnValues register(String username, String psw) {

        if(this.users.containsKey(username))
            return CSReturnValues.USERNAME_ALREADY_PRESENT;

        // hashing psw
        byte[] pswInBytes = md.digest(psw.getBytes());
        this.users.put(username, new UserInfo(username, pswInBytes));

        this.onlineStateUsers.put(username, Boolean.FALSE);

        if(CSProtocol.DEBUG()) {
            System.out.println(username + " si è registrato");
        }

        return CSReturnValues.REGISTRATION_OK;
    }

    /**
     * Allow a user to login into the service, if he was already registered,
     *  the password is correct and he wasn't already online
     */
    public CSReturnValues login(String username, String psw) {
        if(!this.users.containsKey(username))
            return CSReturnValues.USERNAME_NOT_PRESENT;

        if(this.onlineStateUsers.get(username) == Boolean.TRUE) {
            return CSReturnValues.ALREADY_LOGGED_IN;
        }

        // hashing psw
        byte[] pswInBytes = md.digest(psw.getBytes());
        UserInfo user_found = this.users.get(username);

        // compare byte per byte
        if(!Arrays.equals(user_found.psw, pswInBytes)) {
            System.out.println(username + " psw errata");
            return CSReturnValues.PSW_INCORRECT;
        }
        this.onlineStateUsers.put(username, Boolean.TRUE);

        if(CSProtocol.DEBUG()) {
            System.out.println(username + " si è loggato");
        }

        return CSReturnValues.LOGIN_OK;
    }

    /*
    try to create a new project
    IF there is no projectName already in database
        create new project and put the creatore as a member
        Next assign a Multicast IP to the project
     */
    public CSReturnValues createProject(String username, String projectName)
            throws UnknownHostException, NoSuchElementException
    {
        if(!this.users.containsKey(username))
            return CSReturnValues.USERNAME_NOT_PRESENT;

        if(this.projects.containsKey(projectName)) {
            return CSReturnValues.PROJECT_ALREADY_PRESENT;
        }

        this.projects.put(projectName, new Project(projectName, username));
        if(CSProtocol.DEBUG()) {
            System.out.println(projectName + " è statoc creato");
        }

        return CSReturnValues.CREATE_PROJECT_OK;
    }

    /*
        get the MulticastIP of a specific project and returns it as String
        ex. -> "224.0.0.0"
     */
    public String getProjectMulticasIp(String projectName) {
        if(this.projects.containsKey(projectName)) {
            return this.projects.get(projectName).getMulticastIP().toString();
        }
        return null;
    }

    /**
     * Allow a user to logout from the service
     */
    public CSReturnValues logout(String username) {
        if(!this.users.containsKey(username))
            return CSReturnValues.USERNAME_NOT_PRESENT;

        this.onlineStateUsers.replace(username, Boolean.FALSE);

        if(CSProtocol.DEBUG()) {
            System.out.println(username + " ha effettuato il logout");
        }

        return CSReturnValues.LOGOUT_OK;
    }

    /*
        returns an unmodifiable reference to the map of users and their state
     */
    public Map<String, Boolean> getStateUsers() {
        return Collections.unmodifiableMap(this.onlineStateUsers);
    }
}
