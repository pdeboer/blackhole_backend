package controllers

import models.Coordinate
import play.api.Logger
import play.api.data._
import play.api.data.Forms._
import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.mvc.{Controller, _}

import scala.text

/**
 * Controller for products HTTP interface.
 */
object Coordinates extends Controller {

  val coordinatesForm = Form(
    tuple(
      "ra" -> nonEmptyText,
      "dec" -> nonEmptyText
    )
  )


  def insertCoordinate = Action { implicit request =>
    val (ra, dec) = coordinatesForm.bindFromRequest.get
    val Id = Coordinate.insertCoordinate(ra, dec)
    Ok(views.html.coordinatelist.render(Coordinate.findAll()))
  }


  def deleteCoordinate(Id: Int) = Action { implicit request =>
    val result = Coordinate.deleteCoordinate(Id)
    Ok(views.html.coordinatelist.render(Coordinate.findAll()))
  }

  def list = Action {
    val allCoordinates = Coordinate.findAll.map(_.id)
    Ok(Json.toJson(allCoordinates))
  }
  /**
   * Formats a Product instance as JSON.
   */
  implicit object CoordinateWrites extends Writes[Coordinate] {
    def writes(c: Coordinate) = Json.obj(
      "id" -> Json.toJson(c.id),
      "ra" -> Json.toJson(c.ra),
      "dec" -> Json.toJson(c.dec),
      "active" -> Json.toJson(c.active)
    )
  }

  /**
   * Returns details of the given product.
   */
  def detailCoordinate(id: Int) = Action {
    Coordinate.findById(id).map { coordinate =>
      Ok(Json.toJson(coordinate))
    }.getOrElse(NotFound)
  }

  /**
   * Parses a JSON object
   */
  implicit val userReads: Reads[Coordinate] = (
    (JsPath \ "id").read[Int] and
      (JsPath \ "ra").read[String] and
      (JsPath \ "dec").read[String] and
      (JsPath \ "active").read[Int]
    )(Coordinate.apply _)

}