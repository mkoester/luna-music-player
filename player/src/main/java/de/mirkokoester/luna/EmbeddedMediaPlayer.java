package de.mirkokoester.luna;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;

import java.io.File;

public class EmbeddedMediaPlayer extends Application {

    private static final String MEDIA_URL =
            "/home/mk/Music/Foo Fighters - In Your Honor   (CD2)/Foo Fighters - Cold Day In The Sun.mp3";
            //"http://download.oracle.com/otndocs/products/javafx/oow2010-2.flv";

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Embedded Media Player");
        Group root = new Group();
        Scene scene = new Scene(root, 540, 241);

        // create media player
        File file = new File(MEDIA_URL);
        Media media = new Media(file.toURI().toURL().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setAutoPlay(true);
        MediaControl mediaControl = new MediaControl(mediaPlayer);
        scene.setRoot(mediaControl);

        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
