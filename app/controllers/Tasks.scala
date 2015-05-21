package controllers

import models.Task
import play.api.Logger
import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.mvc.{Controller, _}

/**
 * Controller for products HTTP interface.
 */
object Tasks extends Controller {

  def list = Action {
    val allTasks = Task.findAll.map(_.task)
    Ok(Json.toJson(allTasks))
  }
  /**
   * Formats a Product instance as JSON.
   */
  implicit object UserWrites extends Writes[Task] {
    def writes(t: Task) = Json.obj(
      "id" -> Json.toJson(t.id),
      "task" -> Json.toJson(t.task),
      "taskType" -> Json.toJson(t.taskType),
      "value" -> Json.toJson(t.value),
      "formerTaskId" -> Json.toJson(t.formerTaskId),
      "laterTaskId" -> Json.toJson(t.laterTaskId),
      "exitOn" -> Json.toJson(t.exitOn),
      "comment" -> Json.toJson(t.comment)
    )
  }

  /**
   * Returns details of the given product.
   */
  def details(id: Int) = Action {
    Task.findById(id).map { task =>
      Ok(Json.toJson(task))
    }.getOrElse(NotFound)
  }

  /**
   * Parses a JSON object
   */
  implicit val userReads: Reads[Task] = (
    (JsPath \ "id").read[Int] and
      (JsPath \ "task").read[String] and
      (JsPath \ "taskType").read[String] and
      (JsPath \ "value").read[Int] and
      (JsPath \ "formerTaskId").read[Int] and
      (JsPath \ "laterTaskId").read[Int] and
      (JsPath \ "exitOn").read[String] and
      (JsPath \ "comment").read[String]
    )(Task.apply _)

}