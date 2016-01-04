package de.mirkokoester.luna.player.model

import de.mirkokoester.luna.model.Song

class SongTableRepresentation(val song: Song) {
  def getTitle: String = song.getRepresentation
  def getDuration: String = song.duration.toString
}

object SongTableRepresentation {
  def apply(song: Song) = new SongTableRepresentation(song)
}
