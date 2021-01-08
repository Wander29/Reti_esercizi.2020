package server;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;


public class testSwitch extends Application  {
    public static void main(String args[]){
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        VBox layout = new VBox();
        VBox layout2 = new VBox();
        layout.setAlignment(Pos.CENTER);
        layout2.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 300, 300);
        Scene scene2 = new Scene(layout2, 300, 300);

        Label label1 = new Label("This is the First Scene");
        Label label2 = new Label("This is the Second Scene");

        Button button = new Button("Forward");
        button.setOnAction(e -> primaryStage.setScene(scene2));

        Button button2 = new Button("Backwards");
        button2.setOnAction(e -> primaryStage.setScene(scene));

        TextField text = new TextField();
        text.setMaxWidth(100);

        layout.getChildren().addAll(label1, button);
        layout2.getChildren().addAll(label2, button2, text);

        primaryStage.setTitle("CodersLegacy");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}