package models
import anorm.SqlParser._
import anorm._
import play.api.Play.current
import play.api.db._

import scala.language.postfixOps

/**
 * An entry in the Comment list
 *
 * @param sdss_id Comment sdss_id
 * @param uuid The users unique id
 * @param set_id Comment set_id
 * @param rating Comment rating
 * @param comment Comment comment
 * @param ip Comment ip
 */
case class Comment(sdss_id: BigDecimal, uuid: String, set_id: Int, rating: Int, comment: String, ip: String)

/**
 * This Model is used to get data from the Commentmodel
 *
 * @author David Pinezich <david.pinezich@uzh.ch>
 * @version 1.0.0
 */
object Comment {

  /**
   * Find all Tasks
   *
   * @return List[Comment] List of all comments
   */
  def findAll(): List[Comment] = {
    DB.withConnection { implicit connection =>
      SQL("SELECT * FROM comments").as(Comment.simpleComment *)
    }
  }

  /**
   * Find Comment by Id
   *
   * @param id Id of the comment
   * @return Option[Comment] zero or one comment to be found by id
   */
  def findById(id: Int): Option[Comment] = {
    DB.withConnection { implicit connection =>
      SQL("SELECT * FROM comments WHERE id = {id}").on('id -> id).as(Comment.simpleComment.singleOpt)
    }
  }

  /**
   * Gives information if there is already a comment given
   *
   * @param uuid The uuid of the user
   * @param sdss_id The sdss_id of the Galaxy
   */
  def findByUuidAndSdss(uuid: String, sdss_id: BigDecimal): Boolean = {
    DB.withConnection { implicit connection =>
      val rowOption = SQL("SELECT id FROM comments WHERE uuid = {uuid} AND sdss_id = {sdss_id}")
        .on('uuid -> uuid)
        .on('sdss_id -> sdss_id)
        .apply
        .headOption
      rowOption.map(row => true).getOrElse(false)
    }
  }

  /**
   * Insert Comment with rating
   *
   * @param sdss_id the sdss_id
   * @param uuid The unique user id
   * @param set_id The given set id
   * @param rating The given rating
   * @param comment Optional comment
   * @param ip Tracked ip
   * @return The id of the insertion if everything is ok
   */
  def insertComment(sdss_id: BigDecimal, uuid: String, set_id: Int, rating: Int, comment: String, ip: String): Option[Long] = {
    val id: Option[Long] = DB.withConnection { implicit connection =>
      SQL("INSERT INTO comments(`sdss_id`, `uuid`, `set_id`, `rating`, `comment`, `ip`) VALUES ({sdss_id}, {uuid}, {set_id}, {rating}, {comment}, {ip})")
        .on('sdss_id -> sdss_id)
        .on('uuid -> uuid)
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
    get[BigDecimal]("sdss_id") ~
      get[String]("uuid") ~
      get[Int]("set_id") ~
      get[Int]("rating") ~
      get[String]("comment") ~
      get[String]("ip") map {
        case sdss_id ~ uuid ~ set_id ~ rating ~ comment ~ ip => Comment(sdss_id, uuid, set_id, rating, comment, ip)
      }
  }

}