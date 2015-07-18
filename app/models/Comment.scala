package models
import play.api.db._
import play.api.Play.current
import anorm._
import anorm.SqlParser._
import scala.language.postfixOps
import play.api.Logger

import anorm._
import anorm.SqlParser._
/**
 * An entry in the Comment list
 *
 * @param sdss_id Comment sdss_id
 * @param set_id Comment set_id
 * @param rating Comment rating
 * @param comment Comment comment
 * @param ip Comment ip
 */
case class Comment(sdss_id: BigDecimal, set_id: Int, rating: Int, comment: String, ip: String)
/**
 * Task data access
 */
object Comment {

  /**
   * Find all Tasks
   *
   * @return
   */
  def findAll(): List[Comment] = {
    DB.withConnection { implicit connection =>
      SQL("select * from comments").as(Comment.simpleComment *)
    }
  }

  /**
   * Find Comment by Id
   *
   * @param id
   * @return
   */
  def findById(id: Int): Option[Comment] = {
    DB.withConnection { implicit connection =>
      SQL("select * from comments WHERE id = {id}").on('id -> id).as(Comment.simpleComment.singleOpt)
    }
  }


  /**
   * Insert Comment with rating
   *
   * @param sdss_id
   * @param set_id
   * @param rating
   * @param comment
   * @param ip
   * @return
   */
  def insertComment(sdss_id: BigDecimal, set_id: Int, rating: Int, comment: String, ip: String): Option[Long] = {
     Logger.debug(sdss_id.toString + " " + set_id.toString + " " + rating.toString + " " + comment + " " + ip)
     val id: Option[Long] = DB.withConnection { implicit connection =>
       SQL("INSERT INTO comments(`sdss_id`, `set_id`, `rating`, `comment`, `ip`) VALUES ({sdss_id}, {set_id}, {rating}, {comment}, {ip})")
         .on('sdss_id -> sdss_id)
         .on('set_id -> set_id)
         .on('rating -> rating)
         .on('comment -> comment)
         .on('ip -> ip)
         .executeInsert()
     }
    id
  }
  /**
   * Comment structure
   */
  val simpleComment = {
      get[BigDecimal]("sdss_id")~
      get[Int]("set_id") ~
      get[Int]("rating") ~
      get[String]("comment") ~
        get[String]("ip") map {
      case sdss_id~set_id~rating~comment~ip => Comment(sdss_id, set_id, rating, comment, ip)
    }
  }

}