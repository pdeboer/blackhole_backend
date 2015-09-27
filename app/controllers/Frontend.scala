package controllers

import java.io._

import play.api.Play.current
import play.api._
import play.api.mvc.{Controller, _}

/**
 * This Frontend controller makes it possible to have the frontend out of the actual source,
 * that is a must because we want to have an angular app which is not deployed with the actual scala/play app
 *
 * @author David Pinezich <david.pinezich@uzh.ch>
 * @version 1.0.0
 */
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