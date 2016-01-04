package de.mirkokoester.luna.player.model

import java.io.File
import java.net.MalformedURLException
import java.util.concurrent.atomic.AtomicBoolean
import javafx.beans.{InvalidationListener, Observable}
import javafx.beans.property.SimpleIntegerProperty
import javafx.collections.{ObservableList, FXCollections}
import javafx.scene.media.{Media, MediaPlayer}
import javafx.util.Duration

import de.mirkokoester.luna.model.{Playlist, Song}

class Player extends ObservablePlayer with Playlist {
  private final val skipTime:  Duration = new Duration(5000)
  private final val zeroTime:  Duration = new Duration(0)
  private final val isPlaying: AtomicBoolean = new AtomicBoolean(false)
  private var mediaPlayerOpt:  Option[MediaPlayer] = None

  private final val currentlyPlaying: SimpleIntegerProperty = new SimpleIntegerProperty(-1)
  val items: ObservableList[SongTableRepresentation] = FXCollections.observableArrayList( // TODO do not expose
    new SongTableRepresentation(Song.fromPath("/home/mk/Music/Foo Fighters - In Your Honor   (CD2)/Foo Fighters - Cold Day In The Sun.mp3")),
    new SongTableRepresentation(Song.fromPath("/home/mk/Music/Dido - Life For Rent/Dido - White Flag.mp3")),
    new SongTableRepresentation(Song.fromPath("/home/mk/Music/Intergalactic Lovers - Little Heavy Burdens/Intergalactic Lovers - No Regrets.mp3")),
    new SongTableRepresentation(Song.fromPath("/home/mk/Music/Dido - Safe Trip Home/Dido - Grafton Street.mp3"))
  )

  def hasMediaPlayer(): Boolean = mediaPlayerOpt.nonEmpty

  def getCurrentlyPlaying = currentlyPlaying.get()
  def getMedia(): Option[Media]          = mediaPlayerOpt.map(_.getMedia)
  def getCurrentTime(): Option[Duration] = mediaPlayerOpt.map(_.getCurrentTime())
  def seek(seekTime: Duration): Unit     = mediaPlayerOpt.foreach(_.seek(seekTime))
  def setVolume(value: Double): Unit     = mediaPlayerOpt.foreach(_.setVolume(value)) // TODO make volume independent of media
  def getVolume(): Double                = mediaPlayerOpt.map(_.getVolume()).getOrElse(Double.NaN)

  private def playFile(song: Song): Unit = {
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

      mediaPlayer.setOnEndOfMedia(new Runnable {
        override def run(): Unit = {
          mediaPlayer.stop()
          mediaPlayer.seek(zeroTime)
          isPlaying.set(false)
          playNextFromPlaylist()
        }
      })

      observers.foreach(_.setPlayingTitle(song.getRepresentation))
      mediaPlayer.play()
    }
    catch {
      case e: MalformedURLException => {
        e.printStackTrace()
      }
    }
  }

  private def startPlayingFile(song: Song): Unit = mediaPlayerOpt.fold(playFile(song)) { mediaPlayer =>
    mediaPlayer.stop()
    mediaPlayer.dispose()
    playFile(song)
  }

  def startPlayingFileFromPlaylist(index: Int): Unit = {
    if (index >= 0 && index < items.size()) {
      currentlyPlaying.set(index)
      val song = items.get(index).song
      startPlayingFile(song)
    }
  }

  def playPreviousFromPlaylist(): Unit = {
    val prevIndex: Int = currentlyPlaying.get - 1
    if (prevIndex >= 0) {
      startPlayingFileFromPlaylist(prevIndex)
    }
  }

  def playNextFromPlaylist(): Unit = {
    val nextIndex: Int = currentlyPlaying.get + 1
    if (items.size > nextIndex) {
      startPlayingFileFromPlaylist(nextIndex)
    }
  }

  def playPause(): Unit = mediaPlayerOpt foreach { mediaPlayer =>
    if (isPlaying.get) { mediaPlayer.pause() } else { mediaPlayer.play() }
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

  def registerListenerToCurrentlyPlayingTrackFromPlaylist(listener: InvalidationListener): Unit = {
    currentlyPlaying.addListener(listener)
  }

  override def addToPlaylist(song: Song): Unit = {
    items.add(SongTableRepresentation(song))
  }
}
