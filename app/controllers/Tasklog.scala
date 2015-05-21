package controllers

import models.Tasklog
import play.api.Logger
import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.mvc.{Controller, _}

/**
 * Controller for products HTTP interface.
 */
object Tasklogs extends Controller {

  def list = Action {
    val allTasks = Tasklog.findAll.map(_.uuid)
    Ok(Json.toJson(allTasks))
  }
  /**
   * Formats a Product instance as JSON.
   */
  implicit object UserWrites extends Writes[Tasklog] {
    def writes(t: Tasklog) = Json.obj(
      "id" -> Json.toJson(t.id),
      "uuid" -> Json.toJson(t.uuid),
      "question" -> Json.toJson(t.question),
      "answer" -> Json.toJson(t.answer)
    )
  }

  /**
   * Returns details of the given product.
   */
  def details(id: Int) = Action {
    Tasklog.findById(id).map { tasklog =>
      Ok(Json.toJson(tasklog))
    }.getOrElse(NotFound)
  }

  /**
   * Parses a JSON object
   */
  implicit val userReads: Reads[Tasklog] = (
    (JsPath \ "id").read[Int] and
      (JsPath \ "uuid").read[String] and
      (JsPath \ "question").read[String] and
      (JsPath \ "answer").read[String]
    )(Tasklog.apply _)

}