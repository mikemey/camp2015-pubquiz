package orchestrator

import akka.actor.{Props, ActorSystem}

object OrchestratorApp extends App{

  override def main(args: Array[String]) = {

    implicit val system = ActorSystem("maria")

    val orchestrator = system.actorOf(Props(classOf[Orchestrator]), "orchestrator")

  }


}
