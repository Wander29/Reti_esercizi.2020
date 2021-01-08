package client;

import client.controllers.ClientController;
import client.controllers.LoginController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * JavaFX WorthClient
 */
public class WorthClient extends Application {

    private Scene scene;
    private FXMLLoader loader;

    @Override
    public void start(Stage stage) throws IOException {
        this.loader = this.getFXMLLoader("login");

        scene = new Scene(this.loadFXML());
        scene.getStylesheets().add(getClass().getResource("../css/main_style.css"   ).toExternalForm());
        /*
        scene.getStylesheets().add(getClass().getResource("../css/cardListTable.css"   ).toExternalForm());
        scene.getStylesheets().add(getClass().getResource("../css/usersTable.css"   ).toExternalForm());
        scene.getStylesheets().add(getClass().getResource("../css/chatTable.css"   ).toExternalForm());
        */

        // logic
        ClientController controller = this.loader.getController();
            // controller.setStage(stage);
        /*
        stage.setOnShown(event -> {
            ClientController.
        });

         */

        stage.setOnHiding(event -> {
            try {
                controller.handleCloseRequest();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Platform.exit();
        });
        // view
        stage.setScene(scene);
        stage.setMaximized(false);
        stage.setTitle("WORTH - Work Together");

        stage.getIcons().add(new Image(
                WorthClient.class.getResourceAsStream("../images/notebook.png")));

        stage.show();

        // link logic for login
    }

    void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML());
    }

    private FXMLLoader getFXMLLoader(String fxml) {
        return new FXMLLoader(WorthClient.class.getResource(fxml + ".fxml"));
    }

    private Parent loadFXML() throws IOException {
        // FXMLLoader fxmlLoader = new FXMLLoader(WorthClient.class.getResource(fxml + ".fxml"));
        return this.loader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}