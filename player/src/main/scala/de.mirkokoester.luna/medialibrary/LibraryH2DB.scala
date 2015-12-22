package de.mirkokoester.luna.medialibrary

// Use H2Driver to connect to an H2 database
import slick.driver.H2Driver.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class Song(idOpt: Option[Int], path: String, albumArtist: Option[String], artist: Option[String],
                title: Option[String], composer: Option[String], album: Option[String], discNo: Option[Int],
                discTotal: Option[Int], genre: Option[String], comment: Option[String], language: Option[String],
                track: Option[Int], trackTotal: Option[Int], year: Option[Int])

object LibraryH2DB {
  val db = Database.forConfig("h2mem")
  def log(message: String): Unit = println(s"LibraryH2DB: $message")

  // Definition of the SONGS table
  class Songs(tag: Tag) extends Table[
    (Option[Int], String, Option[String], Option[String], Option[String], Option[String], Option[String], Option[Int],
      Option[Int], Option[String], Option[String], Option[String], Option[Int], Option[Int], Option[Int])](tag, "songs") {
    def id          = column[Int]           ("id", O.PrimaryKey, O.AutoInc)
    def path        = column[String]        ("path") // This is the primary key column
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
    // Every table needs a * projection with the same type as the table's type parameter
    def * = (id.?, path, albumArtist, artist, title, composer, album, discNo, discTotal, genre, comment, language, track, trackTotal, year)
  }
  val songs = TableQuery[Songs]

  val setup = DBIO.seq(
    // Create the tables, including primary and foreign keys
    songs.schema.create
  )

  def addSong(s: Song): Future[Unit] = db.run(DBIO.seq(songs += Song.unapply(s).get))

  def setupDB() = db.run(setup)

  val q1 = for(s <- LibraryH2DB.songs) yield s
  def queryAllSongs() = LibraryH2DB.db.stream(q1.result).foreach(s => log(s"song: $s"))

}

