package controllers

import core.AuthAction
import views._
import models.{User, Task, Coordinate, Tasklog}
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

object Application extends Controller {

  // The user login Tuple
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
   * @return void
   */
  def submitlogin = Action { implicit request =>
    val (email, password) = form.bindFromRequest.get
    val user = User.findByEmailAndPassword(email, password)
    user match {
      case Some(theValue) => Ok(views.html.show.render(email)).withSession("active" -> email)
      case None           => Ok(views.html.sorry.render())
    }
}

  /**
   * Show tasklist
   *
   * @return void
   */
  def tasklist = AuthAction {
    Ok(views.html.tasklist.render(Task.findAll()))
  }

  /**
   * Shows the index
   * @return
   */
  def show = Action { request =>
    request.session.get("active").map { email =>
      Ok(views.html.show.render(email))
    }.getOrElse {
      Unauthorized("Not connected")
    }
  }
  /**
   * Show list of logged tasks
   *
   * @return void
   */
  def taskloglist = AuthAction {
    Ok(views.html.tasklog.render(Tasklog.findAll()))
  }


  /**
   * Show list of users
   *
   * @return void
   */
  def userlist = AuthAction {
    val flash = play.api.mvc.Flash(Map(

    ))
    Ok(views.html.userlist.render(User.findAll(), flash))
  }


  /**
   * Show list of coordinates
   *
   * @return void
   */
  def fullcoordinatelist() = AuthAction {
    val flash = play.api.mvc.Flash(Map(

    ))
    Ok(views.html.coordinatelist.render(Coordinate.findAll(), flash))
  }


  /**
   * Show list of coordinates
   *
   * @return void
   */
  def somecoordinatelist(limit: Int, offset: Int) = AuthAction {
    val flash = play.api.mvc.Flash(Map(

    ))
    Ok(views.html.coordinatelist.render(Coordinate.findSome(limit, offset), flash))
  }

  def showjwt = AuthAction {
    val jwtoken = User.getJWT("david.pinezich@gmail.com", "test");
    val jwTokenDecode = User.decodeJWT(jwtoken)

    Ok(html.showjwt.render(jwtoken, jwTokenDecode))
  }

  /**
   * Logout and clean the session.
   */
  def logout = Action {
    Redirect(routes.Application.login).withNewSession.flashing(
      "success" -> "You've been logged out")
  }


  def downloadCoordinates(opt: String, size: String) = Action {
    Coordinates.downloadFile(opt, size);
    Ok(html.downloader.render())
  }

}
