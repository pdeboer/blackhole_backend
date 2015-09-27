package controllers

import models.Spectra
import play.api.libs.json._
import play.api.mvc.{Controller, _}

/**
 * This Controller is used as a controller for the spectra model
 *
 * @author David Pinezich <david.pinezich@uzh.ch>
 * @version 1.0.0
 */
object Spectras extends Controller {

  /**
   * List tasks as a json representation
   *
   * @return Spectra Json
   */
  def list = Action {
    val allSpectras = Spectra.findAll.map(_.id)

    Ok(Json.toJson(allSpectras))
  }

  /**
   * Returns details of the spectra Json formatted
   *
   * @param name String A String to ensure the transformation is done right
   * @return Shows all spectras by json
   */
  def listByName(name: String) = Action {
    val allSpectras = Spectra.findByName(name).map(_.specobjid)
    Ok(Json.toJson(allSpectras.head.toString()))
  }

  /**
   * Formats a Spectra instance as JSON.
   */
  implicit object UserWrites extends Writes[Spectra] {
    def writes(s: Spectra) = Json.obj(
      "name" -> Json.toJson(s.name),
      "specobjid" -> Json.toJson(s.specobjid),
      "ra" -> Json.toJson(s.ra),
      "dec" -> Json.toJson(s.dec),
      "plate" -> Json.toJson(s.plate),
      "mjd" -> Json.toJson(s.mjd),
      "fiber" -> Json.toJson(s.fiber),
      "id" -> Json.toJson(s.id))
  }

}