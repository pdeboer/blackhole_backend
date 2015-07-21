package controllers

import anorm._
import controllers.Tasklogs._
import models.{User, Tasklog, Comment}
import play.api.Logger
import play.api.db.DB
import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.mvc.{Controller, _}


object Comments extends Controller {

  /**
   * List Comments as a json representation
   *
   * @return Comment Json
   */
  def list = Action {
    val allComments = Comment.findAll.map(_.comment)
    Ok(Json.toJson(allComments))
  }
  /**
   * Formats a Comment instance as Json
   */
  implicit object UserWrites extends Writes[Comment] {
    def writes(c: Comment) = Json.obj(
      "sdss_id" -> Json.toJson(c.sdss_id),
      "set_id" -> Json.toJson(c.set_id),
      "rating" -> Json.toJson(c.rating),
      "comment" -> Json.toJson(c.comment),
      "ip" -> Json.toJson(c.ip)
    )
  }

  /**
   * Returns details of the task Json formatted
   *
   * @param id Int
   * @return void
   */
  def details(id: Int) = Action {
    Comment.findById(id).map { c =>
      Ok(Json.toJson(c))
    }.getOrElse(NotFound)
  }



  def save() = Action(parse.json) { request =>
    //Logger.info("start")
    try {
      val commentJson = request.body
      val comment = commentJson.as[Comment]
     // Logger.debug(comment.sdss_id.toString + " " + comment.set_id.toString + " " + comment.rating.toString + " " + comment.comment + " " + comment.ip)
      val id = Comment.insertComment(comment.sdss_id, comment.set_id, comment.rating, comment.comment, comment.ip)
      Ok(id.get.toString())
    }
    catch {
      case e:IllegalArgumentException => BadRequest("Comment not acceptable")
      case e:Exception =>
        Logger.info("exception = %s" format e)
        BadRequest("Invalid Comment")
    }
  }





  /**
   * Parses a JSON object
   */
  implicit val userReads: Reads[Comment] = (
      (JsPath \ "sdss_id").read[BigDecimal] and
      (JsPath \ "set_id").read[Int] and
      (JsPath \ "rating").read[Int] and
      (JsPath \ "comment").read[String] and
        (JsPath \ "ip").read[String]
    )(Comment.apply _)

}