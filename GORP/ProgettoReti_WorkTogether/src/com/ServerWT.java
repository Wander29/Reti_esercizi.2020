package com;

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
        Boolean         isOnline;
        List<Project>   projects;   // projects of which he is a member

        private UserInfo(String us, byte[] psw) {
            this.username   = us;
            this.psw        = psw;
            this.isOnline   = Boolean.FALSE;
            this.projects   = new ArrayList<>();
        }
    }

    // struttura dati per le informazioni di login degli utenti
    Map<String, UserInfo> users;
    // struttura dati per i progetti
    Map<String, Project> projects;
    // contiene tutti gli utenti ed il loro stato
    Map<String, Boolean> onlineStateUsers;

    public ServerWT() {
        this.users              = new ConcurrentHashMap<String, UserInfo>();
        this.onlineStateUsers   = new ConcurrentHashMap<String, Boolean>();

        this.projects           = new HashMap<String, Project>();
    }

    /**
     * registers a user into the service, if not already present
     *
     * @param username
     * @param psw
     * @return          USERNAME_ALREADY_PRESENT
     *                  REGISTRATION_OK
     */
    public int register(String username, byte[] psw) {

        if(this.users.containsKey(username))
            return ClientServerErrorCodes.USERNAME_ALREADY_PRESENT();

        this.users.put(username, new UserInfo(username, psw));
        if(ClientServerProtocol.DEBUG()) {
            System.out.println(username + " si è registrato");
        }
        this.onlineStateUsers.put(username, Boolean.FALSE);

        //users.putIfAbsent(username, new UserInfo(username, name, surname, psw));
        return ClientServerErrorCodes.REGISTRATION_OK();
    }

    /**
     * Allow a user to login into the service, if he was already registered and
     *  the password is correct
     *
     * @param username
     * @param psw
     * @return          USERNAME_NOT_PRESENT
     *                  LOGIN_OK
     */
    public CSReturnValues login(String username, String psw) {
        if(!this.users.containsKey(username))
            return CSReturnValues.USERNAME_NOT_PRESENT;

        UserInfo user_found = this.users.get(username);

        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        byte[] pswInBytes = md.digest(psw.getBytes());

        if(!Arrays.equals(user_found.psw, pswInBytes)) {
            System.out.println(username + " psw errata");
            return CSReturnValues.PSW_INCORRECT;
        }

        if(ClientServerProtocol.DEBUG()) {
            System.out.println(username + " si è loggato");
        }
        user_found.isOnline = Boolean.TRUE;
        this.onlineStateUsers.put(username, Boolean.TRUE);

        return CSReturnValues.LOGIN_OK;
    }

    /*
    try to create a new project
    IF there is no projectName already in database
        create new project and put the creatore as a member
     */
    public CSReturnValues createProject(String username, String projectName) {
        if(!this.users.containsKey(username))
            return CSReturnValues.USERNAME_NOT_PRESENT;

        if(this.projects.containsKey(projectName)) {
            return CSReturnValues.PROJECT_ALREADY_PRESENT;
        }

        this.projects.put(username, new Project(projectName));

        return CSReturnValues.CREATE_PROJECT_OK;
    }

    /**
     * Allow a user to logout from the service
     *
     * @param username
     * @return          USERNAME_NOT_PRESENT
     *                  LOGIN_OK
     */
    public CSReturnValues logout(String username) {
        if(!this.users.containsKey(username))
            return CSReturnValues.USERNAME_NOT_PRESENT;

        this.onlineStateUsers.replace(username, Boolean.FALSE);
        this.users.get(username).isOnline = Boolean.FALSE;

        if(ClientServerProtocol.DEBUG()) {
            System.out.println(username + " ha effettuato il logout");
        }

        return CSReturnValues.LOGOUT_OK;
    }

    /*
        returns an unmodifiable reference to map of users with their state
     */
    public Map<String, Boolean> getStateUsers() {
        return Collections.unmodifiableMap(this.onlineStateUsers);
    }
}
