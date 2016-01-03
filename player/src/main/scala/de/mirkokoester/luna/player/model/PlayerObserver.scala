package de.mirkokoester.luna.player.model

trait PlayerObserver {
  def onReady(): Unit
  def onPlaying(): Unit
  def onPaused(): Unit
  def onStopped(): Unit

  def setPlayingTitle(title: String): Unit
  def updateValues(): Unit
}
