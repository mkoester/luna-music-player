package de.mirkokoester.luna.player;

import de.mirkokoester.luna.model.Player;
import de.mirkokoester.luna.model.Song;
import de.mirkokoester.luna.model.SongTableRepresentation;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class PlaylistController implements Initializable {
    @FXML private TableView<SongTableRepresentation> playlistTableView;
    private Stage playlistStage;
    private PlayerController playerController;
    private Player playerModel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // row number code borrowed from http://stackoverflow.com/a/16407347
        TableColumn numberCol = new TableColumn("#");
        numberCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<SongTableRepresentation, SongTableRepresentation>, ObservableValue<SongTableRepresentation>>() {
            @Override public ObservableValue<SongTableRepresentation> call(TableColumn.CellDataFeatures<SongTableRepresentation, SongTableRepresentation> p) {
                return new ReadOnlyObjectWrapper(p.getValue());
            }
        });

        numberCol.setCellFactory(new Callback<TableColumn<SongTableRepresentation, SongTableRepresentation>, TableCell<SongTableRepresentation, SongTableRepresentation>>() {
            @Override public TableCell<SongTableRepresentation, SongTableRepresentation> call(TableColumn<SongTableRepresentation, SongTableRepresentation> param) {
                return new TableCell<SongTableRepresentation, SongTableRepresentation>() {
                    @Override protected void updateItem(SongTableRepresentation item, boolean empty) {
                        super.updateItem(item, empty);

                        if (this.getTableRow() != null && item != null) {
                            setText(this.getTableRow().getIndex()+1+"");
                        } else {
                            setText("");
                        }
                    }
                };
            }
        });
        numberCol.setSortable(false);

        TableColumn trackCol    = new TableColumn("track");
        TableColumn durationCol = new TableColumn("duration");

        playlistTableView.getColumns().addAll(numberCol, trackCol, durationCol);

        trackCol.setCellValueFactory(
                new PropertyValueFactory<SongTableRepresentation,String>("title")
        );
        durationCol.setCellValueFactory(
                new PropertyValueFactory<SongTableRepresentation,String>("duration")
        );

        playlistTableView.setOnMouseClicked(click -> {
            if (click.getClickCount() == 2) {
                SongTableRepresentation currentItemSelected = (SongTableRepresentation) playlistTableView.getSelectionModel().getSelectedItem();
                if (null != currentItemSelected) {
                    int index = playerModel.items().indexOf(currentItemSelected);
                    if (index >= 0) {
                        playerModel.currentlyPlaying().set(index);
                        playerController.startPlayingFile(currentItemSelected.song());
                    }
                }
            }
        });
    }

    protected boolean registerPlayerControllerAndStageAndPlayerModel(PlayerController playerController, Stage playlistStage, Player playerModel) {
        if (null == this.playerController && null == this.playlistStage && null == this.playerModel) {
            this.playerController = playerController;
            this.playlistStage = playlistStage;
            this.playerModel = playerModel;
            playlistTableView.setItems(this.playerModel.items());
            return true;
        } else {
            return false;
        }
    }

    public void play(ActionEvent actionEvent) {
        if (null != playerController) {
            playerController.playPause(null);
        } else {
            System.out.println("no playerController");
        }
    }

    public void close(ActionEvent actionEvent) {
        playlistStage.hide();
    }

    public void addFile(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        File file = fileChooser.showOpenDialog(null);
        if (null != file) {
            playerModel.items().add(new SongTableRepresentation(Song.fromPath(file.toString())));
        }
    }
}
