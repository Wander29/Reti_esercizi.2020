package server.logic;

import protocol.CSProtocol;
import protocol.CSReturnValues;
import protocol.classes.CardStatus;
import protocol.classes.ListProjectEntry;
import protocol.classes.Project;
import protocol.exceptions.IllegalOperation;
import server.data.ServerProject;
import server.data.UserInfo;
import server.data.WorthData;
import protocol.exceptions.IllegalProjectException;
import protocol.exceptions.IllegalUsernameException;
import utils.psw.PasswordManager;
import utils.psw.PswData;

import java.net.UnknownHostException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;
import java.util.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ConcurrentHashMap;

/*
    this object class will be managed by a manager, which will use an instance as private
 */

public class ServerWT {
    Map<String, Boolean> onlineStateUsers;      // users online state
                                                // when you start server every user is offline
    private WorthData data;
    Map<String, UserInfo> users;                // users login info
    Map<String, ServerProject > projects;       // project data

    public ServerWT(WorthData recoveredData) {
        this.onlineStateUsers   = new ConcurrentHashMap<String, Boolean>();

        if(recoveredData == null )
                data    = new WorthData();
        else
                data    = new WorthData(recoveredData);

        this.projects   = data.getProjects();
        this.users      = data.getUsers();

        for(Map.Entry<String, UserInfo> entry : this.users.entrySet())
            this.onlineStateUsers.put(entry.getKey(), Boolean.FALSE);


        try {
            insertSampleData();
        }
        catch (UnknownHostException e) { e.printStackTrace(); }
        catch (IllegalOperation illegalOperation) {
            illegalOperation.printStackTrace();
        }
    }

    private void insertSampleData() throws UnknownHostException, IllegalOperation {
        ServerProject  p = new ServerProject("Musica per Bambini", "Rancore");
        p.addMember("Orqestra");
        p.addMember("Rinquore");
        // add cards, 2 each column
        p.addCard("1 - Underman", "Il titolo è un gioco di parole in contrapposizione a «super-uomo».", "Rancore");
        p.addCard("2 - Giocattoli", "Nella traccia, l'artista impersona e dà voce a 3 oggetti, 3 “giocattoli” se vogliamo, di cui la ragazza narrata usufruisce nel corso della sua vita, della sua crescita, rispettivamente: un pupazzo, un rossetto ed una sigaretta.", "Rancore");
        p.addCard("3 - Beep Beep", "“Beep Beep”, in parte un extrabeat, è un brano in cui Rancore riprende metaforicamente i personaggi animati Beep Beep e Willy il Coyote: il primo rappresenta Rancore stesso, inseguito dai problemi e dai fantasmi (Willy il Coyote) del suo passato.", "Rancore");
        p.addCard("4 - Depressissimo", "La canzone affronta il tema della depressione in maniera auto-ironica.", "Rancore");
        p.addCard("5 - Sangue di drago", "“Sangue di drago” racconta, seguendo gli schemi cavallereschi medievali, di un principe trasformato inconsapevolmente in drago da un mago, affinché un altro principe, sostenuto da quest'ultimo, possa eliminarlo e ottenere potere e onore. Metafora che probabilmente ricostruisce le modalità con le quali molte volte viene preso il potere: con l'inganno. Il principe che ha ordinato l'incantesimo infatti ottiene l'appoggio del popolo, in quanto ha creato un problema e si è proposto come principe buono che deve salvare la principessa dal drago malvagio (soluzione) che in realtà è il virtuoso principe colpito dal sotterfugio.", "Rancore");

        p.moveCard("4 - Depressissimo", CardStatus.TO_DO, CardStatus.IN_PROGRESS, "Rinquore");
        p.moveCard("4 - Depressissimo", CardStatus.IN_PROGRESS, CardStatus.DONE, "Rancore");

        p.moveCard("5 - Sangue di drago", CardStatus.TO_DO, CardStatus.IN_PROGRESS, "Orqestra");
        p.addMember("aa");
        p.addMember("wander");

        this.projects.put("Musica per Bambini", p);
    }

/*
RMI
 */
    /**
     * registers a user into the service, if not already present
     *
     * @param username
     * @param psw
     * @return          USERNAME_ALREADY_PRESENT
     *                  REGISTRATION_OK
     */
    public CSReturnValues register(String username, String psw)
            throws InvalidKeySpecException, NoSuchAlgorithmException {

        if(this.users.containsKey(username))
            return CSReturnValues.USERNAME_ALREADY_PRESENT;

        // hashing psw
        PswData password = PasswordManager.hashPsw(psw);
        this.users.put(username, new UserInfo(username, password));
        this.onlineStateUsers.put(username, Boolean.FALSE);

        return CSReturnValues.REGISTRATION_OK;
    }

/*
TCP
 */
    /**
     * Allow a user to login into the service, if he was already registered,
     *  the password is correct and he wasn't already online
     */
    public CSReturnValues login(String username, String psw) throws InvalidKeySpecException, NoSuchAlgorithmException {
        if(!this.users.containsKey(username))
            return CSReturnValues.USERNAME_NOT_PRESENT;

        if(this.onlineStateUsers.get(username) == Boolean.TRUE) {
            return CSReturnValues.ALREADY_LOGGED_IN;
        }

        // hashing psw
        UserInfo user_found = this.users.get(username);

        // compare byte per byte
        if(!PasswordManager.comparePsw(psw, user_found.getPsw())) {
            System.out.println(username + " psw errata");
            return CSReturnValues.PSW_INCORRECT;
        }
        this.onlineStateUsers.put(username, Boolean.TRUE);

        return CSReturnValues.LOGIN_OK;
    }

