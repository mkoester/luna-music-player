package de.mirkokoester.luna.medialibrary

import java.io.IOException
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.{FileVisitResult, Files, Path, SimpleFileVisitor}

import de.mirkokoester.luna.model.Song

import scala.util.control.NonFatal
import scala.util.{Failure, Success}

object FileWalker {
  def log(message: String): Unit = println(s"FileWalker: $message")

  def walkRecursively(root: Path): Unit = {
    Files.walkFileTree(root, new SimpleFileVisitor[Path] {
      override def visitFile(path: Path, attrs: BasicFileAttributes) = {
        if (attrs.isRegularFile && path.toString.endsWith(".mp3")) { // TODO follow symlinks, other formats
          log(s"file: ${path.toAbsolutePath.toString}")
          val s = Song.fromPath(path.toAbsolutePath.toString)

          import scala.concurrent.ExecutionContext.Implicits.global
          val ftr = LibraryH2DB.addSong(s)
          ftr onComplete {
            case Success(_) => log(s"success: $path")
            case Failure(NonFatal(e)) => log(s"failure: $path, ${e.getMessage}")
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
