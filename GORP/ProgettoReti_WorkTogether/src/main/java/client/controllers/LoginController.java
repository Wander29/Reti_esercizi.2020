package client.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public class LoginController {

    @FXML
    private JFXTextField usernameTextField;
    @FXML
    private JFXPasswordField passwordTextField;
    @FXML
    private JFXButton loginButton;
    @FXML
    private JFXButton registerButton;

    @FXML
    void activateButtonsIfBothNotEmpty(KeyEvent event) {
        if(usernameTextField.getText().isEmpty() || passwordTextField.getText().isEmpty()) {
            loginButton.setDisable(Boolean.TRUE);
            registerButton.setDisable(Boolean.TRUE);
        }
        else {
            loginButton.setDisable(Boolean.FALSE);
            registerButton.setDisable(Boolean.FALSE);
        }
    }

    @FXML
    void login(MouseEvent event) {
        if(true) {
            Alert info = new Alert(Alert.AlertType.INFORMATION,
                    "Bentornato " + this.usernameTextField.getText() + "!");

            info.setHeaderText("LOGIN");
            info.showAndWait();
        }
    }

    @FXML
    void register(MouseEvent event) {
        // allow user to decide between yes and no
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Sei sicuro di volerti registrare?", ButtonType.YES, ButtonType.NO);
        alert.setHeaderText("REGISTRAZIONE");

        // clicking X(close) also means no
        ButtonType result = alert.showAndWait().orElse(ButtonType.NO);

        if(ButtonType.YES.equals(result)) {
           System.out.println("wait for registration");
        }
    }

}