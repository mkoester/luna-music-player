package de.mirkokoester.luna.model


trait Playlist {
  def clearPlaylist(): Unit
  def addToPlaylist(song: Song): Unit
}
