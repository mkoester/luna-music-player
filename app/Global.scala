import java.io.File
import java.nio.file.Paths

import de.mirkokoester.luna.medialibrary.{Song, LibraryH2DB, FileWalker}
import de.mirkokoester.luna.player.PlayerJavaFX
import play.api._

object Global extends GlobalSettings {

  override def onStart(app: Application): Unit = {
    val path = Paths.get("/home/mk/Music")
    println(s"Global: file: ${path.toString}")

    LibraryH2DB.setupDB()
    Thread.sleep(400)

/*    val dummySong = Song(None, "/home/mk/Downloads/test.mp3", None, None, None, None, None, Some(1), Some(1), None, None, None, Some(1), Some(12), Some(1980))
    LibraryH2DB.addSong(dummySong)
    Thread.sleep(400)
    println("Global: dummySong added")

    LibraryH2DB.queryAllSongs()
    Thread.sleep(1000)
    println("Global: queryAllSongs done")*/

    FileWalker.walkRecursively(path)
    Thread.sleep(2000)
    println(s"Global: FileWalker.walkRecursively($path) done")

    LibraryH2DB.queryAllSongs()
    Thread.sleep(200)
    println("Global: queryAllSongs done")
  }
}
