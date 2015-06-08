package client

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import com.typesafe.config.ConfigFactory

object PubQuizClient {

  def main(args: Array[String]): Unit = {
    val system = ActorSystem("client", ConfigFactory.load("application"))
    val clientActor: ActorRef = system.actorOf(Props[PubQuizClient], "client")
    clientActor ! "send"
  }
}

class PubQuizClient extends Actor {
  def receive = {
    case "send" =>
      println("sending connection message")
      val maria = context.actorSelection("akka.tcp://maria@192.168.1.70:2552/user/orchestrator")
      maria ! "I put the ball in the net!!"
    case resp: String => println("received: " + resp)
  }
}
