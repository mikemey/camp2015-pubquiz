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

    case answer: Answer =>
      Option(question).map(question.respondTo ! _)
  }
}
