package controllers

import play.api.mvc._
import views._
import models.{User, Task}
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

  /*
  def index = Action {
    Ok(html.index())
  }
  */

  def login = Action {
    Ok(html.login.render(""))
  }


  def submitlogin = Action { implicit request =>
    val (email, password) = form.bindFromRequest.get
    val test = User.findByEmailAndPassword(email, password)
    //val results = User.findAll()
    //val tasks = Task.findAll()
    //Logger.debug(results.toString())
    test match {
      case Some(theValue) => Ok(views.html.show.render(User.findAll(), Task.findAll()))
      case None           => Ok(views.html.sorry.render())
    }
}


}
