package controllers

import models.Coordinate
import play.api.data.Forms._
import play.api.data._
import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.mvc.{Controller, _}

/**
 * This Controller is used as a controller for the contact form
 *
 * @author David Pinezich <david.pinezich@uzh.ch>
 * @version 1.0.0
 */
object Coordinates extends Controller {

  // Coordinates Form
  val coordinatesForm = Form(
    mapping(
      "id" -> optional(number),
      "sdss_id" -> bigDecimal,
      "ra" -> bigDecimal,
      "dec" -> bigDecimal,
      "active" -> number)(Coordinate.apply)(Coordinate.unapply))

  /**
   * Insert a pair of Coordinates
   *
   * @return void
   */
  def insertCoordinate = Action { implicit request =>
    val flash = play.api.mvc.Flash(Map("error" -> "Coordinates were not inserted"))
    coordinatesForm.bindFromRequest().fold(
      formWithErrors => BadRequest(views.html.coordinatelist.render(Coordinate.findAll(), flash)),
      tempCoordinates => {
        val Id = Coordinate.insertCoordinate(tempCoordinates.sdss_id, tempCoordinates.ra, tempCoordinates.dec)
        val flash = play.api.mvc.Flash(Map(
          "success" -> "User was succesfully inserted"))
        Ok(views.html.coordinatelist.render(Coordinate.findAll(), flash))
      })
  }

  /**
   * Delete a pair of coordinates
   *
   * @param Id Int id of coordinate to delet
   * @return void The rendered coordinate list
   */
  def deleteCoordinate(Id: Int) = Action { implicit request =>
    val result = Coordinate.deleteCoordinate(Id)
    val flash = play.api.mvc.Flash(Map(
      "success" -> "Coordinates successfully deleted"))
    Ok(views.html.coordinatelist.render(Coordinate.findAll(), flash))
  }

  /**
   * List all Coordinates
   *
   * @return void Shows all coordinates
   */
  def list = Action {
    val allCoordinates = Coordinate.findAll().map(_.id)
    Ok(Json.toJson(allCoordinates))
  }

  /**
   * Json Write container for coordinates
   *
   */
  implicit object CoordinateWrites extends Writes[Coordinate] {
    def writes(c: Coordinate) = Json.obj(
      "id" -> Json.toJson(c.id),
      "sdss_id" -> Json.toJson(c.id),
      "ra" -> Json.toJson(c.ra),
      "dec" -> Json.toJson(c.dec),
      "active" -> Json.toJson(c.active))
  }

  /**
   * Return Json view of detail coordinates
   *
   * @param id Int id of the coordinates
   * @return Coordinates Json
   */
  def detailCoordinate(id: Int) = Action {
    Coordinate.findById(id).map { coordinate =>
      Ok(Json.toJson(coordinate))
    }.getOrElse(NotFound)
  }

  /**
   * Get coordinates
   *
   * @return find a random coordinate
   */
  def getCoordinates() = Action {
    Coordinate.findByRand().map { coordinate =>
      Ok(Json.toJson(coordinate))
    }.getOrElse(NotFound)
  }

  /**
   * Parses a JSON object of Coordinates
   */
  implicit val userReads: Reads[Coordinate] = (
    (JsPath \ "id").read[Option[Int]] and
    (JsPath \ "sdss_id").read[BigDecimal] and
    (JsPath \ "ra").read[BigDecimal] and
    (JsPath \ "dec").read[BigDecimal] and
    (JsPath \ "active").read[Int])(Coordinate.apply _)

}