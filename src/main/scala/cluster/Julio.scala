package cluster

import akka.actor._
import cluster.ClusterBroadcaster.{Answer, PullQuestion, Question}

class Julio extends Actor with ActorLogging {

  var question: Question = null

  override def receive: Receive = {

    case question: Question =>
      this.question = question

    case PullQuestion =>
      sender() ! Option(question)

    case answer: Answer => Option(question).foreach(question.respondTo ! _)

    case "Lavate JULIO!" => log.info("Tengo que lavarme tio! Hoy no, manana!")

    case _ => log.warning("Julio - Unknown message")
  }
}
