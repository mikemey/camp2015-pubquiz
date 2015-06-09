package cluster

import akka.actor._
import akka.cluster.Cluster
import cluster.ClusterBroadcaster.Question

object ClusterBroadcaster {

  case class BroadcastQuestion(question: String, choices: Seq[Choice])

  case class Choice(value: String, isCorrect: Boolean = false)

  case class Question(question: String, choices: Seq[String], respondTo: ActorRef)

}


class ClusterBroadcaster extends Actor with ActorLogging {

  import ClusterBroadcaster.BroadcastQuestion

  import scala.collection.JavaConverters._

  val cluster = Cluster(context.system)

  override def postStop(): Unit = cluster.unsubscribe(self)

  def receive = {
    case BroadcastQuestion(question, choices) =>
      val questionManager = context.actorOf(Props[QuestionManager], s"question-manager-${(Math.random() * 100).toInt}")
      cluster.state.getMembers.asScala.foreach { member =>
        val memberRef = context.actorSelection(s"${member.address.toString}/user/question-receiver")
        memberRef ! Question(question, choices.map(_.value), questionManager)
      }
    case _ => log.warning(s"Unknown message")
  }
}
