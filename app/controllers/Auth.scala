package core

import play.api.mvc._
import scala.concurrent._
import play.api.mvc.Results._

object AuthAction extends ActionBuilder[Request] {

  def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]) = {
    if (request.session.isEmpty) {
      // redirect
      Future.successful(Redirect("/"))
    } else {
      //proceed with action as normal
      block(request)
    }
  }

}