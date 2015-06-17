package controllers

import models.User
import play.api.Logger
import play.api.data._
import play.api.data.Forms._
import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.mvc.{Controller, _}

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
      "uuid" -> nonEmptyText
    )(User.apply)(User.unapply)
  )

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
   * Inserts an User
   *
   * @return void
   */
  def insertUser = Action { implicit request =>
    val flash = play.api.mvc.Flash(Map("error" -> "User was not inserted"))
      userForm.bindFromRequest().fold(
      formWithErrors => BadRequest(views.html.userlist.render(User.findAll(), flash)),
      tempUser => {
        val id = User.insertUser(tempUser.email, tempUser.lastname, tempUser.lastname, tempUser.roleId, tempUser.password)
        val flash = play.api.mvc.Flash(Map(
          "success" -> "User was succesfully inserted"
        ))
        Ok(views.html.userlist.render(User.findAll(), flash))
      })

  }

  /**
   * Delete an User
   *
   * @param email
   * @return void
   */
  def deleteUser(email: String) = Action { implicit request =>
    val result = User.deleteUser(email)
    val flash = play.api.mvc.Flash(Map(
      "success" -> "User was succesfully deleted"
    ))
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
      "success" -> "active state was successfully changed"
    ))
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
      "uuid" -> Json.toJson(p.uuid)
    )
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
        (JsPath \ "uuid").read[String]
    )(User.apply _)


}