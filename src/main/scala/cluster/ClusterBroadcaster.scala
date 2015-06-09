package cluster

import akka.cluster.Cluster
import akka.cluster.ClusterEvent._
import akka.actor.{ActorRef, ActorLogging, Actor}

class ClusterBroadcaster extends Actor with ActorLogging {

  val cluster = Cluster(context.system)

  // subscribe to cluster changes, re-subscribe when restart 
//  override def preStart(): Unit = {
//    //#subscribe
//    cluster.subscribe(self, initialStateMode = InitialStateAsEvents,
//      classOf[MemberEvent], classOf[UnreachableMember])
//    //#subscribe
//  }
  override def postStop(): Unit = cluster.unsubscribe(self)

  def receive = {
    case BroadcastQuestion(question, choices) => ???
    case _ => log.warning(s"Unknown message")
  }
}

case class BroadcastQuestion(question: String, choices: Seq[Answer])

case class Answer(answer: String, isCorrect: Boolean = false)