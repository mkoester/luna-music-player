package de.mirkokoester.luna.player

import java.io.File
import javafx.scene.media.{Media, MediaPlayer}

object PlayerJavaFX {
  def play(file: File): MediaPlayer = {
    val media = new Media(file.toURI.toURL.toString)
    val mediaPlayer = new MediaPlayer(media)
    mediaPlayer.play()
    mediaPlayer
  }
}
