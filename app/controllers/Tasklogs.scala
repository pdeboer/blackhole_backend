package controllers

import models.Tasklog
import play.api.Logger
import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.mvc.{Controller, _}


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
      "question" -> Json.toJson(t.question),
      "answer" -> Json.toJson(t.answer)
    )
  }

  /**
   * Details of the Tasklog
   *
   * @param id
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
      (JsPath \ "question").read[String] and
      (JsPath \ "answer").read[String]
    )(Tasklog.apply _)


  def save() = Action(parse.json) { request =>
    //Logger.info("start")
    try {
      val tasklogJson = request.body
      val log = tasklogJson.as[Tasklog] //@todo set id as optional
      val id = Tasklog.insertTasklog(log.uuid, log.question, log.answer)
      Ok(id.get.toString())
    }
    catch {
      case e:IllegalArgumentException => BadRequest("Log file does not exist")
      case e:Exception =>
        Logger.info("exception = %s" format e)
        BadRequest("Invalid log")
    }
  }
}