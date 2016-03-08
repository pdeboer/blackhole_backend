package models

import anorm.SqlParser._
import anorm._
import play.api.Play.current
import play.api.db._

/**
 * An entry in the Tasklog list
 *
 * @param id Coordinates id
 * @param sdss_id Sdss_id
 * @param total_answers Total of answers
 * @param q1_yes Total of yes for q1
 * @param q1_no Total of no for q1
 * @param q2_yes Total of yes for q2
 * @param q2_no Total of no for q2
 * @param q3_yes Total of yes for q3
 * @param q3_no Total of no for q3
 * @param q4_yes Total of yes for q4
 * @param q4_no Total of no for q4
 * @param q5_yes Total of yes for q5
 * @param q5_no Total of no for q5
 * @param q6_yes Total of yes for q6
 * @param q6_no Total of no for q6
 * @param q7_tot Total of votes for q7
 * @param q7_yes Total of yes for q7
 * @param q7_no Total of no for q7
 */
case class TasklogStatistics(id: Int, sdss_id: BigDecimal, total_answers: Int,
                             q1_yes: Int, q1_no: Int, q2_yes: Int, q2_no: Int,
                             q3_yes: Int, q3_no: Int, q4_yes: Int, q4_no: Int,
                             q5_yes: Int, q5_no: Int, q6_yes: Int, q6_no: Int,
                             q7_tot: Int, q7_yes: Int, q7_no: Int)


/**
 * This Model is used to get data from the Tasklogmodel in statistic purposes
 *
 * @author David Pinezich <david.pinezich@uzh.ch>
 * @version 1.0.0
 */
object TasklogStatistics {


  /**
    * Find best rated galaxy and their task
    *
    * @return List[ratedTasklog]
    */
  def getTasklogStatistics(): List[TasklogStatistics] = {
    DB.withConnection { implicit connection =>
      SQL("SELECT coordinates.id, coordinates.sdss_id, " +
        "IFNULL(SUM(tasklog.question_id = '1'), 0) AS total_answers," +
        "IFNULL(SUM(tasklog.question_id = '1' AND tasklog.answer = 'yes'), 0) AS q1_yes," +
        "IFNULL(SUM(tasklog.question_id = '1' AND tasklog.answer = 'no'), 0) AS q1_no," +
        "IFNULL(SUM(tasklog.question_id = '2' AND tasklog.answer = 'yes'), 0) AS q2_yes," +
        "IFNULL(SUM(tasklog.question_id = '2' AND tasklog.answer = 'no'), 0) AS q2_no," +
        "IFNULL(SUM(tasklog.question_id = '3' AND tasklog.answer = 'yes'), 0) AS q3_yes," +
        "IFNULL(SUM(tasklog.question_id = '3' AND tasklog.answer = 'no'), 0) AS q3_no," +
        "IFNULL(SUM(tasklog.question_id = '4' AND tasklog.answer = 'yes'), 0) AS q4_yes," +
        "IFNULL(SUM(tasklog.question_id = '4' AND tasklog.answer = 'no'), 0) AS q4_no," +
        "IFNULL(SUM(tasklog.question_id = '5' AND tasklog.answer = 'yes'), 0) AS q5_yes," +
        "IFNULL(SUM(tasklog.question_id = '5' AND tasklog.answer = 'no'), 0) AS q5_no," +
        "IFNULL(SUM(tasklog.question_id = '6' AND tasklog.answer = 'yes'), 0) AS q6_yes," +
        "IFNULL(SUM(tasklog.question_id = '6' AND tasklog.answer = 'no'), 0) AS q6_no, " +
        "IFNULL(SUM(tasklog.question_id = '7' AND tasklog.spectra_id = '1'), 0) AS q7_tot, " +
        "IFNULL(SUM(tasklog.question_id = '7' AND tasklog.answer = 'yes'), 0) AS q7_yes, " +
        "IFNULL(SUM(tasklog.question_id = '7' AND tasklog.answer = 'no'), 0) AS q7_no " +
        "FROM coordinates LEFT JOIN tasklog ON coordinates.sdss_id = tasklog.sdss_id " +
        "GROUP BY coordinates.id " +
        "ORDER BY coordinates.id, tasklog.question_id " +
        "LIMIT 0, 1000").as(TasklogStatistics.simpleTasklogStatistic *)
    }
  }




  /**
   * Tasklog struct
   */
  val simpleTasklogStatistic = {
    get[Int]("id") ~ // get[Option[Int]]
      get[BigDecimal]("sdss_id") ~
      get[Int]("total_answers") ~
      get[Int]("q1_yes") ~
      get[Int]("q1_no") ~
      get[Int]("q2_yes") ~
      get[Int]("q2_no") ~
      get[Int]("q3_yes") ~
      get[Int]("q3_no") ~
      get[Int]("q4_yes") ~
      get[Int]("q4_no") ~
      get[Int]("q5_yes") ~
      get[Int]("q5_no") ~
      get[Int]("q6_yes") ~
      get[Int]("q6_no") ~
      get[Int]("q7_tot") ~
      get[Int]("q7_yes") ~
      get[Int]("q7_no") map {
        case id ~ sdss_id ~ total_answers ~ q1_yes ~ q1_no ~ q2_yes ~ q2_no ~ q3_yes ~ q3_no ~ q4_yes ~ q4_no ~ q5_yes ~ q5_no ~ q6_yes ~ q6_no ~ q7_tot ~ q7_yes ~ q7_no =>
          TasklogStatistics(id, sdss_id, total_answers, q1_yes, q1_no, q2_yes, q2_no, q3_yes, q3_no, q4_yes, q4_no, q5_yes, q5_no, q6_yes, q6_no, q7_tot, q7_yes, q7_no)
      }
  }



}