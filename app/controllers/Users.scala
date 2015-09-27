package controllers

import models.{User, UserRegister}
import play.api.data.Forms._
import play.api.data._
import play.api.libs.functional.syntax._
import play.api.libs.json.{Json, _}
import play.api.mvc.{Controller, _}

/**
 * This Controller is used as a controller for the contact form
 *
 * @author David Pinezich <david.pinezich@uzh.ch>
 * @version 1.0.0
 */
object Users extends Controller {

  // User Form
  val userForm = Form(
    mapping(
      "email" -> nonEmptyText,
      "lastname" -> nonEmptyText,
      "firstname" -> nonEmptyText,
      "roleId" -> number,
      "active" -> number,
      "password" -> nonEmptyText,
      "uuid" -> nonEmptyText)(User.apply)(User.unapply))

  // User Form
  val userFormRegister = Form(
    mapping(
      "email" -> nonEmptyText,
      "lastname" -> nonEmptyText,
      "firstname" -> nonEmptyText,
      "password" -> nonEmptyText)(UserRegister.apply)(UserRegister.unapply))

  /**
   * List "all" users by email
   *
   * @return List Json
   */
  def list = Action {
    val userEmails = User.findAll.map(_.email)
    Ok(Json.toJson(userEmails))
  }

  /**
   * Login Tuple
   */
  implicit val login = (
    (__ \ 'email).read[String] and
    (__ \ 'password).read[String]) tupled

  /**
   * Submit of the login tuple for the admin services
   *
   * @return void
   */
  def getUuid() = Action { request =>
    request.body.asJson.map { json =>
      json.validate[(String, String)].map {
        case (email, password) => Ok(User.getUuid(email, password))
      }.recoverTotal {
        e => BadRequest("Detected error:" + JsError.toFlatJson(e))
      }
    }.getOrElse {
      BadRequest("Expecting Json data")
    }
  }

  /**
   * Submit the login tuple to get a jwt token
   *
   * @return void
   */
  def getJWT() = Action { request =>
    request.body.asJson.map { json =>
      json.validate[(String, String)].map {
        case (email, password) => Ok(User.getJWT(email, password))
      }.recoverTotal {
        e => BadRequest("Error" + JsError.toFlatJson(e))
      }
    }.getOrElse {
      BadRequest("Expecting Json Data")
    }

  }

  /**
   * Inserts an User
   *
   * @return void
   */
  def insertUser = Action { implicit request =>
    val flash = play.api.mvc.Flash(Map("error" -> "User was not inserted"))
    userForm.bindFromRequest().fold(
      formWithErrors => BadRequest(views.html.userlist.render(User.findAll(), flash)),
      tempUser => {
        val id = User.insertUser(tempUser.email, tempUser.firstname, tempUser.lastname, tempUser.roleId, tempUser.password)
        val flash = play.api.mvc.Flash(Map(
          "success" -> "User was succesfully inserted"))
        Ok(views.html.userlist.render(User.findAll(), flash))
      })

  }

  /**
   * Inserts an User from frontend
   *
   * @return void
   */
  def insertUserRegister = Action { implicit request =>
    userFormRegister.bindFromRequest().fold(
      formWithErrors => BadRequest("failed"),
      tempUser => {
        if (User.getUuidByEmail(tempUser.email) != "") {
          Ok("exists already")
        } else {
          val id = User.insertUser(tempUser.email, tempUser.firstname, tempUser.lastname, 1, tempUser.password)
          Ok(User.getJWT(tempUser.email, tempUser.password))
        }
      })

  }

  /**
   * Delete an User
   *
   * @param email The email of the user
   * @return void Gives back the rendered userlist
   */
  def deleteUser(email: String) = Action { implicit request =>
    val result = User.deleteUser(email)
    val flash = play.api.mvc.Flash(Map(
      "success" -> "User was succesfully deleted"))
    Ok(views.html.userlist.render(User.findAll(), flash))
  }

  /**
   * Change active state
   *
   * @param email
   * @return void
   */
  def changeActiveUser(email: String) = Action { implicit request =>
    val result = User.changeActiveUser(email)
    val flash = play.api.mvc.Flash(Map(
      "success" -> "active state was successfully changed"))
    Ok(views.html.userlist.render(User.findAll(), flash))
  }

  /**
   * Writes an User by Json Obj
   * @todo see if this distracts the frontend users
   */
  implicit object UserWrites extends Writes[User] {
    def writes(p: User) = Json.obj(
      "email" -> Json.toJson(p.email),
      "firstname" -> Json.toJson(p.firstname),
      "lastname" -> Json.toJson(p.lastname),
      "roleId" -> Json.toJson(p.roleId),
      "active" -> Json.toJson(p.active),
      "password" -> Json.toJson(p.password),
      "uuid" -> Json.toJson(p.uuid))
  }

  /**
   * Lists details of all Users
   *
   * @param email
   * @return void
   */
  def details(email: String) = Action {
    User.findByEmail(email).map { user =>
      Ok(Json.toJson(user))
    }.getOrElse(NotFound)
  }

  /**
   * Parses the User Json Object
   */
  implicit val userReads: Reads[User] = (
    (JsPath \ "email").read[String] and
    (JsPath \ "firstname").read[String] and
    (JsPath \ "lastname").read[String] and
    (JsPath \ "roleId").read[Int] and
    (JsPath \ "active").read[Int] and
    (JsPath \ "password").read[String] and
    (JsPath \ "uuid").read[String])(User.apply _)

}