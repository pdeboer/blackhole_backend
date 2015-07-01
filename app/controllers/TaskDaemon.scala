package controllers

import models.{User, Task, Coordinate, Tasklog}
import play.api.Logger
import play.api.data._
import play.api.data.Forms._
import play.api.mvc.{Controller, _}
import play.api._
import play.api.mvc._
import play.api.libs.json._

// you need this import to have combinators
import play.api.libs.functional.syntax._





object TaskDaemon extends Controller {

  implicit val rds = (
    (__ \ 'jwt).read[String] and
      (__ \ 'ip).read[String]
    ) tupled

  def getNext= Action { request =>
    request.body.asJson.map { json =>
      json.validate[(String, String)].map{
        case (jwt, ip) => {

          // Standard values for coordinates
          var coordinatesRa: BigDecimal = null
          var coordinatesDec: BigDecimal = null
          var coorddinatesSdssId: BigDecimal = null
          var coordinatesId: Option[Int] = null

          // Standard values for tasks
          var taskId: Integer = null
          var task: String = null
          var taskTyp: String  = null
          var taskValue: Integer  = null
          var taskFormerTaskId: Integer = null
          var taskLaterTaskId: Integer = null
          var taskExitOn: String = null

          var questionId: Integer = 1



          val jwTokenDecode = User.decodeJWT(jwt)
          val email = jwTokenDecode.value("email").toString.replace("\"", "")
          val uuid = User.getUuidByEmail(email)

          // Get last entry
          val lastEntry = Tasklog.findLastLogEntry(uuid).getOrElse(None)

          // Get coordinates
          var coordinates: Option[Coordinate] = null


          lastEntry match {
            case None => coordinates = newJob()
            case entry: Tasklog => {

              if(entry.question_id > 2) { // @ToDo remove if more questions are available
                coordinates = newJob()
              } else {
                coordinates = Coordinate.findBySdssId(entry.coordinates_id)
                questionId = entry.question_id + 1
              }


            }
          }
          coordinates match {
            case Some(coords) =>
              coordinatesRa = coords.ra
              coordinatesDec = coords.dec
              coorddinatesSdssId = coords.sdss_id
              coordinatesId = coords.id
            case None => println("Gender: not specified")
          }

          // Get next question
          val nextTask = Task.findById(questionId)

          nextTask match {
            case Some(nextTask) =>
              taskId = nextTask.id
              task = nextTask.task
              taskTyp = nextTask.taskType
              taskValue = nextTask.value
              taskFormerTaskId = nextTask.formerTaskId
              taskLaterTaskId = nextTask.laterTaskId
              taskExitOn = nextTask.exitOn
            case None => println("No Task")
          }




          val returnObject = Json.toJson(
            Map(
              "return" -> Seq(
                Json.toJson(
                  Map(
                    "sdss_id" -> Json.toJson(coorddinatesSdssId.toString()),
                    "ra" -> Json.toJson(coordinatesRa.toString()),
                    "dec" -> Json.toJson(coordinatesDec.toString()),
                    "answer" -> Json.toJson(taskTyp),
                    "question" -> Json.toJson(task),
                    "question_id" -> Json.toJson(taskId.toString)
                  )
                )
              )
            )
          )



          Ok(returnObject)

        }
      }.recoverTotal{
        e => BadRequest("Detected error:"+ JsError.toFlatJson(e))
      }
    }.getOrElse {
      BadRequest("Expecting Json data")
    }
  }




  def newJob(): Option[Coordinate] =  Option[Coordinate] {
    return Coordinate.findByRandWithSet()
  }




}




