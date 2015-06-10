package cluster

import akka.actor.{Actor, ActorLogging}
import cluster.QuizMessages._


class Ciccio extends Actor with ActorLogging {

  var results: Option[Results] = None

  override def receive: Receive = {

    case results: Results =>
      this.results = Some(results)

    case PullResults =>
      val msg: Option[LocalResults] = results map { r =>
        val localAddress = context.self.path.address.toString
        val isLocalNodeWinner = r.answers.getOrElse(localAddress, false)
        LocalResults(r, isLocalNodeWinner)
      }
      sender() ! msg
      results = None

    case "System is started! Tell Julio!" =>
      log.info("Julio tiene que lavarse")
      context.actorSelection("/user/julio") ! "Lavate JULIO!"

    case _ => log.warning("Ciccio - Unknown message")
  }

}
