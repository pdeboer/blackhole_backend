package controllers

import models.Coordinate
import play.api.Logger
import play.api.data._
import play.api.data.Forms._
import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.mvc.{Controller, _}


object Coordinates extends Controller {

  // Coordinates Form
  val coordinatesForm = Form(
    tuple(
      "ra" -> nonEmptyText,
      "dec" -> nonEmptyText
    )
  )

  /**
   * Insert a pair of Coordinates
   *
   * @return void
   */
  def insertCoordinate = Action { implicit request =>
    val (ra, dec) = coordinatesForm.bindFromRequest.get
    val Id = Coordinate.insertCoordinate(ra, dec)
    Ok(views.html.coordinatelist.render(Coordinate.findAll()))
  }


  /**
   * Delete a pair of coordinates
   *
   * @param Id Int
   * @return void
   */
  def deleteCoordinate(Id: Int) = Action { implicit request =>
    val result = Coordinate.deleteCoordinate(Id)
    Ok(views.html.coordinatelist.render(Coordinate.findAll()))
  }

  // Json part

  /**
   * List all Coordinates
   *
   * @return void
   */
  def list = Action {
    val allCoordinates = Coordinate.findAll.map(_.id)
    Ok(Json.toJson(allCoordinates))
  }

  /**
   * Json Write container
   *
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
   * Return Json view of detail coordinates
   * @param id Int
   * @return Coordinates Json
   */
  def detailCoordinate(id: Int) = Action {
    Coordinate.findById(id).map { coordinate =>
      Ok(Json.toJson(coordinate))
    }.getOrElse(NotFound)
  }

  /**
   * Parses a JSON object of Coordinates
   * @todo write explanation
   */
  implicit val userReads: Reads[Coordinate] = (
    (JsPath \ "id").read[Int] and
      (JsPath \ "ra").read[String] and
      (JsPath \ "dec").read[String] and
      (JsPath \ "active").read[Int]
    )(Coordinate.apply _)

}