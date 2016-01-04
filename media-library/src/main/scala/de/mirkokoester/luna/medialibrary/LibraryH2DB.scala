package de.mirkokoester.luna.medialibrary

// Use H2Driver to connect to an H2 database
import de.mirkokoester.luna.model.Song
import slick.backend.DatabasePublisher
import slick.driver.H2Driver.api._

import scala.concurrent.Future

object LibraryH2DB {
  val db = Database.forConfig("h2mem")

  // Schema for the "song" table:
  final class SongTable(tag: Tag)
    extends Table[Song](tag, "song") {

    def id          = column[Int]           ("id", O.PrimaryKey, O.AutoInc)
    def path        = column[String]        ("path")
    def albumArtist = column[Option[String]]("albumArtist")
    def artist      = column[Option[String]]("artist")
    def title       = column[Option[String]]("title")
    def composer    = column[Option[String]]("composer")
    def album       = column[Option[String]]("album")
    def discNo      = column[Option[Int]]   ("discNo")
    def discTotal   = column[Option[Int]]   ("discTotal")
    def genre       = column[Option[String]]("genre")
    def comment     = column[Option[String]]("comment")
    def language    = column[Option[String]]("language")
    def track       = column[Option[Int]]   ("track")
    def trackTotal  = column[Option[Int]]   ("trackTotal")
    def year        = column[Option[Int]]   ("year")
    def duration    = column[Int]           ("duration")

    def * = (path, albumArtist, artist, title, composer, album, discNo, discTotal, genre, comment, language, track,
      trackTotal, year, duration, id.?) <> ((Song.apply _).tupled, Song.unapply)
  }

  // Base query for querying the song table:
  lazy val songs = TableQuery[SongTable]

  private val setup = DBIO.seq(
    // Create the tables, including primary and foreign keys
    songs.schema.create
  )

  def addSong(s: Song): Future[Int] = db.run(songs += s)

  def setupDB() = db.run(setup)

  val q1 = for(s <- LibraryH2DB.songs) yield s
  def queryAllSongs(): DatabasePublisher[Song] = LibraryH2DB.db.stream(q1.result)
}
