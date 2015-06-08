package orchestrator

import akka.actor.{ActorLogging, Actor}

class Orchestrator extends Actor with ActorLogging {

  override def receive: Receive = {
    case msg =>
      println(msg)
      sender() ! "I have received your message"
  }

}
