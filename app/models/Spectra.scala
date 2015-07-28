package models

import play.api.db._
import play.api.Play.current
import anorm._
import anorm.SqlParser._
import scala.language.postfixOps
import play.api.Logger

case class Spectra(name: Int, specobjid: Int, ra: BigDecimal, dec: BigDecimal, fiber: Int, plate: Int, mjd: Int, id: Int)

object Spectra {

    /**
     * Find all Spectras
     *
     * @return
     */
    def findAll(): List[Spectra] = {
      DB.withConnection { implicit connection =>
        SQL("SELECT * FROM spectra").as(Spectra.simpleSpectra *)
      }
    }

    /**
     * Find Spectra by Id
     *
     * @param name
     * @return
     */
    def findByName(name: String): List[Spectra] = {
      DB.withConnection { implicit connection =>
        SQL("select * from spectra WHERE name = {name}").on('name -> name).as(Spectra.simpleSpectra *)
      }
    }



    /**
     * Spectra structure
     */
    val simpleSpectra = {
        get[Int]("name") ~
        get[Int]("specobjid") ~
        get[BigDecimal]("ra") ~
        get[BigDecimal]("dec") ~
        get[Int]("plate") ~
        get[Int]("mjd") ~
          get[Int]("fiber") ~
      get[Int]("id") map {
        case name~specobjid~ra~dec~plate~mjd~fiber~id => Spectra(name, specobjid, ra, dec, plate, mjd, fiber, id)
      }
    }


}
