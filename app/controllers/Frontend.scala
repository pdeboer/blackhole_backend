package controllers

import java.io._
import play.api.mvc.{Controller, _}
import play.api._
import play.api.mvc._

import Play.current


object Frontend extends Controller {

  val AbsolutePath = """^(/|[a-zA-Z]:\\).*""".r

  /**
   * Delivers Frontend
   *
   * @param rootPath
   * @param file
   * @return
   */
  def at(rootPath: String, file: String): Action[AnyContent] = Action { request =>
    val fileToServe = rootPath match {
      case AbsolutePath(_) => new File(rootPath, file)
      case _ => new File(Play.application.getFile(rootPath), file)
    }

    if (fileToServe.exists) {
      Ok.sendFile(fileToServe, inline = true)
    } else {
      Logger.error("Frontend not deliverable: " + file)
      NotFound
    }
  }

}