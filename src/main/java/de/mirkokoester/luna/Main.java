package de.mirkokoester.luna;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("player/player.fxml"));
        primaryStage.setTitle("Luna Music Player");
        primaryStage.setScene(new Scene(root, 400, 475));
        primaryStage.setOnCloseRequest(event -> System.exit(0)); // TODO handle application shutdown
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
