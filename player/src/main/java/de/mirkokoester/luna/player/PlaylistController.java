package de.mirkokoester.luna.player;

import de.mirkokoester.luna.model.Song;
import de.mirkokoester.luna.model.SongTableRepresentation;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
    @FXML private TableView<SongTableRepresentation> playlistTableview;
    private Stage playlistStage;
    private ObservableList<SongTableRepresentation> items = FXCollections.observableArrayList (
            new SongTableRepresentation(Song.fromPath("/home/mk/Music/Foo Fighters - In Your Honor   (CD2)/Foo Fighters - Cold Day In The Sun.mp3")),
            new SongTableRepresentation(Song.fromPath("/home/mk/Music/Dido - Life For Rent/Dido - White Flag.mp3")),
            new SongTableRepresentation(Song.fromPath("/home/mk/Music/Intergalactic Lovers - Little Heavy Burdens/Intergalactic Lovers - No Regrets.mp3")),
            new SongTableRepresentation(Song.fromPath("/home/mk/Music/Dido - Safe Trip Home/Dido - Grafton Street.mp3"))
    );
    private PlayerController playerController;

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

        playlistTableview.getColumns().addAll(numberCol, trackCol, durationCol);

        trackCol.setCellValueFactory(
                new PropertyValueFactory<SongTableRepresentation,String>("title")
        );
        durationCol.setCellValueFactory(
                new PropertyValueFactory<SongTableRepresentation,String>("duration")
        );

        playlistTableview.setItems(items);
        playlistTableview.setOnMouseClicked(click -> {
            if (click.getClickCount() == 2) {
                SongTableRepresentation currentItemSelected = (SongTableRepresentation) playlistTableview.getSelectionModel().getSelectedItem();
                playerController.startPlayingFile(currentItemSelected.song());
            }
        });
    }

    protected boolean registerPlayerControllerAndStage(PlayerController playerController, Stage playlistStage) {
        if (null == this.playerController && null == this.playlistStage) {
            this.playerController = playerController;
            this.playlistStage = playlistStage;
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
            items.add(new SongTableRepresentation(Song.fromPath(file.toString())));
        }
    }
}
