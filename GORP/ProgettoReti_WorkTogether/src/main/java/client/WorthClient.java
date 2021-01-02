package client;

import javafx.application.Application;
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

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("login"));
        scene.getStylesheets().add(getClass().getResource("../../css/main_style.css").toExternalForm());

        stage.setScene(scene);
        stage.setMaximized(true);
        stage.setTitle("WORTH - Work Together");

        stage.getIcons().add(new Image(
                WorthClient.class.getResourceAsStream("../../icons/notebook.png")));

        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(WorthClient.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}