package cluster

import akka.actor.{Actor, ActorLogging}
import akka.cluster.ClusterEvent._
import akka.cluster.{Cluster, Member}
import cluster.ClusterBroadcaster.{Answer, Results}
import spray.http.DateTime

class QuestionManager(question: String, correctAnswer: String, expiresInMinutes: Int, var participants: Seq[Member]) extends Actor with ActorLogging {
  val cluster = Cluster(context.system)
  var recordedAnswers: collection.mutable.Map[String, Boolean] = collection.mutable.Map()
  val quizStartTime = DateTime.now

  override def preStart(): Unit = {
    cluster.subscribe(self, initialStateMode = InitialStateAsEvents,
      classOf[MemberEvent], classOf[UnreachableMember])
  }

  override def postStop(): Unit = cluster.unsubscribe(self)

  override def receive: Receive = {

    case UnreachableMember(member) =>
      log.info("Member detected as unreachable: {}", member)
      participants = participants.filterNot(_ == member)
      finishIfGameIsOver()

    case MemberRemoved(member, _) =>
      log.info("Member detected as removed: {}", member)
      participants = participants.filterNot(_ == member)
      finishIfGameIsOver()

    // Used to not have warnings of death letters for this event
    case MemberUp(member) =>
      log.info("Member detected as up: {}", member)

    case Answer(value) =>
      recordedAnswers + (sender.path.address.toString -> (value == correctAnswer))
      finishIfGameIsOver()

    case s => log.warning("QuestionManager - Unknown message: " + s)
  }

  // TODO: take into account when the game is over due to timeout, send message to the broadcaster to notify all ciccios
  private def finishIfGameIsOver(): Unit = {
    def timesUp = {
      val expirationInMillis = expiresInMinutes * 60000
      (quizStartTime + expirationInMillis).compareTo(DateTime.now) > 0
    }
    if (recordedAnswers.size == participants.size || timesUp) {
      // TODO: This guy needs to know where is the ciccio, or better send this to the Broadcaster
      context.actorSelection("/user/ciccio") ! Results(question, Map() ++ recordedAnswers)
      context.stop(self)
    } else Unit

  }
}
