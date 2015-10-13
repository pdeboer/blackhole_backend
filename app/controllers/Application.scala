package controllers

import core.AuthAction
import models.{Comment, Coordinate, Task, Tasklog, User}
import play.api.data.Forms._
import play.api.data._
import play.api.mvc._
import views._

/**
 * This Controller is used as a central Controller for the backend
 *
 * @author David Pinezich <david.pinezich@uzh.ch>
 * @version 1.0.0
 */
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
   * Show tasklist
   *
   * @return void
   */
  def commentlist = AuthAction {
    Ok(views.html.commentlist.render(Comment.findAll()))
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
   * Show list of logged tasks
   *
   * @return void
   */
  def taskloglistTop = AuthAction {
    Ok(views.html.taskloglistTop.render(Tasklog.findbestRated()))
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
    val jwtoken = User.getJWT("david.pinezich@gmail.com", "test")
    val jwTokenDecode = User.decodeJWT(jwtoken)

    Ok(html.showjwt.render(jwtoken, jwTokenDecode))
  }

  /**
   * Show Userlist with classifications
   *
   * @return void
   */
  def userlistWithClassifiction = AuthAction {
    Ok(views.html.classificationUserlist.render(User.findAllWithClassification()))
  }


  /**
   * Show classifications of a user
   *
   * @return void
   */
  def userClassifications(id: Int) = AuthAction {
    Ok(views.html.userClassifications.render(Tasklog.findAllClassificationsOfUser(id)))
  }


  /**
   * Logout and clean the session.
   */
  def logout = Action {
    Redirect(routes.Application.login).withNewSession.flashing(
      "success" -> "You've been logged out")
  }

  /**
   * Wrapper method for obsolete download of images
   *
   * @param opt String options for downloading
   * @param size String size of the image (hxw)
   * @param limit Int limit of images
   * @param offset Int offset of images to start with
   *
   * @return void Runs the downloader
   */
  def downloadCoordinates(opt: String, size: String, limit: Int, offset: Int) = Action {
    ImageDownloader.downloadFile(opt, size, limit, offset)
    Ok(html.downloader.render())
  }

}
