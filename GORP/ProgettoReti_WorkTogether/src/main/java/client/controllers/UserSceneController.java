package client.controllers;

import client.data.ChatMsgObservable;
import client.data.UserObservable;
import com.jfoenix.controls.*;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import protocol.CSReturnValues;
import protocol.classes.ChatMsg;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import protocol.exceptions.IllegalProtocolMessageException;
import protocol.classes.ListProjectEntry;
import protocol.exceptions.IllegalProjectException;
import protocol.exceptions.IllegalUsernameException;

import java.io.IOException;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class UserSceneController extends ClientController {
/*
    NODES
 */
    @FXML
    private JFXTabPane tabPaneMain;
    @FXML
    private JFXTabPane tabPaneShowProject;
    @FXML
    private Label labelChosenProject;
    @FXML
    private JFXButton btnCancel;
    @FXML
    private JFXToggleButton toggleOnlineMembersOnly;
    @FXML
    private JFXButton bntAddMember;
    @FXML
    private JFXButton btnAddProject;
    @FXML
    private JFXButton btnAddCArd;
    @FXML
    private JFXComboBox<String> comboChooseProject;
    @FXML
    private JFXButton btnExit;
    @FXML
    private Label labelYourUsername;

/*
    Cards
 */
    ObservableList<String> toDoList         = FXCollections.observableArrayList();
    ObservableList<String> inProgressList   = FXCollections.observableArrayList();
    ObservableList<String> toBeRevisedList  = FXCollections.observableArrayList();
    ObservableList<String> doneList         = FXCollections.observableArrayList();

    @FXML
    private JFXListView<String> toDoListView;
    @FXML
    private JFXListView<String> inProgressListView;
    @FXML
    private JFXListView<String> toBeRevisedListView;
    @FXML
    private JFXListView<String> doneListView;

/*
    Members
 */
    private ObservableList<UserObservable> currProjectMembers = FXCollections.observableArrayList();
    @FXML
    private TableView<UserObservable> membersProjectTableView;
    @FXML
    private TableColumn<UserObservable, String> usernameMemberProjectColumn;
    @FXML
    private TableColumn<UserObservable, String> stateMemberProjectColumn;

/*
    CHAT table
 */
    private ObservableList<ChatMsgObservable> chatMessagesCurrentProject = FXCollections.observableArrayList();
    @FXML
    private TableView<ChatMsgObservable> chatTableView;
    @FXML
    private TableColumn<ChatMsgObservable, String> chatUsernameCol;
    @FXML
    private TableColumn<ChatMsgObservable, String> chatMessageCol;
    @FXML
    private TableColumn<ChatMsgObservable, String> chatSentTimeCol;
    @FXML
    private JFXTextArea chatTextArea;
    @FXML
    private JFXButton btnSendChat;

/*
  PROJECTS list
*/
    private List<ListProjectEntry> projects = null;
    private ObservableList<String> projectNames = FXCollections.observableArrayList();
    private SimpleStringProperty currentProject = new SimpleStringProperty(null);

/*
    controller FUNCTIONS
 */
    public UserSceneController() {
        super();
    }

    // called on stage close
    @Override
    public void handleCloseRequest() {
        System.out.println("[CONTROLLER - UserScene] sto gestendo la chiusura");
        try {
            super.handleCloseRequest();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initialize() {
        // rmi: callbacks registration
        try { clientLogic.registerForCallbacks(); }
        catch (RemoteException e) { e.printStackTrace(); }

        // starts chat manager
        try {
            clientLogic.startChatManager();
        }
        catch (IOException e)   {  e.printStackTrace(); closeStage(comboChooseProject); }
        catch (SQLException t)  { t.printStackTrace(); }

        // listeners for TabPanes
        tabPaneShowProjectInit();

    /*
    PROJECTS
     */
        // binds combobox items to list of project names
        listProjects();
        comboChooseProject.setItems(projectNames);

        // starts card lists
        listViewsInit();

        // starts chat table
        chatInitTable();

        // starts member project table
        memberProjectTableInitTable();

        // listeners for toggleButtons
        toggleMembersInit();

    /*
    USERS
     */

    /*
    SETTINGS
     */
        // sets username in app
        labelYourUsername.setText(this.username);
    }

/*
    initialize routins
 */
    private void tabPaneShowProjectInit() {
        tabPaneShowProject.getSelectionModel().selectedItemProperty().
                addListener((ov, oldTab, newTab) ->
        {
            System.err.println("tabPaneShowProject changed: " + newTab.getText());

            switch(newTab.getText()) {
                case "PROGETTO":
                    listProjects();
                    // JFXDepthManager.setDepth(tableViewTODOlist, 1);
                    break;

                case "CHAT":
                    if(this.currentProject.get() == null) {
                        showDialogNoProjectSelected();
                    }
                    else fillChatTable();
                    break;

                case "MEMBRI":
                    if(this.currentProject.get() == null) {
                        showDialogNoProjectSelected();
                        return;
                    }
                    else fillProjectMembers();
                    break;

                default:
                    System.err.println("changed: " + newTab.getText());
                    break;
            }
        });
    }

    private void toggleMembersInit() {
        toggleOnlineMembersOnly.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
                if(toggleOnlineMembersOnly.isSelected())
                {
                    if(currProjectMembers.isEmpty())
                        return;

                    Iterator it = currProjectMembers.iterator();
                    while(it.hasNext())
                    {
                        UserObservable user = (UserObservable) it.next();
                        if(user.getStato() == "offline")
                            it.remove();
                    }
                }
                else {
                    fillProjectMembers();
                }
            }
        });
    }

    //@todo not used
    private void setOnlineImageColumn(TableColumn column) {
        column.setCellFactory(col -> {
            TableCell<UserObservable, String> cell = new TableCell<>();
            cell.itemProperty().addListener((obs, old, newVal) -> {
                if (newVal != null && newVal.equals("online")) {
                    Node centreBox = createPriorityGraphic();
                    cell.graphicProperty().bind(Bindings.when(cell.emptyProperty()).then((Node) null).otherwise(centreBox));
                }
            });
            return cell;
        });
    }

    private void chatInitTable() {
        // initialize chat table for data
        chatUsernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        chatMessageCol.setCellValueFactory(new PropertyValueFactory<>("message"));
        chatSentTimeCol.setCellValueFactory(new PropertyValueFactory<>("timeSent"));

        chatTableView.setItems(chatMessagesCurrentProject);
        chatTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    private void memberProjectTableInitTable() {
        // initialize chat table for data
        usernameMemberProjectColumn.setCellValueFactory(new PropertyValueFactory<UserObservable, String>("username"));
        stateMemberProjectColumn.setCellValueFactory(new PropertyValueFactory<UserObservable, String>("stato"));

        membersProjectTableView.setItems(currProjectMembers);
        membersProjectTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    private void listViewsInit() {
        toDoListView.setItems(toDoList);
        inProgressListView.setItems(inProgressList);
        toBeRevisedListView.setItems(toBeRevisedList);
        doneListView.setItems(doneList);
    }

/*
    runtime routins
 */
    private void listProjects() {
    try {
        List<ListProjectEntry> updatedProjects = clientLogic.listProjects();
        if(this.projects == null) {
            System.out.println("projects null");
            for(ListProjectEntry entry : updatedProjects)
            {
                clientLogic.startChatConnection(entry);
                this.projectNames.add(entry.project);
            }
        }
        else {
            for(ListProjectEntry entry : updatedProjects)
            {
                if(!this.projectNames.contains(entry.project)) {
                    clientLogic.startChatConnection(entry);
                    this.projectNames.add(entry.project);
                }
            }
        }
        for(String s : this.projectNames)
        {
            System.out.println(s);
        }

        this.projects = updatedProjects;

    } catch (IOException e) {
        e.printStackTrace();
    } catch (IllegalUsernameException e) {
        showDialogUsernameNotPresent();
    } catch (IllegalProtocolMessageException e) {
        e.printStackTrace();
    }
}

    private void fillChatTable() {
        if(this.currentProject.get() != null) {
            try {
                List<ChatMsg> msgs = clientLogic.readChat(this.currentProject.get());
                chatMessagesCurrentProject.clear();

                for(ChatMsg msg : msgs) {
                    chatMessagesCurrentProject.add(new ChatMsgObservable(
                            msg.username,
                            msg.msg,
                            msg.getTimeFormatted()
                    ));
                }

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    private void fillProjectMembers() {
        if(this.currentProject.get() == null)
            return;

        try {
            List<String> members = clientLogic.showMembers(this.currentProject.get());
            Set<String> onlineUsers = clientLogic.listOnlineUsers();

            members.remove(this.username);

            currProjectMembers.clear();
            for(String s : members) {
                // get member name and see if he's online

                if(onlineUsers.contains(s)) {
                    System.out.println("user online: " + s);
                    currProjectMembers.add(new UserObservable(s, true));
                }
                else
                    currProjectMembers.add(new UserObservable(s, false));
            }

        }
        catch (IOException e)               { e.printStackTrace(); }
        catch (IllegalUsernameException e)  { showDialogUsernameNotPresent(); }
        catch (IllegalProjectException e)   { showDialogProjectNotPresent(); }
    }

/*
    UTILS
 */
    private void showDialogProjectNotPresent() {
        Alert info = new Alert(Alert.AlertType.INFORMATION);
        info.setHeaderText("Progetto NON presente");
        info.setContentText("L'operazione richiesta non è stata completata");
        info.showAndWait();
    }

    private void showDialogUsernameNotPresent() {
        Alert info = new Alert(Alert.AlertType.INFORMATION);
        info.setHeaderText("Nome utente NON presente");
        info.setContentText("L'operazione richiesta non è stata completata");
        info.showAndWait();
    }

    private Node createPriorityGraphic(){
        HBox graphicContainer = new HBox();
        graphicContainer.setAlignment(Pos.CENTER);
        ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream("../../images/user.png")));
        imageView.setFitHeight(40);
        imageView.setPreserveRatio(true);
        graphicContainer.getChildren().add(imageView);
        return graphicContainer;
    }

/*
    HANDLERS
 */
    /*
    PROJECTS
     */
        /*
        Project
         */

    @FXML
    void handleClickBtnAddProject(MouseEvent event) {
        TextInputDialog dialog = new TextInputDialog("");
        dialog.setHeaderText("Inserisci il nome del progetto");
        dialog.setTitle("Aggiungi progetto");

        Optional<String> result = dialog.showAndWait();
        if(result.isEmpty())
            return;

        String projName = result.get();
        if(projName == null || projName == "")
            return;

        // we have new project name
        try {
            CSReturnValues retVal = clientLogic.createProject(projName);
            switch (retVal) {

                case USERNAME_NOT_PRESENT:
                    showDialogUsernameNotPresent();
                    return;

                case PROJECT_ALREADY_PRESENT:
                    Alert info = new Alert(Alert.AlertType.INFORMATION);
                    info.setHeaderText("Esiste già un progetto di nome: " + projName);
                    info.setContentText("L'operazione richiesta non è stata completata");
                    info.showAndWait();
                    return;

                case SERVER_INTERNAL_NETWORK_ERROR:
                    showAlertNetworkError();
                    return;

                case CREATE_PROJECT_OK:
                    listProjects();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleBtnAddCard(ActionEvent event) {

    }

    @FXML
    void handleComboChooseProject(ActionEvent event) {
        this.currentProject.set(comboChooseProject.getValue());
        labelChosenProject.textProperty().bind(this.currentProject);

        System.out.println("COMBO BOX: " + this.currentProject.get());
        // @todo showCards()

        // List<Cards> clientLogic.showCards(this.currentProject.get());

    }

    @FXML
    void handleAddMember(MouseEvent event) {
        TextInputDialog dialog = new TextInputDialog("");
        dialog.setHeaderText("Inserisci il nome utente");
        dialog.setTitle("Aggiungi membro al progetto: " + this.currentProject.get());

        Optional<String> result = dialog.showAndWait();

        if(result.isEmpty())
            return;

        String member = result.get();
        if(member == null || member == "")
            return;

        try {
            CSReturnValues retVal = clientLogic.addMember(this.currentProject.get(), member);
            switch(retVal) {

                case USERNAME_INVALID:
                    Alert info = new Alert(Alert.AlertType.INFORMATION);
                    info.setHeaderText("Nome utente non valido");
                    info.setContentText("L'operazione richiesta non è stata completata");
                    info.showAndWait();
                    return;

                case USERNAME_NOT_PRESENT:
                    showDialogUsernameNotPresent();
                    return;

                case PROJECT_NOT_PRESENT:
                    showDialogProjectNotPresent();
                    return;

                case USERNAME_ALREADY_PRESENT:
                    Alert info2 = new Alert(Alert.AlertType.INFORMATION);
                    info2.setHeaderText("L'utente " + member + " è già membro di questo progetto");
                    info2.setContentText("L'operazione richiesta non è stata completata");
                    info2.showAndWait();
                    return;

                case ADD_MEMBER_OK:
                    fillProjectMembers();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

        /*
        Chat
         */
    @FXML
    void enableSendButton(KeyEvent event) {
        if(!chatTextArea.getText().isEmpty())
            btnSendChat.setDisable(false);
        else
            btnSendChat.setDisable(true);
    }

    void showDialogNoProjectSelected() {
        Alert info = new Alert(Alert.AlertType.INFORMATION);
        info.setHeaderText("NON è stato selezionato alcun Progetto");
        info.setContentText("Scegli un progetto dal menù a tendina nella schermata «PROGETTO»");
        info.showAndWait();
    }

    @FXML
    void handleSendChat(MouseEvent event) {
        if(this.currentProject.get() == null) {
            showDialogNoProjectSelected();
            return;
        }

        String textMsg = chatTextArea.getText();

        try {
            clientLogic.sendChatMsg(this.currentProject.get(), textMsg);

            chatTextArea.setText("");
            btnSendChat.setDisable(true);
        }
        catch (IOException e)       { e.printStackTrace(); }
    }

        /*
        Members
         */


    /*
    USERS
     */


    /*
   SETTINGS
    */
    @FXML
    void handleExit(MouseEvent event) {
        Stage stage = (Stage) btnExit.getScene().getWindow();
        stage.close();
    }
}

/*
// adds listener for list projects
        projectNames.addListener((InvalidationListener) observable -> {
            System.out.println("spara");
        });

// NUOVO PROGETTO
            String ip = "239.21.21.21;9999";
            ByteBuffer bb = ByteBuffer.allocate(512);
            bb.clear();
            bb.put(ip.getBytes());
            bb.flip();
            // writes the data into a sink channel.
            while(bb.hasRemaining()) {
                pipe_writeChannel.write(bb);
            }

            // writes the data into a sink channel.
            while(bb.hasRemaining()) {
                pipe_writeChannel.write(bb);
            }

// INVIO UDP CHAT

            InetAddress ia = InetAddress.getByName("239.21.21.21");
            String send = "CHAT_MSG;wander;Rancore;" + Long.toString(System.currentTimeMillis()) +
                    ";la chat è avviata!";

            byte[] data = StringUtils.stringToBytes(send);

            DatagramPacket dp = new DatagramPacket(data, data.length, ia, 9999);
            DatagramSocket ms = new DatagramSocket();
            ms.send(dp);

            Thread.sleep(1000);

// RECUPERO MESSAGGI CHAT
            try {
                List<ChatMsg> messages = DbHandler.getInstance().readChat("Rancore");
                for(ChatMsg msg : messages) {
                    DateFormat df = new SimpleDateFormat("HH:mm");

                    System.out.println(
                            "USERNAME: " + msg.username +
                            " - PROJECT: " + msg.project +
                            " - TIMESENT: " + df.format(msg.sentTime) +
                            "\n" + msg.msg);
                }

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
 */


 /*
    FOR TABLES

    // for tables
    ObservableList<cardEntry> list = FXCollections.observableArrayList();

    private void initColumn() {
        tableColTODOlist.setCellValueFactory(new PropertyValueFactory<>("Name"));
    }


    private void loadData() {
        list.clear();
        list.add(new cardEntry("luca"));

        tableViewTODOlist.setItems(list);
    }


    public class cardEntry {
        private final SimpleStringProperty name;

        public cardEntry(String s) {
            this.name = new SimpleStringProperty(s);
        }

        public String getName() {
            return name.get();
        }

    }

*/
