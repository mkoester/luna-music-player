package de.mirkokoester.luna.player;

import de.mirkokoester.luna.model.Song;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class PlaylistController implements Initializable {
    @FXML private ListView playListView;
    private Stage playlistStage;
    private ObservableList<Song> items = FXCollections.observableArrayList (
            Song.fromPath("/home/mk/Music/Foo Fighters - In Your Honor   (CD2)/Foo Fighters - Cold Day In The Sun.mp3"),
            Song.fromPath("/home/mk/Music/Dido - Life For Rent/Dido - White Flag.mp3"),
            Song.fromPath("/home/mk/Music/Intergalactic Lovers - Little Heavy Burdens/Intergalactic Lovers - No Regrets.mp3"),
            Song.fromPath("/home/mk/Music/Dido - Safe Trip Home/Dido - Grafton Street.mp3")
    );
    private PlayerController playerController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        playListView.setItems(items);
        playListView.setOnMouseClicked(click -> {
            if (click.getClickCount() == 2) {
                Song currentItemSelected = (Song) playListView.getSelectionModel().getSelectedItem();
                playerController.startPlayingFile(currentItemSelected.path());
            }
        });
    }

    protected boolean registerPlayerControllerAndStage(PlayerController playerController, Stage playlistStage) {
        if (null == this.playerController && null == this.playlistStage) {
            this.playerController = playerController;
            this.playlistStage = playlistStage;
            return true;
        } else {
            return false;
        }
    }

    public void play(ActionEvent actionEvent) {
        if (null != playerController) {
            playerController.playPause(null);
        } else {
            System.out.println("no playerController");
        }
    }

    public void close(ActionEvent actionEvent) {
        playlistStage.hide();
    }

    public void addFile(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        File file = fileChooser.showOpenDialog(null);
        if (null != file) {
            items.add(Song.fromPath(file.toString()));
        }
    }
}
