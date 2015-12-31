package de.mirkokoester.luna.model

import java.io.File

import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey

import scala.util.control.NonFatal

case class Song(path: String, albumArtist: Option[String], artist: Option[String],
                title: Option[String], composer: Option[String], album: Option[String], discNo: Option[Int],
                discTotal: Option[Int], genre: Option[String], comment: Option[String], language: Option[String],
                track: Option[Int], trackTotal: Option[Int], year: Option[Int], duration: Int, idOpt: Option[Int] = None)

object Song {
  private def toStringOption(s: String): Option[String] = if (null != s && s.nonEmpty) Some(s) else None
  private def toIntOption(s: String):    Option[Int] = try { Some(s.toInt) } catch { case NonFatal(e) => None }

  def fromPath(path: String): Song = {
    val file = new File(path)
    val audioFile = AudioFileIO.read(file)
    val tag = audioFile.getTag
    val audioHeader = audioFile.getAudioHeader

    val albumArtistField = tag.getFirst(FieldKey.ALBUM_ARTIST)
    val artistField      = tag.getFirst(FieldKey.ARTIST)
    val titleField       = tag.getFirst(FieldKey.TITLE)
    val composerField    = tag.getFirst(FieldKey.COMPOSER)
    val albumField       = tag.getFirst(FieldKey.ALBUM)
    val discNoField      = tag.getFirst(FieldKey.DISC_NO)
    val discTotalField   = tag.getFirst(FieldKey.DISC_TOTAL)
    val genreField       = tag.getFirst(FieldKey.GENRE)
    val commentField     = tag.getFirst(FieldKey.COMMENT)
    val langField        = tag.getFirst(FieldKey.LANGUAGE)
    val trackField       = tag.getFirst(FieldKey.TRACK)
    val trackTotalField  = tag.getFirst(FieldKey.TRACK_TOTAL)
    val yearField        = tag.getFirst(FieldKey.YEAR)
    val duration         = audioHeader.getTrackLength

    Song(
      file.getAbsolutePath,
      toStringOption(albumArtistField),
      toStringOption(artistField),
      toStringOption(titleField),
      toStringOption(composerField),
      toStringOption(albumField),
      toIntOption(discNoField),
      toIntOption(discTotalField),
      toStringOption(genreField),
      toStringOption(commentField),
      toStringOption(langField),
      toIntOption(trackField),
      toIntOption(trackTotalField),
      toIntOption(yearField),
      duration
    )
  }
}

class SongTableRepresentation(val song: Song) {
  def getTitle: String = song.artist.getOrElse("") + " - " + song.title.getOrElse("")
  def getDuration: String = song.duration.toString
}

object SongTableRepresentation {
  def apply(song: Song) = new SongTableRepresentation(song)
}
