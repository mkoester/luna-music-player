package de.mirkokoester.luna.player

import java.util.concurrent.TimeUnit

import akka.actor.{Props, Cancellable, Actor}

import scala.concurrent.duration._

/**
 *
 */
class PlayerActor(playerService: PlayerService) extends Actor {
  import context.dispatcher

  var playSchedule: Cancellable = null
  def cancelPlaySchedule(): Unit = {
    if (playSchedule != null) {
      playSchedule.cancel()
      playSchedule = null
    }
  }

  override def preStart(): Unit = ()
  override def postStop(): Unit = {
    cancelPlaySchedule()
  }



  private def startPlaying(): Unit = {
    playSchedule = context.system.scheduler.schedule(
      initialDelay = Duration(0, TimeUnit.MILLISECONDS),
      interval     = Duration(20, TimeUnit.MILLISECONDS),
      receiver     = self,
      message      = PlayerMessages.KeepPlaying
    )
  }

  override def receive: Receive = {
    case PlayerMessages.PlayFile(filename) =>
      if (playerService.play(filename)) {
        startPlaying()
        sender() ! PlayerResponses.PlayFileSuccessResponse
      } else {
        sender() ! PlayerResponses.PlayFileExceptionResponse(new Exception("play: todo"))
      }

    case PlayerMessages.KeepPlaying =>
      println("keep playing")
      if (playerService.available()) {
        if (playerService.needsMoreData()) {
          if (!playerService.playNextBlock()) {
            cancelPlaySchedule()
          }
        }
      } else {
        cancelPlaySchedule()
        // TODO manage situation
      }

    case PlayerMessages.Pause =>
      println("pause")
      cancelPlaySchedule()
      sender() ! PlayerResponses.PauseSuccessResponse

    case PlayerMessages.Resume =>
      println("resume")
      if (playerService.available()) {
        startPlaying()
        sender() ! PlayerResponses.ResumeSuccessResponse
      } else {
        sender() ! PlayerResponses.ResumeExceptionResponse(new Exception("no line or no audio source available"))
      }
  }
}

object PlayerActor {
  def props(playerService: PlayerService) = Props(new PlayerActor(playerService))
}
