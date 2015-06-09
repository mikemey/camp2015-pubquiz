package cluster

import akka.actor.{Actor, ActorLogging}
import akka.cluster.ClusterEvent.{InitialStateAsEvents, MemberEvent, MemberRemoved, UnreachableMember}
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

    case Answer(value) =>
      recordedAnswers + (sender.path.address.toString -> (value == correctAnswer))
      finishIfGameIsOver()
  }


  private def finishIfGameIsOver(): Unit = {
    def timesUp = {
      val expirationInMillis = expiresInMinutes * 60000
      (quizStartTime + expirationInMillis).compareTo(DateTime.now) > 0
    }
    if (recordedAnswers.size == participants.size || timesUp) {
      context.actorSelection("/user/ciccio") ! Results(question, Map() ++ recordedAnswers)
      context.stop(self)
    } else Unit

  }
}
