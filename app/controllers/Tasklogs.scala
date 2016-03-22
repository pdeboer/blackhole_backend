package controllers

import models.{Tasklog, User}
import play.api.Logger
import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.mvc.{Controller, _}

/**
 * This Controller serves for the logging of completed tasks
 *
 * @author David Pinezich <david.pinezich@uzh.ch>
 * @version 1.0.0
 */
object Tasklogs extends Controller {

  /**
   * List all log events to a Json format
   *
   * @return void
   */
  def list = Action {
    val allTasks = Tasklog.findAll.map(_.uuid)
    Ok(Json.toJson(allTasks))
  }

  /**
   * Json write object
   *
   */
  implicit object tasklogWrites extends Writes[Tasklog] {
    def writes(t: Tasklog) = Json.obj(
      "uuid" -> Json.toJson(t.uuid),
      "sdss_id" -> Json.toJson(t.sdss_id),
      "question_id" -> Json.toJson(t.question_id),
      "answer" -> Json.toJson(t.answer),
      "ip" -> Json.toJson(t.ip))
  }

  /**
   * Details of the Tasklog
   *
   * @param uuid The uuid of the user which completes a task
   * @return
   */
  def details(uuid: Int) = Action {
    Tasklog.findById(uuid).map { tasklog =>
      Ok(Json.toJson(tasklog))
    }.getOrElse(NotFound)
  }

  /**
   * Parses the json object
   */
  implicit val tasklogReads: Reads[Tasklog] = (
    (JsPath \ "uuid").read[String] and
      (JsPath \ "set").read[Int] and
    (JsPath \ "sdss_id").read[BigDecimal] and
    (JsPath \ "question_id").read[Int] and
    (JsPath \ "spectra_id").read[Int] and
    (JsPath \ "answer").read[String] and
    (JsPath \ "ip").read[String])(Tasklog.apply _)

  def save() = Action(parse.json) { request =>
    //Logger.info("start")
    try {
      val tasklogJson = request.body
      val log = tasklogJson.as[Tasklog] //@todo set id as optional //@todo rename uuid with jwt

      val id = Tasklog.insertTasklog(User.getUuidByEncryptedString(log.uuid), log.set, log.sdss_id, log.question_id, log.spectra_id, log.answer, log.ip)
      Ok(id.get.toString())
    } catch {
      case e: IllegalArgumentException => BadRequest("Log file does not exist")
      case e: Exception =>
        Logger.info("exception = %s" format e)
        BadRequest("Invalid log")
    }
  }
}