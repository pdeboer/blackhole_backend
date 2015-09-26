package models
import play.api.db._
import play.api.Play.current
import scala.language.postfixOps

import anorm._
import anorm.SqlParser._
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
 * Task data access
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