package de.mirkokoester.luna.player.model

import java.io.File
import java.net.MalformedURLException
import java.util.concurrent.atomic.AtomicBoolean
import javafx.beans.{InvalidationListener, Observable}
import javafx.beans.property.SimpleIntegerProperty
import javafx.collections.{ObservableList, FXCollections}
import javafx.scene.media.{Media, MediaPlayer}
import javafx.util.Duration

class Player extends ObservablePlayer {
  private val skipTime: Duration = new Duration(5000)
  private val zeroTime: Duration = new Duration(0)
  private var mediaPlayerOpt: Option[MediaPlayer] = None
  private val isPlaying: AtomicBoolean = new AtomicBoolean(false)

  val currentlyPlaying: SimpleIntegerProperty = new SimpleIntegerProperty
  val items: ObservableList[SongTableRepresentation] = FXCollections.observableArrayList(
    new SongTableRepresentation(Song.fromPath("/home/mk/Music/Foo Fighters - In Your Honor   (CD2)/Foo Fighters - Cold Day In The Sun.mp3")),
    new SongTableRepresentation(Song.fromPath("/home/mk/Music/Dido - Life For Rent/Dido - White Flag.mp3")),
    new SongTableRepresentation(Song.fromPath("/home/mk/Music/Intergalactic Lovers - Little Heavy Burdens/Intergalactic Lovers - No Regrets.mp3")),
    new SongTableRepresentation(Song.fromPath("/home/mk/Music/Dido - Safe Trip Home/Dido - Grafton Street.mp3"))
  )

  def hasMediaPlayer(): Boolean = mediaPlayerOpt.nonEmpty

  def getMedia(): Media              = mediaPlayerOpt.map(_.getMedia).getOrElse(null) // TODO remove nulls
  def getCurrentTime(): Duration     = mediaPlayerOpt.map(_.getCurrentTime()).getOrElse(null)
  def seek(seekTime: Duration): Unit = mediaPlayerOpt.foreach(_.seek(seekTime))
  def setVolume(value: Double): Unit = mediaPlayerOpt.foreach(_.setVolume(value))
  def getVolume(): Double            = mediaPlayerOpt.map(_.getVolume()).getOrElse(Double.NaN)

  private def playFile(song: Song) {
    val file: File = new File(song.path)
    try {
      val media: Media = new Media(file.toURI.toURL.toString)
      val mediaPlayer = new MediaPlayer(media)
      mediaPlayerOpt = Some(mediaPlayer)

      mediaPlayer.currentTimeProperty.addListener(new InvalidationListener() {
        override def invalidated(ov: Observable) {
          observers.foreach(_.updateValues())
        }
      })

      mediaPlayer.setOnReady(new Runnable {
        override def run(): Unit = observers.foreach(_.onReady())
      })

      mediaPlayer.setOnPlaying(new Runnable {
        override def run(): Unit = {
          observers.foreach(_.onPlaying())
          isPlaying.set(true)
        }
      })

      mediaPlayer.setOnPaused(new Runnable {
        override def run(): Unit = {
          observers.foreach(_.onPaused())
          isPlaying.set(false)
        }
      })

      mediaPlayer.setOnStopped(new Runnable {
        override def run(): Unit = {
          observers.foreach(_.onStopped())
          isPlaying.set(false)
        }
      })

      observers.foreach(_.setPlayingTitle(song.getRepresentation))
      mediaPlayer.play()
    }
    catch {
      case e: MalformedURLException => {
        e.printStackTrace
      }
    }
  }

  def startPlayingFile(song: Song): Unit = mediaPlayerOpt.fold(playFile(song)) { mediaPlayer =>
    mediaPlayer.stop
    mediaPlayer.dispose
    playFile(song)
  }

  def playPause(): Unit = mediaPlayerOpt foreach { mediaPlayer =>
    if (isPlaying.get) { mediaPlayer.pause } else { mediaPlayer.play }
  }

  def stop(): Unit = mediaPlayerOpt.foreach(_.stop())

  def rewind(): Unit = mediaPlayerOpt.foreach { mediaPlayer =>
    val currentTime: Duration = mediaPlayer.getCurrentTime
    var seekTime: Duration = currentTime.subtract(skipTime)
    if (!seekTime.greaterThanOrEqualTo(zeroTime)) {
      seekTime = zeroTime
    }
    mediaPlayer.seek(seekTime)
  }

  def forward(): Unit = mediaPlayerOpt.foreach { mediaPlayer =>
    val currentTime: Duration = mediaPlayer.getCurrentTime
    val duration: Duration = mediaPlayer.getMedia.getDuration
    var seekTime: Duration = currentTime.add(skipTime)
    if (!seekTime.lessThan(duration)) {
      seekTime = duration
    }
    mediaPlayer.seek(seekTime)
  }

}
