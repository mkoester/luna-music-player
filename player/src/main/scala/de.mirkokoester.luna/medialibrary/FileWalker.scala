package de.mirkokoester.luna.medialibrary

import java.io.IOException
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.{FileVisitResult, Files, SimpleFileVisitor, Path}
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import scala.collection.JavaConverters._
import scala.util.control.NonFatal
import scala.util.{Failure, Success}

object FileWalker {
  def log(message: String): Unit = println(s"FileWalker: $message")

  private def toStringOption(s: String): Option[String] = if (s.nonEmpty) Some(s) else None
  private def toIntOption(s: String):    Option[Int] = try { Some(s.toInt) } catch { case NonFatal(e) => None }

  def walkRecursively(root: Path): Unit = {
    Files.walkFileTree(root, new SimpleFileVisitor[Path] {
      override def visitFile(path: Path, attrs: BasicFileAttributes) = {
        if (attrs.isRegularFile && path.toString.endsWith(".mp3")) { // TODO follow symlinks, other formats
          log(s"file: ${path.toAbsolutePath.toString}")
          val f = AudioFileIO.read(path.toFile)
          val tag = f.getTag
          val audioHeader = f.getAudioHeader

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

/*          log(s"ALBUM_ARTIST: $albumArtistField")
          log(s"ARTIST:       $artistField")
          log(s"TITLE:        $titleField")
          log(s"COMPOSER:     $composerField")
          log(s"ALBUM:        $albumField")
          log(s"DISC_NO:      $discNoField")
          log(s"DISC_TOTAL:   $discTotalField")
          log(s"GENRE:        $genreField")
          log(s"COMMENT:      $commentField")
          log(s"LANGUAGE:     $langField")
          log(s"TRACK:        $trackField")
          log(s"TRACK_TOTAL:  $trackTotalField")
          log(s"YEAR:         $yearField")*/

          val s = Song(
            None,
            path.toAbsolutePath.toString,
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
            toIntOption(yearField)
          )

          import scala.concurrent.ExecutionContext.Implicits.global
          val ftr = LibraryH2DB.addSong(s)
          ftr onComplete {
            case Success(_) => log(s"success: $path")
            case Failure(e) => log(s"failure: $path, ${e.getMessage}")
          }

          println()
        }
        // TODO
        FileVisitResult.CONTINUE
      }

      override def visitFileFailed(file: Path, exc: IOException): FileVisitResult = {
        // TODO
        FileVisitResult.CONTINUE
      }
    })
  }

}
