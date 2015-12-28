package de.mirkokoester.luna

import java.io.File
import javafx.embed.swing.JFXPanel

import de.mirkokoester.luna.player.PlayerJavaFX

/**
  *
  */
object Start {
  def main (args: Array[String]) {
    new JFXPanel()
    val player = PlayerJavaFX.play(new File("/home/mk/Music/Foo Fighters - In Your Honor   (CD2)/Foo Fighters - Cold Day In The Sun.mp3"))
    Thread.sleep(10000)
    println(player.getCurrentTime)
    player.stop()
    println("stopped")
    player.dispose()
    println("disposed")
  }
}
