package client.controllers;

import client.model.ChatManager;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTabPane;
import com.jfoenix.effects.JFXDepthManager;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import server.data.UserInfo;
import utils.StringUtils;
import utils.exceptions.IllegalProjectException;
import utils.exceptions.IllegalUsernameException;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Pipe;
import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;

public class UserSceneController extends ClientController {
    private String currentProject;

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
    private JFXButton btnAddCArd;
    @FXML
    private JFXButton btnAddProject;
    @FXML
    private JFXComboBox<String> comboChooseProject;
    @FXML
    private JFXButton btnCancel;
    @FXML
    private VBox toDoVBox;
    @FXML
    private Label labelOne;
    @FXML
    private JFXButton btnExit;

/*
    controller FUNCTIONS
 */
    public UserSceneController() {
        super();
    }


    Pipe pipe;
    Pipe.SinkChannel pipe_writeChannel;

    public void initialize() {


        try {
            pipe = Pipe.open();
            pipe_writeChannel = pipe.sink();
            pipe_writeChannel.configureBlocking(false);
            ChatManager chatManager = new ChatManager(pipe);
            chatManager.start();

            String ip = "239.21.21.21;9999";
            ByteBuffer bb = ByteBuffer.allocate(512);
            bb.clear();
            bb.put(ip.getBytes());
            bb.flip();
            //write the data into a sink channel.
            while(bb.hasRemaining()) {
                pipe_writeChannel.write(bb);
            }

            //write the data into a sink channel.
            while(bb.hasRemaining()) {
                pipe_writeChannel.write(bb);
            }

            Thread.sleep(3000);


            InetAddress ia = InetAddress.getByName("239.21.21.21");
            byte[] data;
            data = "gattini".getBytes();

            DatagramPacket dp = new DatagramPacket(data, data.length, ia, 9999);
            DatagramSocket ms = new DatagramSocket();
            ms.send(dp);

        } catch (IOException e) {  e.printStackTrace(); }
        catch (InterruptedException e) {
            e.printStackTrace();
        }

        updateComboBoxListProjects();
        tabPaneShowProject.getSelectionModel().selectedItemProperty().addListener((ov, oldTab, newTab) -> {
            System.err.println("tabPaneShowProject changed: " + newTab.getText());

            switch(newTab.getText()) {
                case "PROGETTO":
                    updateComboBoxListProjects();
                    // JFXDepthManager.setDepth(tableViewTODOlist, 1);

                    break;

                case "CHAT":
                    break;

                case "MEMBRI":
                    fillProjectMemeber();
                    break;

                default:
                    System.err.println("changed: " + newTab.getText());
                    break;
            }

        });
    }

    @Override
    public void handleCloseRequest() {
        System.out.println("[CONTROLLER - UserScene] sto gestendo la chiusura");
        try {
            super.handleCloseRequest();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

/*
    HANDLERS
 */
    /**
     * retrieves new project name from dialog and asks server to create a new project.
     * Then calls a routine to update list of projects which user can select in relative combo box
     *
     * @param event mouse click
     */
    @FXML
    void handleClickBtnAddProject(MouseEvent event) {
        TextInputDialog dialog = new TextInputDialog("");
        dialog.setHeaderText("Inserisci il nome del progetto");
        dialog.setTitle("Aggiungi progetto");

        Optional<String> result = dialog.showAndWait();

        /*
        String projName = result.map(r -> {
            return r;
        }).orElse("senza nome");
         */
        String projName = result.get();
        // we have new project name
        try {
            clientLogic.createProject(projName);
            updateComboBoxListProjects();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param event onAction
     */
    @FXML
    void handleExit(MouseEvent event) {
        Stage stage = (Stage) btnExit.getScene().getWindow();
        stage.close();
    }

    @FXML
    void handleBtnAddCard(ActionEvent event) {

    }

    @FXML
    void handleComboChooseProject(ActionEvent event) {
        this.currentProject = comboChooseProject.getValue();
        labelChosenProject.setText(this.currentProject);

    }

    @FXML
    void handleLabelOne(MouseEvent event) {

    }

/*
    UTILS
 */
    /**
     * asks server to get list of projects, then updates combo box cells
     */
    private void updateComboBoxListProjects() {
        // ask for listProjects
        List<String> projects = null;
        try {
            projects = clientLogic.listProjects();

            // once you have the list, populate combo box
            comboChooseProject.getItems().clear();
            comboChooseProject.getItems().addAll(projects);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalUsernameException e) {
            showDialogUsernameNotPresentAndExit();
        }
    }

    private void fillProjectMemeber() {
        try {
            List<String> members = clientLogic.showMembers(this.currentProject);

            for(String s : members) {
                System.out.println(s);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalUsernameException e) {
            showDialogUsernameNotPresentAndExit();
        } catch (IllegalProjectException e) {
            showDialogProjectNotPresentAndExit();
        }
    }

    private void showDialogProjectNotPresentAndExit() {
        Alert info = new Alert(Alert.AlertType.INFORMATION);
        info.setHeaderText("Progetto NON presente");
        info.setContentText("C'è stato un problema, l'applicazione verrà chiusa");
        info.showAndWait();

        Stage stage = (Stage) btnExit.getScene().getWindow();
        stage.close();
    }

    private void showDialogUsernameNotPresentAndExit() {
        Alert info = new Alert(Alert.AlertType.INFORMATION);
        info.setHeaderText("Nome utente NON presente");
        info.setContentText("C'è stato un problema di autenticazione, l'applicazione verrà chiusa");
        info.showAndWait();

        Stage stage = (Stage) btnExit.getScene().getWindow();
        stage.close();
    }
}


 /*
    FOR TABLES

    // for tables
    // ObservableList<cardEntry> list = FXCollections.observableArrayList();

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
