import akka.actor.{ActorSystem, Props}
import akka.io.IO
import api.RoutedHttpService
import cluster.{Ciccio, ClusterBroadcaster, Julio}
import com.typesafe.config.ConfigFactory
import spray.can.Http

object Rest extends App {
  val config = ConfigFactory.load()
  implicit lazy val system = ActorSystem("ClusterSystem", config)

  private implicit val _ = system.dispatcher
  val rootService = system.actorOf(Props(new RoutedHttpService()))
  val quizBroadcaster = system.actorOf(Props[ClusterBroadcaster])
  val ciccio = system.actorOf(Props[Ciccio])
  val julio = system.actorOf(Props[Julio])

  IO(Http)(system) ! Http.Bind(rootService, "0.0.0.0", port = 8080)

  ciccio ! "System is started! Tell Julio!"
  sys.addShutdownHook(system.terminate())
}