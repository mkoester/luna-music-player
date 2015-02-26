package controllers

import akka.util.Timeout
import akka.pattern.ask
import de.mirkokoester.luna.play.plugins.LunaPlayerPlugin
import de.mirkokoester.luna.player.PlayerMessages._
import de.mirkokoester.luna.player.PlayerResponses._
import play.api._
import play.api.mvc._

import scala.concurrent.duration._

/**
 *
 */
object PlayerController extends Controller {
  import play.api.libs.concurrent.Execution.Implicits.defaultContext
  implicit val timeout = Timeout(3 seconds)

  def playFile(filename: String): Action[AnyContent] = Action.async {
    LunaPlayerPlugin.player ? PlayFile(filename) map {
      case PlayFileSuccessResponse =>
        Ok(s"playing '$filename'")

      case PlayFileExceptionResponse(exception) =>
        InternalServerError(exception.getMessage)

      case _ =>
        InternalServerError("an error occured")
    }
  }

  def stopPlaying(): Action[AnyContent] = Action.async {
    LunaPlayerPlugin.player ? Stop map {
      case StopSuccessResponse =>
        Ok("stopped playing")

      case StopExceptionResponse(exception) =>
        InternalServerError(exception.getMessage)

      case _ =>
        InternalServerError("an error occured")
    }
  }

  def pausePlaying(): Action[AnyContent] = Action.async {
    LunaPlayerPlugin.player ? Pause map {
      case PauseSuccessResponse =>
        Ok("paused playing")

      case PauseExceptionResponse(exception) =>
        InternalServerError(exception.getMessage)

      case _ =>
        InternalServerError("an error occured")
    }
  }

  def resumePlaying(): Action[AnyContent] = Action.async {
    LunaPlayerPlugin.player ? Resume map {
      case ResumeSuccessResponse =>
        Ok("resumed playing")

      case ResumeExceptionResponse(exception) =>
        InternalServerError(exception.getMessage)

      case _ =>
        InternalServerError("an error occured")
    }
  }
}
