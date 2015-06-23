package de.mirkokoester.luna.play.plugins

import de.mirkokoester.luna.player.{PlayerServiceImpl, PlayerActor}
import play.api._
import play.api.libs.concurrent.Akka
import akka.actor._

class LunaPlayerPlugin(app: Application) extends Plugin {
  implicit val application: Application = app
  lazy val player = Akka.system.actorOf(PlayerActor.props(new PlayerServiceImpl()), "player")

  override def onStart() = {
println("started Plugin")
  }

  override def onStop() = {

  }

  override val enabled = true
}


object LunaPlayerPlugin {
  val player: ActorRef = Play.current.plugin[LunaPlayerPlugin]
    .getOrElse(throw new RuntimeException("LunaPlayer plugin not loaded"))
    .player
}
