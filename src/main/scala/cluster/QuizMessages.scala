package cluster

import akka.actor._
import akka.cluster.Cluster
import cluster.QuizMessages.{DefaultQuestionExpirationInMinutes, Question}
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.duration._

object QuizMessages {

  val DefaultQuestionExpirationInMinutes: Int = 2

  case class BroadcastQuestion(question: String, choices: Seq[Choice])

  case class Choice(value: String, isCorrect: Boolean = false)

  case class Question(question: String, choices: Seq[String], respondTo: ActorRef)

  case class Answer(value: String, participantName: String = "")

  case object PullQuestion

  case class AnswerResult(ipAddress: String, participantName: String, isCorrect: Boolean)

  case class Results(question: String, answers: Seq[AnswerResult])

  case class LocalResults(results: Results, localIsWinner: Boolean)

  case object PullResults

  case object QuestionTimeOut
}

class ClusterBroadcaster extends Actor with ActorLogging {

  import QuizMessages.BroadcastQuestion
  import QuizMessages.QuestionTimeOut
  import scala.collection.JavaConverters._

  val cluster = Cluster(context.system)

  def receive = {
    case BroadcastQuestion(question, choices) =>

      val correctAnswer = choices.find(_.isCorrect).getOrElse(throw new RuntimeException("There must be a correct choice")).value
      val selfAddress = cluster.selfAddress
      val activeMembers = cluster.state.getMembers.asScala.filterNot(_.address == selfAddress)

      // TODO: When it expires, the broadcaster should be watching this guy and notify Julio that there is not question
      val questionManager = cluster.system.actorOf(Props(classOf[QuestionManager], question, correctAnswer, DefaultQuestionExpirationInMinutes,
        activeMembers), s"question-manager-${(Math.random() * 100).toInt}")

      activeMembers.foreach { member =>
        val remoteJulio = cluster.system.actorSelection(s"${member.address.toString}/user/julio")
        val data = Question(question, choices.map(_.value), questionManager)
        println(s"sending question $question \nto member $remoteJulio")
        remoteJulio ! data
      }

      context.system.scheduler.scheduleOnce(DefaultQuestionExpirationInMinutes.minutes, questionManager, QuestionTimeOut)

    case _ => log.warning(s"Unknown message")
  }
}
