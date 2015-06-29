package controllers

import models.Workset
import play.api.Logger
import play.api.data._
import play.api.data.Forms._
import play.api.libs.functional.syntax._
import play.api.libs.json._
import io.really.jwt._
import play.api.libs.json.Json
import play.api.mvc._


object Worksets extends Controller {

  /**
   * Show list of worksets by id
   *
   * @return
   */
  def getWorkset(id: Int) = Action {
    val flash = play.api.mvc.Flash(Map(

    ))
    Ok(views.html.workset.render(Workset.findById(id), flash))
  }

}