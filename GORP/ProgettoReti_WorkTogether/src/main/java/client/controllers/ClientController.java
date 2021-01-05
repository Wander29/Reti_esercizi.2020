package client.controllers;

import client.model.ClientWT;
import javafx.stage.Stage;
import protocol.CSProtocol;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public abstract class ClientController {
    protected String username = null;

    protected static ClientWT clientLogic;
    protected static Stage stage;

    public void handleCloseRequest() throws IOException {
        clientLogic.exit();
        clientLogic.closeConnection();
    }

    public ClientController() {
        try {
            if(clientLogic == null)
            clientLogic = new ClientWT();
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

}
