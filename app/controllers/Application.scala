package controllers

import core.AuthAction
import play.api.mvc._
import views._
import models.{User, Task, Coordinate, Tasklog}
import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

object Application extends Controller {
  val form = Form(
    tuple(
      "email" -> text,
      "password" -> text
    )
  )

  /**
   * The login action redirect
   *
   * @return
   */
  def login = Action {
    Ok(html.login.render(""))
  }


  /**
   * Submit of the login tuple for the admin services
   *
   * @return
   */
  def submitlogin = Action { implicit request =>
    val (email, password) = form.bindFromRequest.get
    val user = User.findByEmailAndPassword(email, password)
    user match {
      case Some(theValue) => Ok(views.html.show.render(email)).withSession("active" -> email)
      case None           => Ok(views.html.sorry.render())
    }
}


  def tasklist = AuthAction {
    Ok(views.html.tasklist.render(Task.findAll()))
  }

  def taskloglist = AuthAction {
    Ok(views.html.tasklog.render(Tasklog.findAll()))
  }


  def userlist = AuthAction {
    Ok(views.html.userlist.render(User.findAll()))
  }


  def coordinatelist = AuthAction {
    Ok(views.html.coordinatelist.render(Coordinate.findAll()))
  }

  /**
   * Logout and clean the session.
   */
  def logout = Action {
    Redirect(routes.Application.login).withNewSession.flashing(
      "success" -> "You've been logged out")
  }

}
