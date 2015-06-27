package controllers

import models.Coordinate
import play.api.Logger
import play.api.data._
import play.api.cache._
import play.api.data.Forms._
import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.Logger

import sys.process._
import java.net.URL
import java.io.File
import play.api.mvc.{Controller, _}


object Coordinates extends Controller {


  /**
   * File downloader
   *
   * @param url
   * @param filename
   * @return
   */
  def fileDownloader(url: String, filename: String) = {

    // Check if its downloaded already
    val tmpFile = new File(filename).exists
    if(!tmpFile) {
      // write file
      new URL(url) #> new File(filename) !!
    }
  }

  /**
   * Download sdss files
   *
   * @return
   */
  def downloadFile(opt: String = "", size: String = "600"): Boolean = {

    val baseUrl = "http://skyservice.pha.jhu.edu/DR7/ImgCutout/getjpeg.aspx?"

    def matchTest(opt: String): String = opt match {
      case "S" => "S" // red quadrat
      case "R" => "R" // Gitter
      case "T" => "T" // Crosses
      case "I" => "I" // Inverted
      case "O" => "O" // Objected (green figures)
      case "P" => "P" // Objected (blue figures)
      case "D" => "D" // Striked (one line)
      case "F" => "F" // Striked (two lines)
      case "G" => "G" // Crosshair
      case "L" => "L" // With description
      case "B" => "B" // purple quadrats
      case _ => ""
    }


    val option = "&opt=" + opt
    val pictureSize = "&width="+ size +"&height="+ size

    val zoomLevel = Map(
      1 -> 50.704256,
      2 -> 25.352128,
      3 -> 12.676064,
      4 -> 6.338032,
      5 -> 3.169016,
      6 -> 1.584508,
      7 -> 0.792254,
      8 -> 0.396127,
      9 -> 0.1980635,
      10 -> 0.09903175,
      11 -> 0.049515875,
      12 -> 0.0247579375,
      13 -> 0.015)

    val coords = Coordinate.findAll()
    // not done by par because of server-spamming
    coords.par.foreach {coord =>
      zoomLevel.par.foreach {keyVal => fileDownloader(baseUrl + "ra=" + coord.ra + "&dec=" + coord.dec + "&scale=" + keyVal._2 + option + pictureSize, "public/images/sdss/" + keyVal._1 + "/" + coord.sdss_id.toString() +".png")}
    }
    true
  }


  // Coordinates Form
  val coordinatesForm = Form(
    mapping(
      "id" -> optional(number),
      "sdss_id" -> bigDecimal,
      "ra" -> bigDecimal,
      "dec" -> bigDecimal,
      "active" -> number
    )(Coordinate.apply)(Coordinate.unapply)
  )

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
          "success" -> "User was succesfully inserted"
        ))
        Ok(views.html.coordinatelist.render(Coordinate.findAll(), flash))
      })
    }


  /**
   * Delete a pair of coordinates
   *
   * @param Id Int
   * @return void
   */
  def deleteCoordinate(Id: Int) = Action { implicit request =>
    val result = Coordinate.deleteCoordinate(Id)
    val flash = play.api.mvc.Flash(Map(
      "success" -> "Coordinates successfully deleted"
    ))
    Ok(views.html.coordinatelist.render(Coordinate.findAll(), flash))
  }

  // Json part

  /**
   * List all Coordinates
   *
   * @return void
   */
  def list = Action {
      val allCoordinates = Coordinate.findAll().map(_.id)
      Ok(Json.toJson(allCoordinates))
  }

  /**
   * Json Write container
   *
   */
  implicit object CoordinateWrites extends Writes[Coordinate] {
    def writes(c: Coordinate) = Json.obj(
      "id" -> Json.toJson(c.id),
      "sdss_id" -> Json.toJson(c.id),
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
   * Get coordinates
   *
   * @return
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
      (JsPath \ "active").read[Int]
    )(Coordinate.apply _)

}