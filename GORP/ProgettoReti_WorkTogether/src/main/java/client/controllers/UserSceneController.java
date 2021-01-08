package client.controllers;

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
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import server.data.UserInfo;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;

public class UserSceneController extends ClientController {

    protected static Stage stage;

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

    public UserSceneController() {
        super();
    }

    public void initialize() {
        tabPaneShowProject.getSelectionModel().selectedItemProperty().addListener((ov, oldTab, newTab) -> {
            System.err.println("changed: " + newTab.getText());

            switch(newTab.getText()) {
                case "PROGETTO":
                    // @todo listProjects() here
                    // comboChooseProject.getItems().add("gattini");
                    // JFXDepthManager.setDepth(tableViewTODOlist, 1);

                    break;

                case "CHAT":
                    break;

                case "MEMBRI":
                    break;

                default:
                    System.err.println("changed: " + newTab.getText());
                    break;
            }

        });
    }

    @FXML
    void handleClickBtnAddProject(MouseEvent event) {
        TextInputDialog dialog = new TextInputDialog("");
        dialog.setHeaderText("Inserisci il nome del progetto");
        dialog.setTitle("Add project?");

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

    void updateComboBoxListProjects() {
        // ask for listProjects
        List<String> projects = null;
        try {
            projects = clientLogic.listProjects();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // once you have the list, populate combo box
        comboChooseProject.getItems().clear();
        comboChooseProject.getItems().addAll(projects);

    }

    @FXML
    void handleExit(ActionEvent event) {
        /*
        try {
            clientLogic.logout();
        } catch (IOException e) {
            e.printStackTrace();
        }

         */
        stage = (Stage) btnExit.getScene().getWindow();
        stage.close();
    }

    @FXML
    void handleLabelOne(MouseEvent event) {

    }
    // for tables
    // ObservableList<cardEntry> list = FXCollections.observableArrayList();

    @FXML
    void handleBtnAddCard(ActionEvent event) {

    }


    /*
    FOR TABLES

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


    @FXML
    void handleComboChooseProject(ActionEvent event) {

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
}