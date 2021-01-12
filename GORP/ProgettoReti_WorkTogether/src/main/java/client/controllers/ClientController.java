package client.controllers;

import client.model.ClientWT;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import protocol.CSProtocol;
import protocol.classes.Card;
import protocol.classes.CardStatus;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.sql.SQLException;

public abstract class ClientController {
    protected final static int MIN_LENGTH_PSW = 2;

    protected static String username = null;
    protected static ClientWT  clientLogic;

    /*
    modular exit: in future will be easy to implement
        another login window instead of closing app
     */
    public void handleCloseRequest() throws IOException {
        if(username != null) {
            clientLogic.logout();
        }
        username = null;

        clientLogic.exit();
        clientLogic.closeConnection();
    }

    public ClientController() {
        try {
            if(clientLogic == null)
            clientLogic = ClientWT.getInstance();
        }
        catch(RemoteException re) {
            re.printStackTrace();
            System.exit(-1);
        }
        catch (NotBoundException e) {
            System.out.println("Servizio" +
                    CSProtocol.RMI_SERVICE_NAME() + " non presente");
            System.exit(-1);
        }
        catch (SQLException t) {
            t.printStackTrace();
        }
    }

    protected void loadNewWindow(String loc, String title) {
        try {
            Parent parent = FXMLLoader.load(getClass().getResource(loc));
            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setTitle(title);

            stage.setScene(new Scene(parent));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void loadInThisWindow(Stage s, String loc, String title) {
        try {
            Parent parent = FXMLLoader.load(getClass().getResource(loc));
            s.setTitle(title);

            s.setScene(new Scene(parent));
            s.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    protected void closeStage(Node node) {
        Stage st = (Stage) node.getScene().getWindow();
        st.close();
    }

    protected void showAlertNetworkError() {
        Alert info = new Alert(Alert.AlertType.INFORMATION);
        info.setHeaderText("ERRORE DI RETE");
        info.showAndWait();
    }

    protected void showDialogProjectNotPresent() {
        Alert info = new Alert(Alert.AlertType.INFORMATION);
        info.setHeaderText("Progetto NON presente");
        info.setContentText("L'operazione richiesta non è stata completata");
        info.showAndWait();
    }

    protected void showDialogUsernameNotPresent() {
        Alert info = new Alert(Alert.AlertType.INFORMATION);
        info.setHeaderText("Nome utente NON presente");
        info.setContentText("L'operazione richiesta non è stata completata");
        info.showAndWait();
    }

    protected void showDialogIllegalOperation() {
        Alert info = new Alert(Alert.AlertType.INFORMATION);
        info.setHeaderText("Operazione NON consentita dal sistema");
        info.setContentText("L'operazione richiesta non è stata completata");
        info.showAndWait();
    }

    protected String cardStatusToString(CardStatus cs) {
        return cs.toString().replace("_", " ");
    }
}
