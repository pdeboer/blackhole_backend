package controllers

import models.User
import play.api.Logger
import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.mvc.{Controller, _}

/**
 * Controller for products HTTP interface.
 */
object Users extends Controller {

  /**
   * Returns an array of products’ EAN codes.
   */
  def list = Action {
    val userEmails = User.findAll.map(_.email)
    Ok(Json.toJson(userEmails))
  }

  /**
   * Formats a Product instance as JSON.
   */
  implicit object UserWrites extends Writes[User] {
    def writes(p: User) = Json.obj(
      "email" -> Json.toJson(p.email),
      "firstname" -> Json.toJson(p.firstname),
      "lastname" -> Json.toJson(p.lastname),
      "roleId" -> Json.toJson(p.roleId),
      "password" -> Json.toJson(p.password)
    )
  }

  /**
   * Returns details of the given product.
   */
  def details(email: String) = Action {
    User.findByEmail(email).map { user =>
      Ok(Json.toJson(user))
    }.getOrElse(NotFound)
  }

  /**
   * Parses a JSON object
   */
  implicit val userReads: Reads[User] = (
    (JsPath \ "email").read[String] and
      (JsPath \ "firstname").read[String] and
      (JsPath \ "lastname").read[String] and
      (JsPath \ "roleId").read[Int] and
      (JsPath \ "password").read[String]
    )(User.apply _)

  /**
   * Saves a product
   */
  def save(email: String) = Action(parse.json) { request =>
    Logger.info("start")
    try {
      val userJson = request.body
      val user = userJson.as[User]
      User.save(user)
      Ok("Saved")
    }
    catch {
      case e:IllegalArgumentException => BadRequest("User not found")
      case e:Exception => {
        Logger.info("exception = %s" format e)
        BadRequest("Invalid EAN")
      }
    }
  }

}