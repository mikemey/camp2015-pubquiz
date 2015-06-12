package actors

import akka.actor._

import scala.sys.process._
class Startup() extends Actor with ActorLogging {

  override def receive: Receive = {

    case "Start!" =>
      "open http://localhost:8080" !

    case _ =>
      log.warning("Startup - Unknown message")
  }
}
