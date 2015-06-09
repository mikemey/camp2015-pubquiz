import akka.actor.{ActorSystem, Props}
import akka.io.IO
import api.RoutedHttpService
import com.typesafe.config.ConfigFactory
import spray.can.Http

object Rest extends App {
  val config = ConfigFactory.load()
  implicit lazy val system = ActorSystem("ClusterSystem", config)

  private implicit val _ = system.dispatcher
  val rootService = system.actorOf(Props(new RoutedHttpService()))

  IO(Http)(system) ! Http.Bind(rootService, "0.0.0.0", port = 8080)

  sys.addShutdownHook(system.terminate())
}