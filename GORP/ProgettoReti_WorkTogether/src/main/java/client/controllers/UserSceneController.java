package client.controllers;

import client.WorthClientMain;
import client.data.ChatMsgObservable;
import client.data.UserObservable;
import com.jfoenix.controls.*;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.css.PseudoClass;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.stage.StageStyle;
import protocol.CSReturnValues;
import protocol.classes.Card;
import protocol.classes.ChatMsg;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import protocol.classes.Project;
import protocol.exceptions.IllegalProtocolMessageException;
import protocol.classes.ListProjectEntry;
import protocol.exceptions.IllegalProjectException;
import protocol.exceptions.IllegalUsernameException;

import java.io.IOException;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.*;

public class UserSceneController extends ClientController {
/*
    NODES
 */
    @FXML
    private JFXTabPane tabPaneMain;
    @FXML
    private Tab tabMainProjects;
    @FXML
    private Tab tabUsers;
    @FXML
    private Tab tabInfo;

    @FXML
    private JFXTabPane tabPaneShowProject;
    @FXML
    private Tab tabProject;
    @FXML
    private Tab tabChat;
    @FXML
    private Tab tabMembers;

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
    @FXML
    private JFXButton btnUpdate;

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
    private ObservableList<String> projectNames = FXCollections.observableArrayList();
    private SimpleStringProperty currentProject = new SimpleStringProperty("");
    private Project projectInfo = null;

/*
    Users
 */
    private ObservableList<UserObservable> usersWorth = FXCollections.observableArrayList();
    @FXML
    private TableView<UserObservable> tableViewUsers;
    @FXML
    private TableColumn<UserObservable, String> usernameUsersCol;
    @FXML
    private TableColumn<UserObservable, String> stateUsersCol;
    @FXML
    private JFXToggleButton toggleOnlineUsers;

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
        tabPaneMainInit();

    /*
    PROJECTS
     */
        // binds combobox items to list of project names
        labelChosenProject.setText("");
        listProjects();
        comboChooseProject.setItems(projectNames);

        // starts card lists
        listViewsInit();

        // starts chat table
        chatInitTable();

        // starts member project table
        memberProjectTableInit();

        // starts users table
        usersTableInit();

        // listeners for toggleButtons
        toggleMembersInit();
        toggleUsersInit();

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
                    if(this.currentProject.get().isEmpty()) {
                        showDialogNoProjectSelected();

                        tabPaneShowProject.getSelectionModel().select(tabProject);
                    }
                    else fillChatTable();
                    break;

