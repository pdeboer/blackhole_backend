package controllers

import java.io._

import play.api.Play.current
import play.api._
import play.api.mvc.{Controller, _}

/**
 * This Image Controller makes it possible to have an image folder out of the actual compiled image folder
 * with over 2mio pictures, a compilation would take hours or probably not end at alls
 *
 * @author David Pinezich <david.pinezich@uzh.ch>
 * @version 1.0.0
 */
object Images extends Controller {

  val AbsolutePath = """^(/|[a-zA-Z]:\\).*""".r

  /**
   * Delivers Images on strict asset
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
      Logger.error("Images not able to be shown: " + file)
      NotFound
    }
  }

}