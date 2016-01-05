package de.mirkokoester.luna.model

import scala.util.Random

class SongMedialibraryTabelRepresentation(val song: Song) {
  import song._
  import SongMedialibraryTabelRepresentation._
  val getPath:        String  = path
  val getAlbumArtist: String  = albumArtist.getOrElse("")
  val getArtist:      String  = artist.getOrElse("")
  val getTitle:       String  = title.getOrElse("")
  val getComposer:    String  = composer.getOrElse("")
  val getAlbum:       String  = album.getOrElse("")
  val getDiscNo:      Integer = discNo.map(Integer.valueOf).orNull
  val getDiscTotal:   Integer = discTotal.map(Integer.valueOf).orNull
  val getGenre:       String  = genre.getOrElse("")
  val getComment:     String  = comment.getOrElse("")
  val getLanguage:    String  = language.getOrElse("")
  val getTrack:       Integer = track.map(Integer.valueOf).orNull
  val getTrackTotal:  Integer = trackTotal.map(Integer.valueOf).orNull
  val getYear:        Integer = year.map(Integer.valueOf).orNull
  val getDuration:    Int     = duration
  val getId:          String  = idOpt.map(_.toString).getOrElse("")

  val getRating:      Integer = Random.nextInt(11) // [0-10] or null // TODO get real rating
  val getRatingStar:  String  = ratingToStars(getRating)
}

object SongMedialibraryTabelRepresentation {
  def apply(song: Song) = new SongMedialibraryTabelRepresentation(song)
  def ratingToStars(rating: Integer): String = {
    if (null != rating) {
/*      val builder = new StringBuilder()
      var r = rating

      while (r >= 2) {
        r = r - 2
        builder.append("\u2605") // ★
      }
      if (r == 1) {
        builder.append("\u272F") // ✮ (✯✭)
      }

      val length = builder.length
      if (length < 5) {
        builder.replace(length, 5, "\u2606\u2606\u2606\u2606\u2606") // ☆
        builder.length = 5
      }

      builder.result()*/

      (rating: Int) match {
        case  0 => "\u2606\u2606\u2606\u2606\u2606"
        case  1 => "\u272F\u2606\u2606\u2606\u2606"
        case  2 => "\u2605\u2606\u2606\u2606\u2606"
        case  3 => "\u2605\u272F\u2606\u2606\u2606"
        case  4 => "\u2605\u2605\u2606\u2606\u2606"
        case  5 => "\u2605\u2605\u272F\u2606\u2606"
        case  6 => "\u2605\u2605\u2605\u2606\u2606"
        case  7 => "\u2605\u2605\u2605\u272F\u2606"
        case  8 => "\u2605\u2605\u2605\u2605\u2606"
        case  9 => "\u2605\u2605\u2605\u2605\u272F"
        case 10 => "\u2605\u2605\u2605\u2605\u2605"
        case  _ => "?????"
      }
    } else {
      "?????"
    }
  }
}
