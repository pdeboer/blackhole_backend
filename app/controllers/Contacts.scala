package controllers

import models.Contact
import play.api.Logger
import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.mvc.{Controller, _}

/**
 * This Controller is used as a controller for the contact form
 *
 * @author David Pinezich <david.pinezich@uzh.ch>
 * @version 1.0.0
 */
object Contacts extends Controller {

  /**
   * Json write object for contact purposes
   */
  implicit object contactWrites extends Writes[Contact] {
    def writes(c: Contact) = Json.obj(
      "name" -> Json.toJson(c.name),
      "email" -> Json.toJson(c.email),
      "phone" -> Json.toJson(c.phone),
      "message" -> Json.toJson(c.message),
      "uuid" -> Json.toJson(c.uuid))
  }

  /**
   * Parses the json object for contact
   */
  implicit val contactReads: Reads[Contact] = (
    (JsPath \ "name").read[String] and
    (JsPath \ "email").read[String] and
    (JsPath \ "phone").read[String] and
    (JsPath \ "message").read[String] and
    (JsPath \ "uuid").read[String])(Contact.apply _)

  /**
   * This method saves the actual given contact
   *
   * @return Int id of the insertion
   */
  def save() = Action(parse.json) { request =>
    //Logger.info("start")
    try {
      val contactJson = request.body
      val contact = contactJson.as[Contact]

      val id = Contact.insertContact(contact.name, contact.email, contact.phone, contact.message, contact.uuid)
      Ok(id.get.toString())
    } catch {
      case e: IllegalArgumentException => BadRequest("Contact failed")
      case e: Exception =>
        Logger.info("exception = %s" format e)
        BadRequest("Invalid contact configuration")
    }
  }

}