    /*
   try to create a new project
   IF there is no projectName already in database
       create new project and put the creatore as a member
       Next assign a Multicast IP to the project
    */
    public CSReturnValues createProject(String username, String projectName)
    {
        if(!this.users.containsKey(username))
            return CSReturnValues.USERNAME_NOT_PRESENT;

        if(this.projects.containsKey(projectName)) {
            return CSReturnValues.PROJECT_ALREADY_PRESENT;
        }

        try {
            this.projects.put(projectName, new ServerProject(projectName, username));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        return CSReturnValues.CREATE_PROJECT_OK;
    }

    /*
    lists only projects where username is member
     */
    public List<ListProjectEntry> listProjects(String username)
            throws IllegalUsernameException
    {
        if(!this.users.containsKey(username))
            throw new IllegalUsernameException();

        List<ListProjectEntry> listProject = new ArrayList<>();

        for(Map.Entry<String, ServerProject> projEntry : projects.entrySet()) {
            ServerProject project = projEntry.getValue();

            if(project.getMembers().contains(username)) {

                String projName = project.getProjectName();

                listProject.add(new ListProjectEntry(
                        projName,
                        getProjectMulticasIp(projName),
                        project.getChatMulticastPort()
                ));
            }
        }

        return listProject;
    }

    public Project showProject(String username, String projectName)
            throws IllegalUsernameException, IllegalProjectException
    {

        if(!this.users.containsKey(username))
            throw new IllegalUsernameException();

        if(!this.projects.containsKey(projectName)) {
            throw new IllegalProjectException();
        }

        //        return this.projects.get(projectName);
        Project p = new Project(this.projects.get(projectName));
        return p;
    }

    public CSReturnValues moveCard(String username, String projectName,
                                   String cardName, CardStatus from, CardStatus to) throws IllegalOperation
    {
        if(!this.users.containsKey(username))
            return CSReturnValues.USERNAME_NOT_PRESENT;

        if(!this.projects.containsKey(projectName))
            return CSReturnValues.PROJECT_NOT_PRESENT;

        ServerProject proj = this.projects.get(projectName);
        if(!proj.isCardInFromStatus(cardName, from))
            return CSReturnValues.CARD_FROM_STATUS_OUTDATED;

        proj.moveCard(cardName, from, to, username);

        return CSReturnValues.MOVE_CARD_OK;
    }

    public List<String> showMembers(String username, String projectName)
            throws IllegalUsernameException, IllegalProjectException
    {
        if(!this.users.containsKey(username))
            throw new IllegalUsernameException();

        if(!this.projects.containsKey(projectName)) {
            throw new IllegalProjectException();
        }

        return this.projects.get(projectName).getMembers();
    }

    public CSReturnValues addMember(String username, String projectName, String newMember) {
        if(!this.users.containsKey(username))
            return CSReturnValues.USERNAME_NOT_PRESENT;

        if(!this.projects.containsKey(projectName)) {
            return CSReturnValues.PROJECT_NOT_PRESENT;
        }

        if(!this.users.containsKey(newMember))
            return CSReturnValues.USERNAME_INVALID;

        ServerProject proj = this.projects.get(projectName);
        if(proj.getMembers().contains(newMember)) {
            return CSReturnValues.USERNAME_ALREADY_PRESENT;
        }

        proj.addMember(newMember);

        return CSReturnValues.ADD_MEMBER_OK;
    }

    public CSReturnValues addCard(String username, String projectName,
                                  String cardName, String description)
    {
        if(!this.users.containsKey(username))
            return CSReturnValues.USERNAME_NOT_PRESENT;

        if(!this.projects.containsKey(projectName))
            return CSReturnValues.PROJECT_NOT_PRESENT;

        ServerProject proj = this.projects.get(projectName);
        if(proj.getCardFromAnyList(cardName) != null) {
            return CSReturnValues.CARD_ALREADY_PRESENT;
        }

        proj.addCard(cardName, description, username);

        return CSReturnValues.ADD_CARD_OK;
    }

    public CSReturnValues deleteProject(String username, String projectName)
    {
        if(!this.users.containsKey(username))
            return CSReturnValues.USERNAME_NOT_PRESENT;

        if(!this.projects.containsKey(projectName))
            return CSReturnValues.PROJECT_NOT_PRESENT;

        //  remove from map and add project ip to ipFree list
        this.projects.remove(projectName).delete();

        return CSReturnValues.DELETE_PROJECT_OK;
    }

/*
 ******************************************** EXIT OPERATIONS
 */
    /**
     * Allow a user to logout from the service
     */
    public CSReturnValues logout(String username) {
        if(!this.users.containsKey(username))
            return CSReturnValues.USERNAME_NOT_PRESENT;

        this.onlineStateUsers.replace(username, Boolean.FALSE);

        return CSReturnValues.LOGOUT_OK;
    }

    /*
    UTILS
     */
     /*
        get the MulticastIP of a specific project and returns it as String
        ex. -> "224.0.0.0"
     */
    public String getProjectMulticasIp(String projectName) {
        if(this.projects.containsKey(projectName)) {
            return this.projects.get(projectName).getChatMulticastIP().toString().substring(1);
        }
        return null;
    }

    public int getProjectMulticastPort(String projectName) {
        if(this.projects.containsKey(projectName))
            return this.projects.get(projectName).getChatMulticastPort();

        return -1;
    }

    /*
        returns an unmodifiable reference to the map of users and their state
     */
    public Map<String, Boolean> getStateUsers() {
        return Collections.unmodifiableMap(this.onlineStateUsers);
    }

    public WorthData getWorthData() { return this.data; }
}

/*
    public Map<String, Project> getProjects() {
        return this.projects;
    }

    public Map<String, UserInfo> getUsers() {
        return this.users;
    }
 */
