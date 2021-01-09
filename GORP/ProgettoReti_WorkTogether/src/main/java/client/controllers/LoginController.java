package client.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import protocol.CSReturnValues;

import java.io.IOException;
import java.rmi.RemoteException;

public class LoginController extends ClientController {
/*
    NODES
 */
    @FXML
    private JFXTextField usernameTextField;
    @FXML
    private JFXPasswordField passwordTextField;
    @FXML
    private JFXButton loginButton;
    @FXML
    private JFXButton registerButton;

/*
    controller FUNCTIONS
 */
    public LoginController() {
        super();
    }

    public void initialize() {
        try {
            System.out.println("[CONTROLLER - Login] initialize");
            this.clientLogic.startConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handleCloseRequest() {
        try {
            System.out.println("[CONTROLLER - Login] sto gestendo la chiusura");
            // this.clientLogic.logout(this.username);

            super.handleCloseRequest();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

/*
    HANDLERS
 */
    @FXML
    void activateButtonsIfBothNotEmpty(KeyEvent event) {
        if(usernameTextField.getText().isEmpty() || passwordTextField.getText().isEmpty() ||
            passwordTextField.getText().length() < MIN_LENGTH_PSW) {
            loginButton.setDisable(Boolean.TRUE);
            registerButton.setDisable(Boolean.TRUE);
        }
        else {
            loginButton.setDisable(Boolean.FALSE);
            registerButton.setDisable(Boolean.FALSE);
        }
    }

    @FXML
    void loginOnMouseClicked(MouseEvent event) {
        String user = usernameTextField.getText();
        String password = passwordTextField.getText();

        try {
            // ask server to login
            CSReturnValues retVal = clientLogic.login(user, password);

            // compute server response
            String  headerString    = null,
                    contentString   = null;
            final String loginError = "Il Login NON è andato a buon fine";

            switch(retVal) {

                case LOGIN_OK:
                        this.username = user;
                        loadInThisWindow((Stage) loginButton.getScene().getWindow(),
                                "../userScene.fxml", "WORTH");
                    return;

                case USERNAME_NOT_PRESENT:
                    headerString    = loginError;
                    contentString   = username + " NON è un nome utente valido";
                    break;

                case PSW_INCORRECT:
                    headerString    = loginError;
                    contentString   = "La password per l'utente " + user + " NON è valida";
                    break;

                case ALREADY_LOGGED_IN:
                    headerString    = loginError;
                    contentString   = "L'utente " + user + " è già online " +
                            "Non puoi accedere contemporaneamente più volte a WORTH";
                    break;
            }

            // show dialog about server response
            Alert info = new Alert(Alert.AlertType.INFORMATION);
            info.setHeaderText(headerString);
            info.setContentText(contentString);
            info.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

        @FXML
    void registerOnMouseClicked(MouseEvent event) {
        // allow user to decide between yes and no
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Sei sicuro di volerti registrare?", ButtonType.YES, ButtonType.NO);
        alert.setHeaderText("REGISTRAZIONE");

        // clicking X(close) also means no
        ButtonType result = alert.showAndWait().orElse(ButtonType.NO);

        if(ButtonType.YES.equals(result)) {
            String username = usernameTextField.getText();
            String password = passwordTextField.getText();

            try {
                // ask server to register the user
                CSReturnValues retVal = this.clientLogic.register(username, password);

                // compute server response
                String  headerString    = null,
                        contentString   = null;

                switch(retVal) {

                    case REGISTRATION_OK:
                        headerString    = "La registrazione è andata a buon fine!";
                        contentString   = "Accedi a WORTH: reinserisci i dati e clicca su Login";
                        usernameTextField.setText("");
                        passwordTextField.setText("");
                        break;

                    case USERNAME_ALREADY_PRESENT:
                        headerString    = "La registrazione NON è andata a buon fine";
                        contentString   = " \"" + username + "\" è già in uso da un altro utente";
                        break;

                    default: ;
                }

                // show dialog about server response
                Alert info = new Alert(Alert.AlertType.INFORMATION);
                info.setHeaderText(headerString);
                info.setContentText(contentString);
                info.showAndWait();

            } catch (RemoteException e) {
                showAlertNetworkError();
            }
        }
    }
}