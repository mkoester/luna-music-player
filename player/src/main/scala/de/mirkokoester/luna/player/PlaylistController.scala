package de.mirkokoester.luna.player

import javafx.beans.{Observable, InvalidationListener}
import javafx.scene.Node
import javafx.scene.input.MouseEvent

import de.mirkokoester.luna.player.model.Player
import de.mirkokoester.luna.player.model.Song
import de.mirkokoester.luna.player.model.SongTableRepresentation
import javafx.beans.property.ReadOnlyObjectWrapper
import javafx.beans.value.ObservableValue
import javafx.event.{EventHandler, ActionEvent}
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.{TableRow, TableCell, TableColumn, TableView}
import javafx.scene.control.cell.PropertyValueFactory
import javafx.stage.FileChooser
import javafx.stage.Stage
import javafx.util.Callback
import java.io.File
import java.net.URL
import java.util.ResourceBundle

import scala.collection.JavaConverters._

class PlaylistController extends Initializable {
  @FXML protected var playlistTableView: TableView[SongTableRepresentation] = null
  private var playlistStage: Stage = null
  private var playerController: PlayerController = null
  private var playerModel: Player = null

  def initialize(location: URL, resources: ResourceBundle) {
    val numberCol: TableColumn[SongTableRepresentation, SongTableRepresentation] = new TableColumn("#")
    numberCol.setCellValueFactory(new Callback[TableColumn.CellDataFeatures[SongTableRepresentation, SongTableRepresentation], ObservableValue[SongTableRepresentation]]() {
      def call(p: TableColumn.CellDataFeatures[SongTableRepresentation, SongTableRepresentation]): ObservableValue[SongTableRepresentation] = {
        new ReadOnlyObjectWrapper(p.getValue)
      }
    })
    numberCol.setCellFactory(new Callback[TableColumn[SongTableRepresentation, SongTableRepresentation], TableCell[SongTableRepresentation, SongTableRepresentation]]() {
      def call(param: TableColumn[SongTableRepresentation, SongTableRepresentation]): TableCell[SongTableRepresentation, SongTableRepresentation] = {
        new TableCell[SongTableRepresentation, SongTableRepresentation]() {
          protected override def updateItem(item: SongTableRepresentation, empty: Boolean) {
            super.updateItem(item, empty)
            if (this.getTableRow != null && item != null) {
              setText(this.getTableRow.getIndex + 1 + "")
            }
            else {
              setText("")
            }
          }
        }
      }
    })
    numberCol.setSortable(false)
    val trackCol: TableColumn[SongTableRepresentation, SongTableRepresentation] = new TableColumn("track")
    val durationCol: TableColumn[SongTableRepresentation, SongTableRepresentation] = new TableColumn("duration")
    playlistTableView.getColumns.addAll(numberCol, trackCol, durationCol)
    trackCol.setCellValueFactory(new PropertyValueFactory[SongTableRepresentation, SongTableRepresentation]("title"))
    durationCol.setCellValueFactory(new PropertyValueFactory[SongTableRepresentation, SongTableRepresentation]("duration"))
    playlistTableView.setOnMouseClicked(new EventHandler[MouseEvent] {
      override def handle(event: MouseEvent): Unit = {
        if (event.getClickCount == 2) {
          val currentItemSelectedOpt = Option(playlistTableView.getSelectionModel().getSelectedItem())
          currentItemSelectedOpt foreach { currentItemSelected =>
            val index = playerModel.items.indexOf(currentItemSelected)
            if (index >= 0) {
              playerModel.startPlayingFileFromPlaylist(index)
            }
          }
        }
      }
    })
  }

  protected [player] def registerPlayerControllerAndStageAndPlayerModel(playerController: PlayerController, playlistStage: Stage, playerModel: Player): Boolean = {
    if (null == this.playerController && null == this.playlistStage && null == this.playerModel) {
      this.playerController = playerController
      this.playlistStage = playlistStage
      playlistStage.getScene().getStylesheets().add(getClass().getResource("playlist.css").toExternalForm())
      this.playerModel = playerModel
      playlistTableView.setItems(this.playerModel.items)

      playerModel.registerListenerToCurrentlyPlayingTrackFromPlaylist(new InvalidationListener() {
        override def invalidated(observable: Observable): Unit = {
          var currentRow = 0;
          val currentlyPlaying = playerModel.getCurrentlyPlaying
          for (node: Node <- playlistTableView.lookupAll("TableRow").asScala) {
            node match {
              case row: TableRow[_] =>
                if (currentRow == currentlyPlaying) {
                  row.getStyleClass().add("currentlyPlaying")
                } else {
                  row.getStyleClass().remove("currentlyPlaying")
                }
                currentRow = currentRow + 1

              case _ =>
            }
          }
        }
      })

      true
    }
    else {
      false
    }
  }

  def play(actionEvent: ActionEvent) {
    if (null != playerController) {
      playerController.playPause(null)
    }
    else {
      System.out.println("no playerController")
    }
  }

  def close(actionEvent: ActionEvent) {
    playlistStage.hide()
  }

  def addFile(actionEvent: ActionEvent) {
    val fileChooser: FileChooser = new FileChooser
    fileChooser.setTitle("Open Resource File")
    val file: File = fileChooser.showOpenDialog(null)
    if (null != file) {
      playerModel.items.add(new SongTableRepresentation(Song.fromPath(file.toString)))
    }
  }
}
