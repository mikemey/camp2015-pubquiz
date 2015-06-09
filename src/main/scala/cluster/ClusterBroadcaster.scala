package cluster

import akka.actor.{Actor, ActorLogging}
import akka.cluster.Cluster

object ClusterBroadcaster {
  case class BroadcastQuestion(question: String, choices: Seq[Answer])

  case class Answer(answer: String, isCorrect: Boolean = false)
}

class ClusterBroadcaster extends Actor with ActorLogging {

  import ClusterBroadcaster.BroadcastQuestion

  val cluster = Cluster(context.system)

  override def postStop(): Unit = cluster.unsubscribe(self)

  def receive = {
    case BroadcastQuestion(question, choices) => ???
    case _ => log.warning(s"Unknown message")
  }
}
