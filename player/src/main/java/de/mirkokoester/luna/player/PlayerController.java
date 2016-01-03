package de.mirkokoester.luna.player;

import de.mirkokoester.luna.player.model.Player;
import de.mirkokoester.luna.player.model.PlayerObserver;
import de.mirkokoester.luna.player.model.Song;
import de.mirkokoester.luna.player.model.SongTableRepresentation;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.media.Media;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class PlayerController implements Initializable, PlayerObserver {
    @FXML private Label playingTitle;
    @FXML private Slider volumeSlider;
    @FXML private Label volumeLabel;
    @FXML private Slider timeSlider;
    @FXML private Label playTimeLabel;
    @FXML private Button playPauseButton;

    private Player playerModel = new Player();
    private Stage playlistStage;
    private PlaylistController playlistController;

    private static final String MEDIA_URL =
            "/home/mk/Music/Foo Fighters - In Your Honor   (CD2)/Foo Fighters - Cold Day In The Sun.mp3";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        playerModel.register(this);
        FXMLLoader fxmlLoader = new FXMLLoader();
        try {
            Parent pl = fxmlLoader.load(getClass().getResource("playlist.fxml").openStream());
            playlistStage = new Stage();
            playlistStage.setTitle("Playlist");
            playlistStage.setScene(new Scene(pl, 450, 450));

            playlistController = fxmlLoader.getController();
            if (null != playlistController) {
                if (!playlistController.registerPlayerControllerAndStageAndPlayerModel(this, playlistStage, playerModel)) {
                    System.out.println("registration of this PlayerController in playlistController did not succeed");
                }
            } else {
                System.out.println("no playlistController");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (null != timeSlider) {
            timeSlider.valueProperty().addListener(ov -> {
                if (timeSlider.isValueChanging()) {
                    // multiply duration by percentage calculated by slider position
                    Media media = playerModel.getMedia();
                    if (null != media) {
                        Duration duration = media.getDuration();
                        playerModel.seek(duration.multiply(timeSlider.getValue() / 100.0));
                        updateValues();
                    }
                }
            });
        }

        if (null != volumeSlider) {
            volumeSlider.valueProperty().addListener(ov -> {
                if (volumeSlider.isValueChanging()) {
                    playerModel.setVolume(volumeSlider.getValue() / 100.0);
                    updateValues();
                }
            });
        }
    }



    protected void startPlayingFile(Song song) {
        // TODO remove
        playerModel.startPlayingFile(song);
    }

    public void playPause(ActionEvent actionEvent) {
        playerModel.playPause();
    }

    public void stop(ActionEvent actionEvent) {
        playerModel.stop();
    }

    private void playFromPlaylist(int index) {
        SongTableRepresentation nextSong = playerModel.items().get(index);
        if (null != nextSong) {
            playerModel.currentlyPlaying().set(index);
            startPlayingFile(nextSong.song());
        }
    }

    public void previous(ActionEvent actionEvent) {
        int prevIndex = playerModel.currentlyPlaying().get() - 1;
        if (prevIndex >= 0) { playFromPlaylist(prevIndex); }
    }

    public void next(ActionEvent actionEvent) {
        int nextIndex = playerModel.currentlyPlaying().get() + 1;
        if (playerModel.items().size() > nextIndex)  { playFromPlaylist(nextIndex); }
    }

    public void rewind(ActionEvent actionEvent) {
        playerModel.rewind();
    }

    public void forward(ActionEvent actionEvent) {
        playerModel.forward();
    }

    public void showPlaylist(ActionEvent actionEvent) {
        if (null != playlistStage) {
            playlistStage.show();
        }
    }

    public void showMediaLibrary(ActionEvent actionEvent) {

    }

    @Override
    public void updateValues() {
        if (playTimeLabel != null && timeSlider != null && volumeSlider != null && playerModel.hasMediaPlayer()) {
            Platform.runLater(() -> {
                Duration currentTime = playerModel.getCurrentTime(); // FIXME
                Duration duration = playerModel.getMedia().getDuration();
                playTimeLabel.setText(formatTime(currentTime, duration));
                timeSlider.setDisable(duration.isUnknown());
                if (!timeSlider.isDisabled()
                        && duration.greaterThan(Duration.ZERO)
                        && !timeSlider.isValueChanging()) {
                    timeSlider.setValue(currentTime.divide(duration.toMillis()).toMillis()
                            * 100.0);
                }
                if (!volumeSlider.isValueChanging()) {
                    volumeSlider.setValue((int)Math.round(playerModel.getVolume()
                            * 100));
                }
                volumeLabel.setText( (int)Math.round(playerModel.getVolume() * 100) + " %" );
            });
        }
    }

    private static String formatTime(Duration elapsed, Duration duration) {
        int intElapsed = (int)Math.floor(elapsed.toSeconds());
        int elapsedHours = intElapsed / (60 * 60);
        if (elapsedHours > 0) {
            intElapsed -= elapsedHours * 60 * 60;
        }
        int elapsedMinutes = intElapsed / 60;
        int elapsedSeconds = intElapsed - elapsedHours * 60 * 60
                - elapsedMinutes * 60;

        if (duration.greaterThan(Duration.ZERO)) {
            int intDuration = (int)Math.floor(duration.toSeconds());
            int durationHours = intDuration / (60 * 60);
            if (durationHours > 0) {
                intDuration -= durationHours * 60 * 60;
            }
            int durationMinutes = intDuration / 60;
            int durationSeconds = intDuration - durationHours * 60 * 60 -
                    durationMinutes * 60;
            if (durationHours > 0) {
                return String.format("%d:%02d:%02d/%d:%02d:%02d",
                        elapsedHours, elapsedMinutes, elapsedSeconds,
                        durationHours, durationMinutes, durationSeconds);
            } else {
                return String.format("%02d:%02d/%02d:%02d",
                        elapsedMinutes, elapsedSeconds,durationMinutes,
                        durationSeconds);
            }
        } else {
            if (elapsedHours > 0) {
                return String.format("%d:%02d:%02d", elapsedHours,
                        elapsedMinutes, elapsedSeconds);
            } else {
                return String.format("%02d:%02d",elapsedMinutes,
                        elapsedSeconds);
            }
        }
    }

    @Override
    public void setPlayingTitle(String title) {
        if (null != playingTitle) {
            playingTitle.setText(title);
        }
    }

    @Override
    public void onReady() {
        updateValues();
    }

    @Override
    public void onPlaying() {
        playPauseButton.setText("\u2016");
    }

    @Override
    public void onPaused() {
        playPauseButton.setText("\u25B6");
    }

    @Override
    public void onStopped() {
        playPauseButton.setText("\u25B6");
    }
}
