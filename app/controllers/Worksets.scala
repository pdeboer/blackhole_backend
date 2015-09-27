package controllers

import models.Workset
import play.api.mvc._

/**
 * This Controller is used as a controller for the workset
 *
 * @author David Pinezich <david.pinezich@uzh.ch>
 * @version 1.0.0
 */
object Worksets extends Controller {

  /**
   * Show list of worksets by id
   *
   * @return rendered view of worksets byId
   */
  def getWorkset(id: Int) = Action {
    val flash = play.api.mvc.Flash(Map())
    Ok(views.html.workset.render(Workset.findById(id), flash))
  }

}