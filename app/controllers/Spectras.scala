package controllers

import controllers.Application._
import models.Spectra
import play.api.Logger
import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.mvc.{Controller, _}
import play.api.data._
import play.api.data.Forms._


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
   * @param name Int
   * @return void
   */
  def listByName(name: BigDecimal) = Action {
    val allSpectras = Spectra.findByName(name).map(_.specobjid)
/*
    val returnArray = scala.collection.mutable.Map[Int, String]()
    val i = 0
    allSpectras.foreach { spec =>
      returnArray(i) = spec.toString()
    }
    */
    Ok(Json.toJson(allSpectras(0).toString()))
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
    "id" -> Json.toJson(s.id)
    )
  }



}