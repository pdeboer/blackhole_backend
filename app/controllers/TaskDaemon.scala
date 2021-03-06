package controllers

import models._
import play.api.Logger
import play.api.libs.json._
import play.api.mvc.{Controller, _}

// you need this import to have combinators
import play.api.libs.functional.syntax._

object TaskDaemon extends Controller {

  // The task reader
  implicit val rds = (
    (__ \ 'jwt).read[String] and
    (__ \ 'ip).read[String]) tupled

  /**
   * This method is used to deliver the next action for the user
   *
   * @return JSON with all data combined
   */
  def getNext = Action { request =>
    // First of all, make sure the request is legit and has a jwt + ip
    request.body.asJson.map { json =>
      json.validate[(String, String)].map {
        case (jwt, ip) =>
          // find the active set_id and set it as a constant
          val SET = Set.findActiveSet()

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
          var taskConstraintBusinessRule = ""

          // Control variables
          var hasSpectra = false
          var hasRadio = false
          var hasXray = false

          // Counter variables
          var numberOfSpectras = 0;
          var lastSpectra = 0
          var nextSpectra = 0;
          // Check the last entry the user mase
          lastEntry match {
            case None => coordinates = newJob(SET, uuid) // no last entry, new job
            case entry: Tasklog =>
              // Find the task by id
              Task.findById(entry.question_id).map { tConstraint =>
                taskConstraintExitOn = tConstraint.exitOn
                taskConstraintLaterTaskId = tConstraint.laterTaskId
                taskConstraintBusinessRule = tConstraint.businessRule
              }

              //hasSpectra = Coordinate.coordinateHasSpectra(entry.sdss_id)
              hasSpectra = Coordinate.coordinateHasSpectraNew(entry.sdss_id)
              hasRadio = Coordinate.coordinateHasRadio(entry.sdss_id)
              hasXray = Coordinate.coordinateHasXray(entry.sdss_id)

              if(hasSpectra) {
                numberOfSpectras = Coordinate.countSpectrasForCoordinates(entry.sdss_id)
              }

              if(hasSpectra && entry.question_id > 5) {
                lastSpectra = entry.spectra_id;
                nextSpectra = entry.spectra_id + 1;
              }

              // Business rules
              var plusFactor = 0

              /**
                * This is the businesslogic to left out certain images/shows for some occasions
                * it is a true fact that the order must be stable, otherways we would need to refactor
                * this thing. The current workflow is 3xSdss 1x xray 1x radio 2-Xx Spectra
                */
              if (taskConstraintBusinessRule.contains("check_sdss")) {
                if (entry.answer == "no") {
                  plusFactor = 1
                }
                if(!hasXray) {
                  // No xray so hop 2
                  plusFactor = plusFactor + 1
                } else if(!hasXray && !hasRadio) {
                  // No radio and xray so hop 3
                  plusFactor = plusFactor + 2
                } else if(!hasXray && !hasRadio && !hasSpectra) {
                  // Nothing at all, jump out
                  taskConstraintLaterTaskId = 0
                }
              } else if (taskConstraintBusinessRule.contains("check_xray")) {
                if (!hasXray && !hasSpectra && !hasRadio) {
                  // All false, jump out
                  taskConstraintLaterTaskId = 0
                } else if (!hasXray && hasRadio) {
                  // radio is in + 1 hopping
                  plusFactor = 1
                } else if (!hasXray && !hasRadio && hasSpectra) {
                  // spectra is in but no radio, hop 2
                  plusFactor = 2
                }
              } else if (taskConstraintBusinessRule.contains("check_radio")) {
                if (!hasSpectra && !hasRadio) {
                  // this case should never appear
                  taskConstraintLaterTaskId = 0
                } else if (hasSpectra) {
                  // has a spectra, but no radio hop one
                  plusFactor = 1
                }
              } else if (taskConstraintBusinessRule.contains("check_spectra")) {
                if (!hasSpectra) {
                  // this case should never appear
                  taskConstraintLaterTaskId = 0
                }
              } else if (taskConstraintBusinessRule.contains("check_all_spectra")) {
                if(entry.answer == "no" && entry.question_id == 6) {
                  taskConstraintLaterTaskId = 0
                }
                if (hasSpectra && (numberOfSpectras == lastSpectra)) {
                  Logger.info("full spectra")
                  taskConstraintLaterTaskId = 0
                }
              }

              // Depending on the "why" a special finished database entry will be set
              if (entry.answer == taskConstraintExitOn || taskConstraintLaterTaskId == 0) {
                if (entry.answer == taskConstraintExitOn) {
                  if (CompletedTaskLog.CheckForInsert(uuid, "halted", entry.sdss_id, SET) == false) {
                    CompletedTaskLog.insertCompletedTasklog(uuid, "halted", entry.sdss_id, SET)
                  }
                } else if (taskConstraintLaterTaskId == 0) {
                  if (CompletedTaskLog.CheckForInsert(uuid, "completed", entry.sdss_id, SET) == false) {
                    CompletedTaskLog.insertCompletedTasklog(uuid, "completed", entry.sdss_id, SET)
                  }
                }
                // Get a new job in other cases
                coordinates = newJob(SET, uuid)
              } else {
                // Choose the right job id, depending if xray, spectra... exist
                coordinates = Coordinate.findBySdssId(entry.sdss_id)
                if (questionId < 7) {
                  questionId = entry.question_id + 1 + plusFactor
                } else {
                  questionId = 7
                }
              }
          }


          // dirty hack...
          if (questionId > 7) {
            questionId = 7
          }


          // Check the coordinates
          coordinates match {
            case Some(coords) =>
              coordinatesRa = coords.ra
              coordinatesDec = coords.dec
              coordinatesSdssId = coords.sdss_id
              coordinatesId = coords.id

              // Get next question
              val nextTask = Task.findById(questionId)
Logger.info(questionId.toString)
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
              val listOfSpectras = Spectra.findByName(coordinatesSdssId.toString())

              // Statistics
              // Get number of solved tasks
              val solvedTasks = CompletedTaskLog.getNumberOfCompletedTasks(uuid)
              implicit val spectraFormat = Json.format[Spectra]

              // Find out if it is already rated for persistance
              val isRated: Boolean = Comment.findByUuidAndSdss(uuid, coordinatesSdssId)

              // Create the return json
              val returnObject = Json.toJson(
                Map(
                  "debug" -> Seq(
                    Json.toJson(
                      Map(
                        "uuid" -> Json.toJson(uuid),
                        "jwt" -> Json.toJson(jwt),
                        "hasSpectras" -> Json.toJson(hasSpectra), //debug
                        "hasRadio" -> Json.toJson(hasRadio), //debug
                        "hasXray" -> Json.toJson(hasXray), //debug
                        "lastSpectra" -> Json.toJson(lastSpectra), //debug
                        "nextSpectra" -> Json.toJson(nextSpectra), //debug
                        "nrOfSpectras" -> Json.toJson(numberOfSpectras)))), //debug
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
                        "spectras" -> Json.toJson(listOfSpectras), //obsolete?
                        "spectra_nr" -> Json.toJson(nextSpectra),
                        "nrOfSpectras" -> Json.toJson(numberOfSpectras), //debug
                        "set" -> Json.toJson(SET)))),
                        "options" -> Seq(
                    Json.toJson(
                      Map(
                        "is_rated" -> Json.toJson(isRated),
                        "user_known" -> Json.toJson(false),
                        "persona_info" -> Json.toJson(false),
                        "preset" -> Json.toJson(taskPreset),
                        "notification" -> Json.toJson(false)))),
                  "statistics" -> Seq(
                    Json.toJson(
                      Map(
                        "completed" -> Json.toJson(solvedTasks))))))

              Ok(returnObject)

            case None =>
              Ok("All tasks are done") // finished with the set

          }

      }.recoverTotal {
        e => BadRequest("Detected error:" + JsError.toFlatJson(e))
      }
    }.getOrElse {
      BadRequest("Expecting Json data")
    }
  }

  /**
   * If a new job needs to be accuired (new galaxy image)
   *
   * @param set set of the actual used set
   * @param uuid uuid of the user
    * @return Option[Coordinate] delivers a set of coordinates
   */
  def newJob(set: Int, uuid: String): Option[Coordinate] = Option[Coordinate] {
    // Get random a new coordinate found inside the set and not yet finished
    val coordinates = Coordinate.findByRandWithSetAndUuid(set, uuid)
    coordinates match {
      case Some(coords) => return coordinates
      case None => return None
    }

  }

}

