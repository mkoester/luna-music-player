package de.mirkokoester.luna.player;

import de.mirkokoester.luna.model.Song;
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
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;

public class PlayerController implements Initializable {
    @FXML private Label playingTitle;
    @FXML private Slider volumeSlider;
    @FXML private Label volumeLabel;
    @FXML private Slider timeSlider;
    @FXML private Label playTimeLabel;
    @FXML private Button playPauseButton;

    private Stage playlistStage;
    private PlaylistController playlistController;
    private MediaPlayer mediaPlayer;
    private AtomicBoolean isPlaying = new AtomicBoolean(false); // Do I need this?

    private Duration skipTime = new Duration(5000);
    private Duration zeroTime = new Duration(0);

    private static final String MEDIA_URL =
            "/home/mk/Music/Foo Fighters - In Your Honor   (CD2)/Foo Fighters - Cold Day In The Sun.mp3";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        FXMLLoader fxmlLoader = new FXMLLoader();
        try {
            Parent pl = fxmlLoader.load(getClass().getResource("playlist.fxml").openStream());
            playlistStage = new Stage();
            playlistStage.setTitle("Playlist");
            playlistStage.setScene(new Scene(pl, 450, 450));

            playlistController = fxmlLoader.getController();
            if (null != playlistController) {
                if (!playlistController.registerPlayerControllerAndStage(this, playlistStage)) {
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
                    Duration duration = mediaPlayer.getMedia().getDuration();
                    mediaPlayer.seek(duration.multiply(timeSlider.getValue() / 100.0));
                    updateValues();
                }
            });
        }

        if (null != volumeSlider) {
            volumeSlider.valueProperty().addListener(ov -> {
                if (volumeSlider.isValueChanging()) {
                    mediaPlayer.setVolume(volumeSlider.getValue() / 100.0);
                    updateValues();
                }
            });
        }
    }

    public void previous(ActionEvent actionEvent) {

    }

    private void playFile(Song song) {
        File file = new File(song.path());
        try {
            Media media = new Media(file.toURI().toURL().toString());
            mediaPlayer = new MediaPlayer(media);

            mediaPlayer.currentTimeProperty().addListener(ov -> {
                updateValues();
            });

            mediaPlayer.setOnReady( () ->
                    updateValues()
            );
            mediaPlayer.setOnPlaying( () -> {
                playPauseButton.setText("\u2016");
                isPlaying.set(true);
            });
            mediaPlayer.setOnPaused( () -> {
                playPauseButton.setText("\u25B6");
                isPlaying.set(false);
            });
            mediaPlayer.setOnStopped( () -> {
                playPauseButton.setText("\u25B6");
                isPlaying.set(false);
            });

            if (null != playingTitle) {
                playingTitle.setText(song.getRepresentation());
            }
            mediaPlayer.play();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    protected void startPlayingFile(Song song) {
        if (null == mediaPlayer) {
            playFile(song);
        } else {
            mediaPlayer.stop();
            mediaPlayer.dispose();
            playFile(song);
        }
    }

    public void playPause(ActionEvent actionEvent) {
        if (null == mediaPlayer) {
            startPlayingFile(Song.fromPath(MEDIA_URL));
        } else {
            if (isPlaying.get()) {
                mediaPlayer.pause();
            } else {
                mediaPlayer.play();
            }
        }
    }

    public void stop(ActionEvent actionEvent) {
        if (null != mediaPlayer) {
            mediaPlayer.stop();
        }
    }

    public void next(ActionEvent actionEvent) {
        
    }

    public void rewind(ActionEvent actionEvent) {
        if (null != mediaPlayer) {
            Duration currentTime = mediaPlayer.getCurrentTime();
            Duration seekTime = currentTime.subtract(skipTime);
            if (!seekTime.greaterThanOrEqualTo(zeroTime)) {
                seekTime = zeroTime;
            }
            mediaPlayer.seek(seekTime);
        }
    }

    public void forward(ActionEvent actionEvent) {
        if (null != mediaPlayer) {
            Duration currentTime = mediaPlayer.getCurrentTime();
            Duration duration = mediaPlayer.getMedia().getDuration();
            Duration seekTime = currentTime.add(skipTime);
            if (!seekTime.lessThan(duration)) {
                seekTime = duration;
            }
            mediaPlayer.seek(seekTime);
        }
    }

    public void showPlaylist(ActionEvent actionEvent) {
        if (null != playlistStage) {
            playlistStage.show();
        }
    }

    public void showMediaLibrary(ActionEvent actionEvent) {

    }





    protected void updateValues() {
        if (playTimeLabel != null && timeSlider != null && volumeSlider != null) {
            Platform.runLater(() -> {
                Duration currentTime = mediaPlayer.getCurrentTime();
                Duration duration = mediaPlayer.getMedia().getDuration();
                playTimeLabel.setText(formatTime(currentTime, duration));
                timeSlider.setDisable(duration.isUnknown());
                if (!timeSlider.isDisabled()
                        && duration.greaterThan(Duration.ZERO)
                        && !timeSlider.isValueChanging()) {
                    timeSlider.setValue(currentTime.divide(duration.toMillis()).toMillis()
                            * 100.0);
                }
                if (!volumeSlider.isValueChanging()) {
                    volumeSlider.setValue((int)Math.round(mediaPlayer.getVolume()
                            * 100));
                }
                volumeLabel.setText( (int)Math.round(mediaPlayer.getVolume() * 100) + " %" );
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
}
