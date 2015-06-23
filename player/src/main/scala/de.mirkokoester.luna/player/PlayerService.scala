package de.mirkokoester.luna.player

import java.io.File
import javax.sound.sampled._

trait PlayerService {
  def play(filename: String): Boolean
  def available(): Boolean
  def needsMoreData(): Boolean
  def playNextBlock(): Boolean
  def close(): Unit
}

class PlayerServiceImpl extends PlayerService {
  val bufferSize = 32768
  val data: Array[Byte] = new Array[Byte](bufferSize) // TODO make it variable in size?
  var line: SourceDataLine = null
  var ais: AudioInputStream = null
  var decAis: AudioInputStream = null

  var nBytesRead: Int = 0
  var nBytesWritten: Int = 0

  override def close(): Unit = {
    if (line != null) closeLine()
    if (ais != null) ais.close()
    if (decAis != null) decAis.close()
  }

  override def playNextBlock(): Boolean = {
    // Start
    line.start()
    println(s"buffer size: ${bufferSize}, available: ${line.available()}")
    if (nBytesRead != -1) {
      nBytesRead = decAis.read(data, 0, data.length)
      if (nBytesRead != -1) {
        nBytesWritten = line.write(data, 0, nBytesRead)
        true
      } else {
        false
        // TODO cancelPlaySchedule()
      }
    } else {
      false
      // TODO cancelPlaySchedule()
    }
  }


  private def rawplay(targetFormat: AudioFormat, din: AudioInputStream): Unit = {
    println(s"rawplay")

    def getLineAndPlay(): Unit = {
      line = getLine(targetFormat)
      if (line != null) {
        // TODO return success
      } else {
        // TODO handle situation
      }
    }

    if (din != null) {
      decAis = din

      if (line != null) { // reuse line
        if (line.getFormat == targetFormat) {
          // TODO return success
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

  /**
   * line and audioSource available
   * @return
   */
  override def available(): Boolean =
    line != null && line.isOpen && decAis != null

  override def needsMoreData(): Boolean = {
    available() && bufferSize <= line.available()
  }


  private def closeLine(): Unit = {
    line.drain()
    line.stop()
    line.close()
  }






  override def play(filename: String): Boolean = {
    println(s"playing file '$filename'")
    val file: File = new File(filename)
    if (file.exists() && file.isFile && file.canRead) {
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

      true
    } else {
      false
    }
  }
}
