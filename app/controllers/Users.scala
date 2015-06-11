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
      "password" -> nonEmptyText
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
      userForm.bindFromRequest().fold(
      formWithErrors => BadRequest(views.html.userlist.render(User.findAll())),
      tempUser => {
        val id = User.insertUser(tempUser.email, tempUser.lastname, tempUser.lastname, tempUser.roleId, tempUser.password)
        Ok(views.html.userlist.render(User.findAll()))
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
    Ok(views.html.userlist.render(User.findAll()))
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
      "password" -> Json.toJson(p.password)
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
      (JsPath \ "password").read[String]
    )(User.apply _)


}