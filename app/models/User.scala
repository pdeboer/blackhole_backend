package models
import anorm.SqlParser._
import anorm._
import io.really.jwt._
import play.api.Play.current
import play.api.db._
import play.api.libs.json.Json

import scala.language.postfixOps
/**
 * An entry in the product catalogue.
 *
 * @param email User The email of the user
 * @param firstname User The firstname of the user
 * @param lastname User The lastname of the User
 * @param roleId User The roleId of the User
 * @param active User Is the User active or not
 * @param password User The password of the user
 * @param uuid User The identification Uuid of the user
 */
case class User(email: String, firstname: String, lastname: String, roleId: Int, active: Int, password: String, uuid: String)


case class UserRegister(email: String, firstname: String, lastname: String, password: String)


/**
 * This Model is used to get data from the Usermodel
 *
 * @author David Pinezich <david.pinezich@uzh.ch>
 * @version 1.0.0
 */
object User {

  /**
   * Secret for the jwt authentification
   */
  val secret = "pplibdatanalyzerSec2015"

  /**
   * Find all Users
   *
   * @return List[User] Lists all users
   */
  def findAll(): List[User] = {
    DB.withConnection { implicit connection =>
      SQL("SELECT * FROM users").as(User.simpleUser *)
    }
  }

  /**
   * Find all Users by email
   *
   * @param email The email of the user
   * @return Option[User] finds zero or one user by email
   */
  def findByEmail(email: String): Option[User] = {
    DB.withConnection { implicit connection =>
      SQL("SELECT * FROM users WHERE email = {email}").on('email -> email).as(User.simpleUser.singleOpt)
    }
  }

  /**
   * Find all Users by email and password
   *
   * @param email The email adress of the user
   * @param password The password of the user
   * @return Option[User] finds zeros or one user by password and email
   */
  def findByEmailAndPassword(email: String, password: String): Option[User] = {
    DB.withConnection { implicit connection =>
      SQL("SELECT * FROM users WHERE email = {email} AND password = {password} AND active = 1")
        .on('email -> email)
        .on('password -> password)
        .as(User.simpleUser.singleOpt)
    }
  }

  /**
   * Get the uuid by email and password
   *
   * @param email The email adress of the user
   * @param password The password of the user
   * @return String The uuid of the user
   */
  def getUuid(email: String, password: String): String = {
    DB.withConnection { implicit connection =>
      val rowOption = SQL("SELECT uuid as uuid FROM users WHERE email = {email} AND password = {password} AND active = 1 LIMIT 1")
        .on('email -> email)
        .on('password -> password)
        .apply
        .headOption
      rowOption.map(row => row[String]("uuid")).getOrElse("")
    }
  }

  /**
   * Get the uuid by email, and only by email
   *
   * @param email The email adress of the user
   * @return String The uuid of the user
   */
  def getUuidByEmail(email: String): String = {
    DB.withConnection { implicit connection =>
      val rowOption = SQL("SELECT uuid as uuid FROM users WHERE email = {email} AND active = 1 LIMIT 1")
        .on('email -> email)
        .apply
        .headOption
      rowOption.map(row => row[String]("uuid")).getOrElse("")
    }
  }

  /**
   * Gets the uuid in normal string form, if it is presented as encrypted jwt string
   *
   * @param uuidEncrypted The uuid in encrypted form
   * @return String The uuid of the user
   */
  def getUuidByEncryptedString(uuidEncrypted: String): String = {
    val jwTokenDecode = User.decodeJWT(uuidEncrypted)
    val email = jwTokenDecode.value("email").toString.replace("\"", "")
    User.getUuidByEmail(email)
  }

  /**
   * Get the JW Token
   *
   * @param email The email adress of the user
   * @param password The password of the user
   * @return String the jwToken of the user (encrypted uuid)
   */
  def getJWT(email: String, password: String): String = {
    // @ToDo do timestamp automatic + 1month
    val checkUser = findByEmailAndPassword(email, password)
    checkUser match {
      case None => ""
      case user: Option[User] =>
        val payload = Json.obj("email" -> email, "password" -> password, "data" -> 1466899053)
        val jwToken = JWT.encode(secret, payload)
        jwToken
    }
  }

  /**
   * Decode the jwtToken
   *
   * @param token String the given token
   * @return String The decoded JwToken
   */
  def decodeJWT(token: String): play.api.libs.json.JsObject = {
    val jwTokenDecoded = JWT.decode(token, Some(secret)).asInstanceOf[JWTResult.JWT].payload
    jwTokenDecoded
  }

  /**
   * Insert an User
   *
   * @param email The email of the user
   * @param lastname The lastname of the user
   * @param firstname The firstname of the user
   * @param roleId The roleId of the user
   * @param password The password of the user
   *
   * @return Gives back a 1 if the user is inserted successfully
   */
  def insertUser(email: String, lastname: String, firstname: String, roleId: Int, password: String): Int = {
    val uuid = java.util.UUID.randomUUID.toString //@ToDo test for double random uuids
    DB.withConnection({ implicit connection =>
      SQL("INSERT INTO users(`email`, `lastname`, `firstname`, `roleId`, `password`, `uuid`) VALUES ({email}, {lastname}, {firstname}, {roleId}, {password}, {uuid})").on('email -> email, 'lastname -> lastname, 'firstname -> firstname, 'roleId -> roleId, 'password -> password, 'uuid -> uuid).executeInsert()
    })
    1
  }

  /**
   * Delete an User by email
   *
   * @param email The email adress of the user
   * @return Int If the deletion is a success, we get back a 1 otherwise a 0
   */
  def deleteUser(email: String): Int = {
    DB.withConnection({ implicit connection =>
      val result = SQL("DELETE FROM users WHERE `email` = {email}").on('email -> email).executeUpdate()
    })
    1
  }

  /**
   * Change the active state of an user by his email adress
   *
   * @param email The email adress of the user
   * @return Int If the deletion is a success, we get back a 1 otherwise a 0
   */
  def changeActiveUser(email: String): Int = {
    DB.withConnection({ implicit connection =>
      val checkState = SQL("SELECT active as active FROM users WHERE `email` = {email}").on('email -> email).apply().head
      val oldState = checkState[Int]("active")
      var newState = 1
      if (oldState == 1) {
        newState = 0
      }
      val result = SQL("UPDATE users SET `active` = {active} WHERE `email` = {email}")
        .on('active -> newState)
        .on('email -> email)
        .executeUpdate()
    })
    1
  }

  /**
   * Struct of User
   */
  val simpleUser = {
    get[String]("email") ~
      get[String]("firstname") ~
      get[String]("lastname") ~
      get[Int]("roleId") ~
      get[Int]("active") ~
      get[String]("password") ~
      get[String]("uuid") map {
        case email ~ firstname ~ lastname ~ roleId ~ active ~ password ~ uuid => User(email, firstname, lastname, roleId, active, password, uuid)
      }
  }

}