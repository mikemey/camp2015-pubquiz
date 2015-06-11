package actors

import akka.actor.ActorRef
import spray.json.DefaultJsonProtocol._

object QuizMessages {

  val DefaultQuestionExpirationInMinutes: Int = 1

  case class BroadcastQuestion(question: String, choices: Seq[Choice])

  case class Choice(value: String, isCorrect: Boolean = false)

  case class Question(question: String, choices: Seq[String], respondTo: ActorRef)

  case class Answer(value: String, participantName: String = "")

  case object PullQuestion

  case class AnswerResult(ipAddress: String, participantName: String, isCorrect: Boolean)

  case class Results(question: String, answers: Seq[AnswerResult])

  case class LocalResults(results: Results, localIsWinner: Boolean, questionFinished: Boolean)

  case object PullResults

  case object QuestionTimeOut

  case object Reset

  case object ResultsComplete

  implicit val answerFormat = jsonFormat2(Choice)
  implicit val broadcastQuestionFormat = jsonFormat2(BroadcastQuestion)

}
