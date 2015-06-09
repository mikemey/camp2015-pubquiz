package cluster

import akka.actor.{Actor, ActorLogging, ActorRef}
import akka.cluster.ClusterEvent.{InitialStateAsEvents, MemberEvent, MemberRemoved, UnreachableMember}
import akka.cluster.{Cluster, Member}
import cluster.ClusterBroadcaster.Answer
import spray.http.DateTime

class QuestionManager(correctAnswer: String, expiresInMinutes: Int, var participants: Seq[Member]) extends Actor with ActorLogging {
  val cluster = Cluster(context.system)
  var recordedAnswers: collection.mutable.Map[ActorRef, Boolean] = collection.mutable.Map()
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
      recordedAnswers + (sender -> (value == correctAnswer))
      finishIfGameIsOver()
  }


  private def finishIfGameIsOver(): Unit = {
    def timesUp = {
      val expirationInMillis = expiresInMinutes * 60000
      (quizStartTime + expirationInMillis).compareTo(DateTime.now) > 0
    }
    if (recordedAnswers.size == participants.size || timesUp) {
      //send message with results to UI
      Unit
    } else Unit

  }
}
