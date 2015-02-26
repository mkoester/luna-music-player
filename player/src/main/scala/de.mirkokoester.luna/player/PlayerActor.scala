package de.mirkokoester.luna.player

import java.io.File
import java.util.concurrent.TimeUnit
import javax.sound.sampled._

import akka.actor.{Props, Cancellable, Actor}
import de.mirkokoester.luna.player.PlayerResponses

import scala.concurrent.duration._

/**
 *
 */
class PlayerActor extends Actor {
  import context.dispatcher
  val bufferSize = 32768
  val data: Array[Byte] = new Array[Byte](bufferSize) // TODO make it variable in size?
  var line: SourceDataLine = null
  var ais: AudioInputStream = null
  var decAis: AudioInputStream = null

  var nBytesRead: Int = 0
  var nBytesWritten: Int = 0

  var playSchedule: Cancellable = null
  def cancelPlaySchedule(): Unit = {
    if (playSchedule != null) {
      playSchedule.cancel()
      playSchedule = null
    }
  }

  override def preStart(): Unit = ()
  override def postStop(): Unit = {
    cancelPlaySchedule()
    if (line != null) closeLine()
    if (ais != null) ais.close()
    if (decAis != null) decAis.close()
  }



  private def play(): Unit = {
    // Start
    line.start()
    println(line.available())
    if (nBytesRead != -1) {
      nBytesRead = decAis.read(data, 0, data.length)
      if (nBytesRead != -1) {
        nBytesWritten = line.write(data, 0, nBytesRead)
      } else {
        cancelPlaySchedule()
        // TODO
      }
    } else {
      cancelPlaySchedule()
      // TODO
    }
  }

  private def startPlaying(): Unit = {
    nBytesRead = 0
    nBytesWritten = 0
    playSchedule = context.system.scheduler.schedule(
      initialDelay = Duration(0, TimeUnit.MILLISECONDS),
      interval     = Duration(20, TimeUnit.MILLISECONDS),
      receiver     = self,
      message      = PlayerMessages.KeepPlaying
    )
  }

  private def rawplay(targetFormat: AudioFormat, din: AudioInputStream): Unit = {
    println(s"rawplay")

    def getLineAndPlay(): Unit = {
      line = getLine(targetFormat)
      if (line != null) {
        startPlaying()
      } else {
        // TODO handle situation
      }
    }

    if (din != null) {
      decAis = din

      if (line != null) { // reuse line
        if (line.getFormat == targetFormat) {
          startPlaying()
        } else {
          closeLine()
          getLineAndPlay()
        }
      } else {
        getLineAndPlay()
      }
    } else {
      // TODO handle situation
    }
  }

  /**
   * throws LineUnavailableException
   * @param audioFormat
   * @return
   */
  private def getLine(audioFormat: AudioFormat): SourceDataLine = {
    var res: SourceDataLine = null
    val info: DataLine.Info = new DataLine.Info(classOf[SourceDataLine], audioFormat)
    res = AudioSystem.getLine(info).asInstanceOf[SourceDataLine]
    res.open(audioFormat)

    res
  }

  private def lineAndAudioSourceAvailable(): Boolean =
    line != null && line.isOpen && decAis != null

  private def closeLine(): Unit = {
    line.drain()
    line.stop()
    line.close()
  }

  override def receive: Receive = {
    case PlayerMessages.PlayFile(filename) =>
      println(s"playing file '$filename'")
      val file: File = new File(filename)
      ais = AudioSystem.getAudioInputStream(file)

      val baseFormat: AudioFormat = ais.getFormat
      val decodedFormat: AudioFormat = new AudioFormat(
        AudioFormat.Encoding.PCM_SIGNED,
        baseFormat.getSampleRate,
        16,
        baseFormat.getChannels,
        baseFormat.getChannels * 2,
        baseFormat.getSampleRate,
        false)
      decAis = AudioSystem.getAudioInputStream(decodedFormat, ais)

      // Play now.
      rawplay(decodedFormat, decAis)
      sender() ! PlayerResponses.PlayFileSuccessResponse

    case PlayerMessages.KeepPlaying =>
      if (lineAndAudioSourceAvailable()) {
        if (line.available() > bufferSize) play()
      } else {
        cancelPlaySchedule
        // TODO manage situation
      }

    case PlayerMessages.Pause =>
      println("pause")
      cancelPlaySchedule()
      sender() ! PlayerResponses.PauseSuccessResponse

    case PlayerMessages.Resume =>
      println("resume")
      if (lineAndAudioSourceAvailable()) {
        startPlaying()
        sender() ! PlayerResponses.ResumeSuccessResponse
      } else {
        sender() ! PlayerResponses.ResumeExceptionResponse(new Exception("no line or no audio source available"))
      }
  }
}

object PlayerActor {
  val props = Props[PlayerActor]
}
