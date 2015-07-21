package controllers

import controllers.Application._
import models.Task
import play.api.Logger
import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.mvc.{Controller, _}
import play.api.data._
import play.api.data.Forms._


object Tasks extends Controller {
  // The user login Tuple
  val form = Form(
    tuple(
      "comment" -> text,
      "taskid" -> number
    )
  )


  def updateComment = Action { implicit request =>

      val (comment, taskid) = form.bindFromRequest.get

      val task = Task.updateComment(taskid.toInt, comment)
      val flash = play.api.mvc.Flash(Map(
        "success" -> "comment updated"
      ))
      Ok(views.html.tasklist.render(Task.findAll()))
    }



  /**
   * List tasks as a json representation
   *
   * @return Tasks Json
   */
  def list = Action {
    val allTasks = Task.findAll.map(_.task)
    Ok(Json.toJson(allTasks))
  }
  /**
   * Formats a Task instance as JSON.
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
      "businessRule" -> Json.toJson(t.businessRule),
      "comment" -> Json.toJson(t.comment)
    )
  }

  /**
   * Returns details of the task Json formatted
   *
   * @param id Int
   * @return void
   */
  def details(id: Int) = Action {
    Task.findById(id).map { task =>
      Ok(Json.toJson(task))
    }.getOrElse(NotFound)
  }

  /**
   * Returns details of the task Json formatted
   *
   * @param id Int
   * @return void
   */
  def changeComment(id: Int) = Action {
    //Logger.debug(Task.findById(id).toString)
    Task.findById(id).map { task =>
      Ok(views.html.tasklistcomment.render(task))
    }.getOrElse(NotFound)
  }

  /**
   * Returns details of the task Json formatted
   *
   * @param id Int
   * @return void
   */
  def getNext(id: Int) = Action {
    val newId = id + 1;
    Task.findById(newId).map { nextTask =>
      Ok(Json.toJson(nextTask))
    }.getOrElse(NotFound)
  }

  /**
   * Details Html
   *
   * @param id
   * @return
   */
  def detailsHtml(id: Int) = Action {
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
      (JsPath \ "businessRule").read[String] and
      (JsPath \ "comment").read[String]
    )(Task.apply _)

}