package controllers

import models._
import play.api.mvc.{ Controller, _ }
import play.api.mvc._
import play.api.libs.json._
import play.api.Logger

// you need this import to have combinators
import play.api.libs.functional.syntax._

object TaskDaemon extends Controller {

  implicit val rds = (
    (__ \ 'jwt).read[String] and
    (__ \ 'ip).read[String]) tupled

  def getNext = Action { request =>
    request.body.asJson.map { json =>
      json.validate[(String, String)].map {
        case (jwt, ip) => {

          val SET = 1

          // Standard values for coordinates
          var coordinatesRa: BigDecimal = null
          var coordinatesDec: BigDecimal = null
          var coordinatesSdssId: BigDecimal = null
          var coordinatesId: Option[Int] = null

          // Standard values for tasks
          var taskId: Integer = null
          var task: String = null
          var taskTyp: String = null
          var taskValue: Integer = null
          var taskFormerTaskId: Integer = null
          var taskLaterTaskId: Integer = null
          var taskExitOn: String = null
          var taskComment: String = null
          var taskPreset: String = null

          // Standard for questions
          var questionId: Integer = 1

          // Get uuid by jwt
          val jwTokenDecode = User.decodeJWT(jwt)
          val email = jwTokenDecode.value("email").toString.replace("\"", "")
          val uuid = User.getUuidByEmail(email)

          // Get last entry
          val lastEntry = Tasklog.findLastLogEntry(uuid).getOrElse(None)

          // Get coordinates
          var coordinates: Option[Coordinate] = null

          // Task constraint for the last task
          var taskConstraint: Option[Task] = null
          var taskConstraintExitOn = ""
          var taskConstraintLaterTaskId = 0

          lastEntry match {
            case None => coordinates = newJob(SET, uuid)
            case entry: Tasklog => {

              Task.findById(entry.question_id).map { tConstraint =>
                taskConstraintExitOn = tConstraint.exitOn
                taskConstraintLaterTaskId = tConstraint.laterTaskId
              }

              if (entry.answer == taskConstraintExitOn || taskConstraintLaterTaskId == 0) {
                if (entry.answer == taskConstraintExitOn) {
                  CompletedTaskLog.insertCompletedTasklog(uuid, "halted", entry.sdss_id, 1)
                } else if (taskConstraintLaterTaskId == 0) {
                  CompletedTaskLog.insertCompletedTasklog(uuid, "completed", entry.sdss_id, 1)
                }

                coordinates = newJob(SET, uuid)
              } else if (entry.answer == "no" && taskConstraintExitOn == "halt") {
                coordinates = Coordinate.findBySdssId(entry.sdss_id)
                questionId = entry.question_id + 2
              } else {
                //Logger.debug("old job cont")
                coordinates = Coordinate.findBySdssId(entry.sdss_id)
                questionId = entry.question_id + 1
              }

            }
          }

          coordinates match {
            case Some(coords) =>
              coordinatesRa = coords.ra
              coordinatesDec = coords.dec
              coordinatesSdssId = coords.sdss_id
              coordinatesId = coords.id

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
                  taskComment = nextTask.comment
                  taskPreset = nextTask.preset
                case None => println("No Task")
              }

              // Spectras
              val listOfSpectras = Spectra.findByName(coordinatesSdssId)

              // Statistics
              // Get number of solved tasks
              val solvedTasks = CompletedTaskLog.getNumberOfCompletedTasks(uuid)
              implicit val spectraFormat = Json.format[Spectra]

              val isRated: Boolean = Comment.findByUuidAndSdss(uuid, coordinatesSdssId)
              Logger.debug(uuid.toString() + " :: " + coordinatesSdssId.toString())

              val returnObject = Json.toJson(
                Map(
                  "return" -> Seq(
                    Json.toJson(
                      Map(
                        "sdss_id" -> Json.toJson(coordinatesSdssId.toString()),
                        "ra" -> Json.toJson(coordinatesRa.toString()),
                        "dec" -> Json.toJson(coordinatesDec.toString()),
                        "answer" -> Json.toJson(taskTyp),
                        "question" -> Json.toJson(task),
                        "question_id" -> Json.toJson(taskId.toString),
                        "tooltip" -> Json.toJson(taskComment),
                        "spectras" -> Json.toJson(listOfSpectras)))),
                  "options" -> Seq(
                    Json.toJson(
                      Map(
                        "is_rated" -> Json.toJson(isRated),
                        "user_known" -> Json.toJson(false),
                        "persona_info" -> Json.toJson(false),
                        "preset" -> Json.toJson(false),
                        "notification" -> Json.toJson(false)))),
                  "statistics" -> Seq(
                    Json.toJson(
                      Map(
                        "completed" -> Json.toJson(solvedTasks))))))

              Ok(returnObject)

            case None =>
              Ok("All tasks are done")

          }
        }

      }.recoverTotal {
        e => BadRequest("Detected error:" + JsError.toFlatJson(e))
      }
    }.getOrElse {
      BadRequest("Expecting Json data")
    }
  }

  def newJob(set: Int, uuid: String): Option[Coordinate] = Option[Coordinate] {

    val coordinates = Coordinate.findByRandWithSetAndUuid(set, uuid)
    coordinates match {
      case Some(coords) => return coordinates
      case None => return None
    }

  }

}

