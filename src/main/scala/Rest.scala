import akka.actor.{ActorSystem, Props}
import akka.io.IO
import rest.RoutedHttpService
import actors.{CiccioAkaResultsManager, QuizMessages, JulioAkaAnswerProvider}
import com.typesafe.config.ConfigFactory
import spray.can.Http

object Rest extends App {
  val config = ConfigFactory.load("application")
  implicit lazy val system = ActorSystem("ClusterSystem", config)

  val rootService = system.actorOf(Props(new RoutedHttpService()))

  IO(Http)(system) ! Http.Bind(rootService, "0.0.0.0", port = 8080)

  sys.addShutdownHook(system.terminate())
}
