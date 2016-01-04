package de.mirkokoester.luna.player

import javafx.beans.{Observable, InvalidationListener}

import de.mirkokoester.luna.medialibrary.MedialibraryController
import de.mirkokoester.luna.player.model.Player
import de.mirkokoester.luna.player.model.PlayerObserver
import de.mirkokoester.luna.player.model.SongTableRepresentation
import javafx.application.Platform
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.fxml.Initializable
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.Slider
import javafx.stage.Stage
import javafx.util.Duration
import java.io.IOException
import java.net.URL
import java.util.ResourceBundle

import scala.math
import scala.util.control.NonFatal
import scala.util.{Failure, Success}

object PlayerController {
  private def toTwoDigits(number: Int): String = if (number > 9) number.toString else s"0$number"
  private def formatTime(elapsed: Duration, duration: Duration): String = {
    var intElapsed: Int = Math.floor(elapsed.toSeconds).toInt
    val elapsedHours: Int = intElapsed / (60 * 60)
    if (elapsedHours > 0) {
      intElapsed -= elapsedHours * 60 * 60
    }
    val elapsedMinutes: Int = intElapsed / 60
    val elapsedSeconds: Int = intElapsed - elapsedHours * 60 * 60 - elapsedMinutes * 60
    val elapsedMinutesStr  = toTwoDigits(elapsedMinutes)
    val elapsedSecondsStr  = toTwoDigits(elapsedSeconds)
    if (duration.greaterThan(Duration.ZERO)) {
      var intDuration: Int = Math.floor(duration.toSeconds).toInt
      val durationHours: Int = intDuration / (60 * 60)
      if (durationHours > 0) {
        intDuration -= durationHours * 60 * 60
      }
      val durationMinutes: Int = intDuration / 60
      val durationSeconds: Int = intDuration - durationHours * 60 * 60 - durationMinutes * 60
      val durationMinutesStr = toTwoDigits(durationMinutes)
      val durationSecondsStr = toTwoDigits(durationSeconds)
      if (durationHours > 0) {
        s"$elapsedHours:$elapsedMinutesStr:$elapsedSecondsStr / $durationHours:$durationMinutesStr:$durationSecondsStr"
      }
      else {
        s"$elapsedMinutes:$elapsedSecondsStr / $durationMinutes:$durationSecondsStr"
      }
    }
    else {
      if (elapsedHours > 0) {
        s"$elapsedHours:$elapsedMinutesStr:$elapsedSecondsStr"
      }
      else {
        s"$elapsedMinutes:$elapsedSecondsStr"
      }
    }
  }
}

class PlayerController extends Initializable with PlayerObserver {
  @FXML protected var playingTitle:    Label = null
  @FXML protected var volumeSlider:    Slider = null
  @FXML protected var volumeLabel:     Label = null
  @FXML protected var timeSlider:      Slider = null
  @FXML protected var playTimeLabel:   Label = null
  @FXML protected var playPauseButton: Button = null
  private val playerModel:            Player = new Player
  private var playlistStage:          Stage = null
  private var playlistController:     PlaylistController = null
  private var medialibraryStage:      Stage = null
  private var medialibraryController: MedialibraryController = null

  def initialize(location: URL, resources: ResourceBundle) {
    playerModel.register(this)
    val fxmlLoader: FXMLLoader = new FXMLLoader
    try {
      val pl: Parent = fxmlLoader.load(getClass.getResource("playlist.fxml").openStream)
      playlistStage = new Stage
      playlistStage.setTitle("Playlist")
      playlistStage.setScene(new Scene(pl, 450, 450))
      playlistController = fxmlLoader.getController[PlaylistController]()
      if (null != playlistController) {
        if (!playlistController.registerPlayerControllerAndStageAndPlayerModel(this, playlistStage, playerModel)) {
          System.out.println("registration of this PlayerController in playlistController did not succeed")
        }
      } else {
        System.out.println("no playlistController")
      }
    }
    catch {
      case e: IOException => {
        e.printStackTrace
      }
    }

    MedialibraryController.getStageAndController() match {
      case Success((medialibraryStage, medialibraryController)) =>
        this.medialibraryStage = medialibraryStage
        this.medialibraryController = medialibraryController
        if (!medialibraryController.registerPlaylist(playerModel)) {
          System.out.println("registration of the playlist in medialibraryController did not succeed")
        }
      case Failure(NonFatal(e)) => e.printStackTrace
    }

    if (null != timeSlider) {
      timeSlider.valueProperty.addListener(new InvalidationListener() {
        override def invalidated(observable: Observable): Unit = {
          if (timeSlider.isValueChanging()) {
            // multiply duration by percentage calculated by slider position
            playerModel.getMedia().map(_.getDuration).foreach { duration =>
              playerModel.seek(duration.multiply(timeSlider.getValue() / 100.0))
              updateValues()
            }
          }
        }
      })
    }
    if (null != volumeSlider) {
      volumeSlider.valueProperty.addListener(new InvalidationListener() {
        override def invalidated(observable: Observable): Unit = {
          if (volumeSlider.isValueChanging()) {
            playerModel.setVolume(volumeSlider.getValue() / 100.0)
            updateValues()
          }
        }
      })
    }
  }

  def playPause(actionEvent: ActionEvent) {
    playerModel.playPause()
  }

  def stop(actionEvent: ActionEvent) {
    playerModel.stop()
  }

  private def playFromPlaylist(index: Int): Unit = {
    playerModel.startPlayingFileFromPlaylist(index)
  }

  def previous(actionEvent: ActionEvent): Unit = {
    playerModel.playPreviousFromPlaylist()
  }

  def next(actionEvent: ActionEvent): Unit = {
    playerModel.playNextFromPlaylist()
  }

  def rewind(actionEvent: ActionEvent): Unit = {
    playerModel.rewind()
  }

  def forward(actionEvent: ActionEvent): Unit = {
    playerModel.forward()
  }

  def showPlaylist(actionEvent: ActionEvent): Unit = {
    if (null != playlistStage) {
      playlistStage.show()
    }
  }

  def showMediaLibrary(actionEvent: ActionEvent): Unit = {
    if (null != medialibraryStage) {
      medialibraryStage.show()
    }
  }

  def updateValues(): Unit = {
    if (playTimeLabel != null && timeSlider != null && volumeSlider != null && playerModel.hasMediaPlayer) {
      Platform.runLater(new Runnable() {
        override def run(): Unit = {
          for {
            currentTime <- playerModel.getCurrentTime()
            media       <- playerModel.getMedia()
            duration = media.getDuration()
          } {
            playTimeLabel.setText(PlayerController.formatTime(currentTime, duration))
            timeSlider.setDisable(duration.isUnknown())
            if (!timeSlider.isDisabled()
              && duration.greaterThan(Duration.ZERO)
              && !timeSlider.isValueChanging()) {
              timeSlider.setValue(currentTime.divide(duration.toMillis()).toMillis()
                * 100.0)
            }
            if (!volumeSlider.isValueChanging()) {
              volumeSlider.setValue(playerModel.getVolume() * 100)
            }
            volumeLabel.setText(math.round(playerModel.getVolume() * 100) + " %")
          }
        }
      })
    }
  }

  def setPlayingTitle(title: String) {
    if (null != playingTitle) {
      playingTitle.setText(title)
    }
  }

  def onReady(): Unit = {
    updateValues
  }

  def onPlaying(): Unit = {
    playPauseButton.setText("\u2016")
  }

  def onPaused(): Unit = {
    playPauseButton.setText("\u25B6")
  }

  def onStopped(): Unit = {
    playPauseButton.setText("\u25B6")
  }
}
