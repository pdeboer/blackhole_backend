package models

import play.api.db._
import play.api.Play.current
import anorm._
import anorm.SqlParser._
import scala.language.postfixOps

/**
 * Case class for Spectras
 *
 * @param name The name of the spectra (sdss_id)
 * @param specobjid The id of the inspection
 * @param ra Ra number in decimals
 * @param dec Dec number in decimals
 * @param fiber The fiber number (light from focal plane to individual object )
 * @param plate The Plate number (position of optical fiber)
 * @param mjd The mjd number (modified julian date)
 * @param id Unique indexing number
 */
case class Spectra(name: BigDecimal, specobjid: String, ra: BigDecimal, dec: BigDecimal, fiber: Int, plate: Int, mjd: Int, id: Int)

/**
 * This Model is used to get data from the spectra model
 *
 * @author David Pinezich <david.pinezich@uzh.ch>
 * @version 1.0.0
 */
object Spectra {

  /**
   * Method to find all Spectras
   *
   * @return List[Spectra] List of all Spectras
   */
  def findAll(): List[Spectra] = {
    DB.withConnection { implicit connection =>
      SQL("SELECT * FROM spectra").as(Spectra.simpleSpectra *)
    }
  }

  /**
   * Find a list of Spectras by theirname
   *
   * @param name The name of the Spectra database is actually the sdss_id to match it with the coordinates table
   * @return List[Spectra] Returns a list of Spectras 0-n with the given name
   */
  def findByName(name: BigDecimal): List[Spectra] = {
    DB.withConnection { implicit connection =>
      SQL("SELECT * FROM spectra WHERE name = {name}").on('name -> name).as(Spectra.simpleSpectra *)
    }
  }

  /**
   * Find a list of Spectras by their Id
   *
   * @param id The id of the Spectra
   * @return List[Spectra] Returns a list of Spectras 0-n with the given name
   */
  def findByNId(id: Int): List[Spectra] = {
    DB.withConnection { implicit connection =>
      SQL("SELECT * FROM spectra WHERE id = {id}").on('id -> id).as(Spectra.simpleSpectra *)
    }
  }

  /**
   * Spectra structure, to be used with [Spectra]
   * The specobjid is a String to make sure it gets correctly to the Rest interface (without rounding)
   */
  val simpleSpectra = {
    get[BigDecimal]("name") ~
      get[String]("specobjid") ~
      get[BigDecimal]("ra") ~
      get[BigDecimal]("dec") ~
      get[Int]("plate") ~
      get[Int]("mjd") ~
      get[Int]("fiber") ~
      get[Int]("id") map {
        case name ~ specobjid ~ ra ~ dec ~ plate ~ mjd ~ fiber ~ id => Spectra(name, specobjid, ra, dec, plate, mjd, fiber, id)
      }
  }

}