                case "MEMBRI":
                    if(this.currentProject.get().isEmpty()) {
                        showDialogNoProjectSelected();

                        tabPaneShowProject.getSelectionModel().select(tabProject);
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

    private void tabPaneMainInit() {
        tabPaneMain.getSelectionModel().selectedItemProperty().
                addListener((ov, oldTab, newTab) ->
                {
                    System.err.println("tabPaneMain changed: " + newTab.getText());

                    switch(newTab.getText()) {
                        case "PROGETTI":
                            listProjects();
                            break;

                        case "UTENTI WORTH":
                            fillUsersWorth();
                            break;

                        case "INFO":
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

    private void toggleUsersInit() {
        toggleOnlineUsers.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
                if(toggleOnlineUsers.isSelected())
                {
                    usersWorth.clear();
                    for(String user : clientLogic.listOnlineUsers()) {
                        usersWorth.add(new UserObservable(user, Boolean.TRUE));
                    }
                }
                else {
                    fillUsersWorth();
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

    private static final PseudoClass pseudoClassMine =  PseudoClass.getPseudoClass("mine");
    private void chatInitTable() {
        // initialize chat table for data
        chatUsernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        chatMessageCol.setCellValueFactory(new PropertyValueFactory<>("message"));
        chatSentTimeCol.setCellValueFactory(new PropertyValueFactory<>("timeSent"));

        // apply style for user's row
        chatTableView.setRowFactory(tableView -> {
            TableRow<ChatMsgObservable> row = new TableRow<>() {

                @Override
                public void updateItem(ChatMsgObservable msg, boolean empty) {

                    if(msg != null && msg.getUsername().equals(username))
                        pseudoClassStateChanged(pseudoClassMine, true);
                    else
                        pseudoClassStateChanged(pseudoClassMine, false);

                    super.updateItem(msg, empty);
                }
            };
            return row;
        });

        chatTableView.setItems(clientLogic.getChatCurrentMsgs());
        chatTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    private void memberProjectTableInit() {
        // initialize chat table for data
        usernameMemberProjectColumn.setCellValueFactory(new PropertyValueFactory<UserObservable, String>("username"));
        stateMemberProjectColumn.setCellValueFactory(new PropertyValueFactory<UserObservable, String>("stato"));

        // apply style for user's row
        tableRowStyleForUserRow(membersProjectTableView);

        membersProjectTableView.setItems(currProjectMembers);
        membersProjectTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    private void usersTableInit() {
        // initialize chat table for data
        usernameUsersCol.setCellValueFactory(new PropertyValueFactory<UserObservable, String>("username"));
        stateUsersCol.setCellValueFactory(new PropertyValueFactory<UserObservable, String>("stato"));

        // apply style for user's row
        tableRowStyleForUserRow(tableViewUsers);

        tableViewUsers.setItems(usersWorth);
        tableViewUsers.getSelectionModel().
                setSelectionMode(SelectionMode.SINGLE);
    }

    private static final PseudoClass pseudoClassMyself =  PseudoClass.getPseudoClass("myself");
    private void tableRowStyleForUserRow(TableView tv) {
        // apply style for user's row
        tv.setRowFactory(tableView -> {
            TableRow<UserObservable> row = new TableRow<>() {

                @Override
                public void updateItem(UserObservable user, boolean empty) {

                    if(user != null && user.getUsername().equals(username))
                        pseudoClassStateChanged(pseudoClassMyself, true);
                    else
                        pseudoClassStateChanged(pseudoClassMyself, false);

                    super.updateItem(user, empty);
                }
            };
            return row;
        });
    }

    private void listViewsInit() {
        addListenerListView(toDoListView);
        addListenerListView(inProgressListView);
        addListenerListView(toBeRevisedListView);
        addListenerListView(doneListView);

        toDoListView.setItems(toDoList);
        inProgressListView.setItems(inProgressList);
        toBeRevisedListView.setItems(toBeRevisedList);
        doneListView.setItems(doneList);
    }

    private void addListenerListView(ListView listView) {
        listView.setOnMouseClicked(e -> {
            String selectedCard = (String) listView.getSelectionModel().getSelectedItem();
            Card c = this.projectInfo.getCardFromAnyList(selectedCard);

            // once user selects a card -> open new window to handle it
            if(c != null) {
                CardShowController controller = loadNewCardShow();
                if(this.currentProject.get().isEmpty())
                    System.out.println("durante la selezione di una carta: non è stato selezionato il progetto!");

                controller.initShowCard(c, this.currentProject.get());
            }
        });
    }

/*
    runtime routins
 */
    private void listProjects() {
        try {
            List<ListProjectEntry> updatedProjects = clientLogic.listProjects();
            if(this.projectNames.isEmpty())
            {
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
        }
        catch (IOException e)                       { e.printStackTrace(); }
        catch (IllegalUsernameException e)          { showDialogUsernameNotPresent(); }
        catch (IllegalProtocolMessageException e)   { e.printStackTrace();}
    }

    private void fillCards() {
        if(this.projectInfo != null) {
            replaceCardList(this.projectInfo.getToDoCards(), toDoList);
            replaceCardList(this.projectInfo.getInProgressCards(), inProgressList);
            replaceCardList(this.projectInfo.getToBeRevisedCards(), toBeRevisedList);
            replaceCardList(this.projectInfo.getDoneCards(), doneList);

            if(toDoList.isEmpty() && inProgressList.isEmpty() && toBeRevisedList.isEmpty()) {
                btnCancel.setDisable(false);
            } else {
                btnCancel.setDisable(true);
            }
        }
    }

    private void replaceCardList(Map<String, Card> map, ObservableList<String> list) {

        List<String> tmp = new ArrayList<>();
        list.clear();

        for(String s : map.keySet()) {
            tmp.add(s);
        }
        list.addAll(tmp);
    }

    private void fillChatTable() {
        if(this.currentProject.get().isEmpty() )
            return;

        try {
            clientLogic.readChat(this.currentProject.get());

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void fillProjectMembers() {
        if(this.currentProject.get().isEmpty())
            return;

        try {
            List<String> members = clientLogic.showMembers(this.currentProject.get());
            Set<String> onlineUsers = clientLogic.listOnlineUsers();

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
        catch (IllegalProjectException e)   {
            showDialogProjectNotPresent();
            resetUIstate();
        }
    }

    private void fillUsersWorth() {

        usersWorth.clear();
        Map<String, Boolean> users = clientLogic.listUsers();

        for(Map.Entry<String, Boolean> user : users.entrySet()) {
            String username = user.getKey();
            Boolean isOnline = user.getValue();

            usersWorth.add(new UserObservable(username, isOnline));
        }
    }

/*
    UTILS
 */

    private void showDialogNoProjectSelected() {
        Alert info = new Alert(Alert.AlertType.INFORMATION);
        info.setHeaderText("NON è stato selezionato alcun Progetto");
        info.setContentText("Scegli un progetto dal menù a tendina nella schermata «PROGETTO»");
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

    protected CardShowController loadNewCardShow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../cardShow.fxml"));
            Parent parent = loader.load();

            CardShowController controller = loader.getController();

            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setTitle("Card");
            stage.getIcons().add(new Image(
                    WorthClientMain.class.getResourceAsStream("../images/notebook.png")));

            // when closing -> update listViews asking server for updates
            stage.setOnHiding(event -> {
                updateShownProject();
            });

            stage.setScene(new Scene(parent));
            stage.show();

            return controller;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void updateShownProject() {
        try {
            this.projectInfo = clientLogic.showProject(this.currentProject.get());
            fillCards();
        }
        catch (IOException e)               { e.printStackTrace(); }
        catch (IllegalUsernameException e)  { showDialogUsernameNotPresent(); }
        catch (IllegalProjectException e)   {
            showDialogProjectNotPresent();
            resetUIstate();
        }
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
        dialog.setTitle("Aggiungi progetto");
        dialog.setContentText("Nome del progetto: ");
        dialog.setHeaderText(null);
        dialog.setGraphic(null);

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
    void handleUpdateProjects(MouseEvent event) {
        listProjects();
        if(!this.currentProject.get().isEmpty())
            updateShownProject();
    }

    @FXML
    void handleBtnAddCard(ActionEvent event) {
        if(this.currentProject.get().isEmpty())
            return;

        try {
            // get new card name
            TextInputDialog dialogCardName = new TextInputDialog("");
            dialogCardName.setHeaderText(null);
            dialogCardName.setGraphic(null);
            dialogCardName.setContentText("Nome card: ");
            dialogCardName.setTitle("Aggiungi Card");
            Optional<String> resultName = dialogCardName.showAndWait();

            if(resultName.isEmpty())
                return;

            String cardName = resultName.get();
            if(cardName.isEmpty())
                return;

            // get new card description
            TextInputDialog dialogDescription = new TextInputDialog("");
            dialogDescription.setTitle("Aggiungi Card");
            dialogDescription.setContentText("Descrizione: ");
            dialogDescription.setHeaderText(null);
            dialogDescription.setGraphic(null);
            Optional<String> resultDescription = dialogDescription.showAndWait();

            if(resultDescription.isEmpty())
                return;

            String description = resultDescription.get();
            if(description.isEmpty())
                return;

            String projectName = this.currentProject.get();

            System.out.println("request: ADD CARD " + cardName + " descr: " + description);

            CSReturnValues retVal = clientLogic.addCard(projectName, cardName, description);

            switch(retVal) {

                case ADD_CARD_OK:
                    Alert info = new Alert(Alert.AlertType.INFORMATION);
                    info.setContentText("Card aggiunta");
                    info.setHeaderText(null);
                    info.showAndWait();

                    updateShownProject();
                    break;

                case USERNAME_NOT_PRESENT:
                    showDialogUsernameNotPresent();
                    return;

                case PROJECT_NOT_PRESENT:
                    showDialogProjectNotPresent();
                    resetUIstate();
                    return;

                case CARD_ALREADY_PRESENT:
                    Alert infoCard = new Alert(Alert.AlertType.INFORMATION);
                    infoCard.setHeaderText("Card «" + cardName + "» già presente");
                    infoCard.setContentText("L'operazione richiesta non è stata completata");
                    infoCard.showAndWait();
                    return;

                default:
                    return;
            }
        }
        catch (IOException e)           { e.printStackTrace(); }
    }

    @FXML
    void handleComboChooseProject(ActionEvent event) {
        this.currentProject.set(comboChooseProject.getValue());
        labelChosenProject.textProperty().bind(this.currentProject);
        System.out.println("COMBO BOX: " + this.currentProject.get());

        if(this.currentProject.get().isEmpty())
            return;

        updateShownProject();
    }

    @FXML
    void handleAddMember(MouseEvent event) {
        TextInputDialog dialog = new TextInputDialog("");
        dialog.setTitle("Aggiungi membro");
        dialog.setContentText("Nome utente: ");
        dialog.setHeaderText(null);
        dialog.setGraphic(null);

        Optional<String> result = dialog.showAndWait();

        if(result.isEmpty())
            return;

        String member = result.get();
        if(member.isEmpty())
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
                    showDialogNoProjectSelected();
                    resetUIstate();
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

    @FXML
    void handleDeleteProject(MouseEvent event) {

        if(this.currentProject.get().isEmpty())
            return;

        try {
            String projectName = this.currentProject.get();
            CSReturnValues retVal = clientLogic.deleteProject(projectName);

            switch(retVal) {

                case DELETE_PROJECT_OK:
                    Alert info = new Alert(Alert.AlertType.INFORMATION);
                    info.setContentText("Progetto cancellato");
                    info.setHeaderText(null);
                    info.showAndWait();


                    // @todo al momento della callback potrei non averne bisogno
                    this.projectNames.remove(projectName);
                    resetUIstate();
                    break;

                case USERNAME_NOT_PRESENT:
                    showDialogUsernameNotPresent();
                    return;

                case PROJECT_NOT_PRESENT:
                    showDialogNoProjectSelected();
                    resetUIstate();
                    return;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void resetUIstate() {
        comboChooseProject.setValue("");
        btnCancel.setDisable(true);

        toDoList.clear();
        inProgressList.clear();
        toBeRevisedList.clear();
        doneList.clear();

        listProjects();
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

    @FXML
    void handleSendChat(MouseEvent event) {
        if(this.currentProject.get().isEmpty()) {
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
