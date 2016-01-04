package de.mirkokoester.luna.model

class SongMedialibraryTabelRepresentation(song: Song) {
  import song._
  def getPath:        String  = path
  def getAlbumArtist: String  = albumArtist.getOrElse("")
  def getArtist:      String  = artist.getOrElse("")
  def getTitle:       String  = title.getOrElse("")
  def getComposer:    String  = composer.getOrElse("")
  def getAlbum:       String  = album.getOrElse("")
  def getDiscNo:      Integer = discNo.map(Integer.valueOf).orNull
  def getDiscTotal:   Integer = discTotal.map(Integer.valueOf).orNull
  def getGenre:       String  = genre.getOrElse("")
  def getComment:     String  = comment.getOrElse("")
  def getLanguage:    String  = language.getOrElse("")
  def getTrack:       Integer = track.map(Integer.valueOf).orNull
  def getTrackTotal:  Integer = trackTotal.map(Integer.valueOf).orNull
  def getYear:        Integer = year.map(Integer.valueOf).orNull
  def getDuration:    Int     = duration
  def getId:          String  = idOpt.map(_.toString).getOrElse("")
}

object SongMedialibraryTabelRepresentation {
  def apply(song: Song) = new SongMedialibraryTabelRepresentation(song)
}
