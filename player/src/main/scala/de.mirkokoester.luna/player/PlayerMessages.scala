package de.mirkokoester.luna.player

trait PlayerMessage

object PlayerMessages {
  case class PlayFile(filename: String) extends PlayerMessage
  case object KeepPlaying extends PlayerMessage
  case object Pause extends PlayerMessage
  case object Resume extends PlayerMessage
  case object Stop extends PlayerMessage
}

trait PlayerResponse

object PlayerResponses {
  case object PlayFileSuccessResponse extends PlayerResponse
  case class PlayFileExceptionResponse(exception: Exception) extends PlayerResponse
  case object PauseSuccessResponse extends PlayerResponse
  case class PauseExceptionResponse(exception: Exception) extends PlayerResponse
  case object ResumeSuccessResponse extends PlayerResponse
  case class ResumeExceptionResponse(exception: Exception) extends PlayerResponse
  case object StopSuccessResponse extends PlayerResponse
  case class StopExceptionResponse(exception: Exception) extends PlayerResponse
}
