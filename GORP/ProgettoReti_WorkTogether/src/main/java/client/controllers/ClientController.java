package client.controllers;

import client.model.ClientWT;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import protocol.CSProtocol;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public abstract class ClientController {
    protected static String username = null;

    protected static ClientWT clientLogic;

    public void handleCloseRequest() throws IOException {
        if(username != null) {
            clientLogic.logout();
        }
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
}
