package cluster

import akka.actor._
import akka.cluster.Cluster
import cluster.ClusterBroadcaster.{DefaultQuestionExpirationInMinutes, Question}

object ClusterBroadcaster {

  val DefaultQuestionExpirationInMinutes: Int = 2

  case class BroadcastQuestion(question: String, choices: Seq[Choice])

  case class Choice(value: String, isCorrect: Boolean = false)

  case class Question(question: String, choices: Seq[String], respondTo: ActorRef)

  case class Answer(value: String)

  case object PullQuestion

}

class ClusterBroadcaster extends Actor with ActorLogging {

  import ClusterBroadcaster.BroadcastQuestion

  import scala.collection.JavaConverters._

  val cluster = Cluster(context.system)

  override def postStop(): Unit = cluster.unsubscribe(self)

  def receive = {
    case BroadcastQuestion(question, choices) =>

      val correctAnswer = choices.find(_.isCorrect).getOrElse(throw new RuntimeException("There must be a correct choice")).value
      val activeMembers = cluster.state.getMembers.asScala

      val questionManager = context.actorOf(Props(classOf[QuestionManager], correctAnswer, DefaultQuestionExpirationInMinutes,
        activeMembers), s"question-manager-${(Math.random() * 100).toInt}")

      activeMembers.foreach { member =>
        val memberRef = context.actorSelection(s"${member.address.toString}/user/question-receiver")
        memberRef ! Question(question, choices.map(_.value), questionManager)
      }
    case _ => log.warning(s"Unknown message")
  }
}
