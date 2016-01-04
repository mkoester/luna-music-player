package de.mirkokoester.luna.medialibrary

import java.net.URL
import java.util.ResourceBundle
import javafx.collections.{FXCollections, ObservableList}
import javafx.event.ActionEvent
import javafx.fxml.{FXMLLoader, FXML, Initializable}
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.{Scene, Parent}
import javafx.scene.control.{TableColumn, TableView}
import javafx.stage.Stage

import de.mirkokoester.luna.model.SongMedialibraryTabelRepresentation

import scala.concurrent.ExecutionContextExecutor
import scala.util.control.NonFatal
import scala.util.{Failure, Success, Try}

class MedialibraryController extends Initializable {
  @FXML protected var medialibraryTableView: TableView[SongMedialibraryTabelRepresentation] = null
  val items: ObservableList[SongMedialibraryTabelRepresentation] = FXCollections.observableArrayList()
  implicit val ec: ExecutionContextExecutor = scala.concurrent.ExecutionContext.Implicits.global // TODO get during initialization

  override def initialize(location: URL, resources: ResourceBundle): Unit = {
    if (null != medialibraryTableView) {
      medialibraryTableView.setItems(items)

      // TODO make it configurable which columns should be shown
      val albumArtistCol: TableColumn[SongMedialibraryTabelRepresentation, SongMedialibraryTabelRepresentation] = new TableColumn("albumArtist")
      val artistCol: TableColumn[SongMedialibraryTabelRepresentation, SongMedialibraryTabelRepresentation] = new TableColumn("artist")
      val titleCol: TableColumn[SongMedialibraryTabelRepresentation, SongMedialibraryTabelRepresentation] = new TableColumn("title")
      val durationCol: TableColumn[SongMedialibraryTabelRepresentation, SongMedialibraryTabelRepresentation] = new TableColumn("duration")
      val trackCol: TableColumn[SongMedialibraryTabelRepresentation, SongMedialibraryTabelRepresentation] = new TableColumn("track")
      val yearCol: TableColumn[SongMedialibraryTabelRepresentation, SongMedialibraryTabelRepresentation] = new TableColumn("year")
      val albumCol: TableColumn[SongMedialibraryTabelRepresentation, SongMedialibraryTabelRepresentation] = new TableColumn("album")
      val commentCol: TableColumn[SongMedialibraryTabelRepresentation, SongMedialibraryTabelRepresentation] = new TableColumn("comment")
      medialibraryTableView.getColumns.addAll(albumArtistCol, artistCol, titleCol, durationCol, trackCol, yearCol, albumCol, commentCol)
      albumArtistCol.setCellValueFactory(new PropertyValueFactory[SongMedialibraryTabelRepresentation, SongMedialibraryTabelRepresentation]("albumArtist"))
      artistCol.setCellValueFactory(new PropertyValueFactory[SongMedialibraryTabelRepresentation, SongMedialibraryTabelRepresentation]("artist"))
      titleCol.setCellValueFactory(new PropertyValueFactory[SongMedialibraryTabelRepresentation, SongMedialibraryTabelRepresentation]("title"))
      durationCol.setCellValueFactory(new PropertyValueFactory[SongMedialibraryTabelRepresentation, SongMedialibraryTabelRepresentation]("duration"))
      trackCol.setCellValueFactory(new PropertyValueFactory[SongMedialibraryTabelRepresentation, SongMedialibraryTabelRepresentation]("track"))
      yearCol.setCellValueFactory(new PropertyValueFactory[SongMedialibraryTabelRepresentation, SongMedialibraryTabelRepresentation]("year"))
      albumCol.setCellValueFactory(new PropertyValueFactory[SongMedialibraryTabelRepresentation, SongMedialibraryTabelRepresentation]("album"))
      commentCol.setCellValueFactory(new PropertyValueFactory[SongMedialibraryTabelRepresentation, SongMedialibraryTabelRepresentation]("comment"))
    }

    LibraryH2DB.queryAllSongs().foreach { song =>
      items.add(SongMedialibraryTabelRepresentation(song))
    } onComplete {
      case Success(_) => println(s"MedialibraryController: added all songs, #songs in list: ${items.size()}")
      case Failure(NonFatal(e)) => println(s"MedialibraryController: failure adding songs, ${e.getMessage}")
        e.printStackTrace()
    }
  }

  def close(actionEvent: ActionEvent) {

  }
}

object MedialibraryController {
  def getStageAndController(): Try[(Stage, MedialibraryController)] = { // TODO set windows size
    val fxmlLoader: FXMLLoader = new FXMLLoader
    Try {
      val ml: Parent = fxmlLoader.load(getClass.getResource("medialibrary.fxml").openStream)
      val medialibraryStage = new Stage
      medialibraryStage.setTitle("Media Library")
      medialibraryStage.setScene(new Scene(ml, 450, 450))
      val medialibraryController = fxmlLoader.getController[MedialibraryController]()
      (medialibraryStage, medialibraryController)
    }
  }
}