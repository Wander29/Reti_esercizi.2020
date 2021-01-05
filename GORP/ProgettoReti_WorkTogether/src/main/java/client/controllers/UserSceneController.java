package client.controllers;

import java.io.IOException;

public class UserSceneController extends ClientController  {

    public UserSceneController() { ; }

    @Override
    public void handleCloseRequest() {
        try {
            System.out.println("[CONTROLLER - UserScene] sto gestendo la chiusura");
            // this.clientLogic.logout(this.username);
            ClientController.clientLogic.closeConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
