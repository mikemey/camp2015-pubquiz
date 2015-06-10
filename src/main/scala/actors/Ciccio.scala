package actors

import akka.actor.{Actor, ActorLogging}
import actors.QuizMessages._

class Ciccio extends Actor with ActorLogging {

  var results: Option[Results] = None

  override def receive: Receive = {

    case results: Results =>
      log.info(s"ciccio received the results: $results")
      this.results = Some(results)

    case PullResults =>
      val msg: Option[LocalResults] = results map { r =>
        val localAddress = akka.cluster.Cluster(context.system).selfAddress.toString
        val isLocalNodeWinner = r.answers.exists(answer => answer.ipAddress == localAddress && answer.isCorrect)
        LocalResults(r, isLocalNodeWinner)
      }
      sender() ! msg
      results = None

    case "System is started! Tell Julio!" =>
      log.info("Julio tiene que lavarse")
      context.actorSelection("/user/julio") ! "Lavate JULIO!"

    case _ =>
      log.warning("Ciccio - Unknown message")
  }

}
