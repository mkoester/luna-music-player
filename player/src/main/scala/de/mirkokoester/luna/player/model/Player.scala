package de.mirkokoester.luna.player.model

import javafx.beans.property.SimpleIntegerProperty
import javafx.collections.{ObservableList, FXCollections}

class Player {
  val currentlyPlaying: SimpleIntegerProperty = new SimpleIntegerProperty
  val items: ObservableList[SongTableRepresentation] = FXCollections.observableArrayList(
    new SongTableRepresentation(Song.fromPath("/home/mk/Music/Foo Fighters - In Your Honor   (CD2)/Foo Fighters - Cold Day In The Sun.mp3")),
    new SongTableRepresentation(Song.fromPath("/home/mk/Music/Dido - Life For Rent/Dido - White Flag.mp3")),
    new SongTableRepresentation(Song.fromPath("/home/mk/Music/Intergalactic Lovers - Little Heavy Burdens/Intergalactic Lovers - No Regrets.mp3")),
    new SongTableRepresentation(Song.fromPath("/home/mk/Music/Dido - Safe Trip Home/Dido - Grafton Street.mp3"))
  )

}
