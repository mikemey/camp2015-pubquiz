package actors

import actors.QuizMessages.{Answer, PullQuestion, Question}
import akka.actor._

class Julio(participantName: String) extends Actor with ActorLogging {

  var question: Question = null

  override def receive: Receive = {

    case question: Question =>
      this.question = question

    case PullQuestion =>
      sender() ! Option(question)

    case answer: Answer =>
      Option(question).foreach(_.respondTo ! answer.copy(participantName = participantName))
      question = null

    case "Lavate JULIO!" =>
      log.info(s"Participant name is $participantName")
      log.info("Tengo que lavarme tio! Hoy no, manana!")
      context.actorSelection("/user/startup") ! "Start!"

    case _ =>
      log.warning("Julio - Unknown message")
  }
}
