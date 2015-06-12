package actors

import java.net.InetAddress

import akka.actor._

import scala.sys.process._
class Startup() extends Actor with ActorLogging {

  override def receive: Receive = {

    case "Start!" =>
      s"open http://${InetAddress.getLocalHost.getHostAddress}:8080" !

    case _ =>
      log.warning("Startup - Unknown message")
  }
}
