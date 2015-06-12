import java.net._

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import com.typesafe.config.ConfigFactory
import rest.RoutedHttpService
import spray.can.Http

object Rest extends App {
  val config = ConfigFactory.load("application")
  implicit lazy val system = ActorSystem("ClusterSystem", config)

  val rootService = system.actorOf(Props(new RoutedHttpService()))

  IO(Http)(system) ! Http.Bind(rootService, InetAddress.getLocalHost.getHostAddress, port = 8080)

  sys.addShutdownHook(system.terminate())
}
