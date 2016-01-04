package de.mirkokoester.luna

import java.nio.file.Paths
import javafx.application.Application
import javafx.event.EventHandler
import javafx.fxml.FXMLLoader
import javafx.scene.{Parent, Scene}
import javafx.stage.{Stage, WindowEvent}

import de.mirkokoester.luna.medialibrary.{FileWalker, LibraryH2DB}

import scala.concurrent.ExecutionContextExecutor
import scala.util.control.NonFatal
import scala.util.{Failure, Success}

class Main extends Application {
  def start(primaryStage: Stage) {
    val root: Parent = FXMLLoader.load(getClass.getResource("player/player.fxml"))
    primaryStage.setTitle("Luna Music Player")
    primaryStage.setScene(new Scene(root, 400, 475))
    primaryStage.setOnCloseRequest(new EventHandler[WindowEvent] {
      override def handle(event: WindowEvent): Unit = System.exit(0) // TODO handle application shutdown
    })
    primaryStage.show()
  }
}

object Main {
  implicit val ec: ExecutionContextExecutor = scala.concurrent.ExecutionContext.Implicits.global // TODO remove
  def main(args: Array[String]) {
    val path = Paths.get("/home/mk/Music")
    println(s"Main: file: ${path.toString}")

    LibraryH2DB.setupDB() onComplete {
      case Success(_) => println(s"Main: success setting up db")
      case Failure(NonFatal(e)) => println(s"failure setting up db, ${e.getMessage}")
        e.printStackTrace()
    }
    Thread.sleep(400)

    LibraryH2DB.queryAllSongs().foreach(s => println(s"Main: song: $s"))
    Thread.sleep(1000)
    println("Main: queryAllSongs done")

    FileWalker.walkRecursively(path)
    Thread.sleep(2000)
    println(s"Main: FileWalker.walkRecursively($path) done")

    Application.launch(classOf[Main], args: _*)
  }
}
