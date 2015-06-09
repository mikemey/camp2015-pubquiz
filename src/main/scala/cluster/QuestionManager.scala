package cluster

import akka.actor.{Actor, ActorLogging}

class QuestionManager extends Actor with ActorLogging {

//    override def preStart(): Unit = {
//      //#subscribe
//      cluster.subscribe(self, initialStateMode = InitialStateAsEvents,
//        classOf[MemberEvent], classOf[UnreachableMember])
//      //#subscribe
//    }
    override def postStop(): Unit = cluster.unsubscribe(self)

  override def receive: Receive = {
    ???
  }
}
