package de.mirkokoester.luna.player.model

import scala.collection.mutable

trait ObservablePlayer { player: Player =>
  protected val observers: mutable.MutableList[PlayerObserver] = mutable.MutableList.empty

  def register(observer: PlayerObserver): Unit = {
    observers += observer
  }
}
