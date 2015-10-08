package controllers

import java.io._
import java.net.URL

import models.Coordinate
import play.api.Play
import play.api.mvc.Controller

import scala.sys.process._

/**
 * This Controller is used as a central Controller for the obsolete image download procedure
 *
 * @author David Pinezich <david.pinezich@uzh.ch>
 * @version 1.0.0
 */
object ImageDownloader extends Controller {

  /**
   * File downloader (obsolete since imageDownloader)
   *
   * @param url The url of the file
   * @param filename The filename
   *
   * @return returns nothing, file gets written
   */
  def fileDownloader(url: String, filename: String) = {

    // Check if its downloaded already
    val tmpFile = new File(filename).exists
    if (!tmpFile) {
      // write file
      new URL(url) #> new File(filename) !!
    }
  }

  /**
   * Download the sdss file (image) (This function is obsolete thanks to imageDownloader)
   *
   * @param opt The sdss options
   * @param size The size of the image
   * @param limit The limit of coordinates to be worked on
   * @param offset The offset (startpoint) to be worked on
   *
   * @return True if the file gets downloaded successfully
   */
  def downloadFile(opt: String = "", size: String = "600", limit: Int = 1000, offset: Int = 0): Boolean = {

    val baseUrl = "http://skyservice.pha.jhu.edu/DR7/ImgCutout/getjpeg.aspx?"
    val mode = "downloader" // file

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

    // predefined subfolder
    var subFolder = ""

    // Change subfolder
    if (opt == "I") {
      subFolder = "inverted/"
    } else if (opt == "small") {
      subFolder = "small/"
    } else if (opt == "S") {
      subFolder = "point/"
    }

    // option + picture size preset
    val option = "&opt=" + opt
    val pictureSize = "&width=" + size + "&height=" + size

    // different zoomlevels as is from sdss homepage (dr7)
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

    //Logger.debug("public/images/sdss/" + subFolder + "1/23" +".png")
    //Logger.debug(baseUrl + "ra=23&dec=23&scale=26" + option + pictureSize)

    val coords = Coordinate.findSome(limit, offset)
    val imageDirectory = Play.current.configuration.getString("image.directory").get
    if (mode == "downloader") {
      try {
        coords.par.foreach { coord =>

          zoomLevel.par.foreach { keyVal =>
            fileDownloader(baseUrl + "ra=" + coord.ra + "&dec=" + coord.dec + "&scale=" + keyVal._2 + option + pictureSize, imageDirectory + keyVal._1.toString + "/" + subFolder + coord.sdss_id.toString() + ".png")
          }
        }
      } catch {
        case e: Exception => "Image currently not found"
      }
    } else if (mode == "file") {

      val writer = new PrintWriter(new File("public/coords_" + subFolder.substring(0, subFolder.length - 1) + ".txt"))

      coords.foreach { coord =>
        zoomLevel.foreach { keyVal => writer.write(baseUrl + "ra=" + coord.ra + "&dec=" + coord.dec + "&scale=" + keyVal._2 + option + pictureSize + "\n") }
      }
      writer.close()
    }
    true
  }

}