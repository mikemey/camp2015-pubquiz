package actors

import actors.QuizMessages._
import akka.actor.{Actor, ActorLogging}

class Ciccio extends Actor with ActorLogging {

  var results: Option[Results] = None
  var questionFinished = false

  override def receive: Receive = {

    case results: Results =>
      log.info(s"ciccio received the results: $results")
      this.results = Some(results)

    case PullResults =>
      respondWithResults
      results = None

    case PullPartialResults =>
      respondWithResults
      if (questionFinished) results = None

    case ResultsComplete =>
      questionFinished = true

    case "System is started! Tell Julio!" =>
      log.info("Julio tiene que lavarse")
      context.actorSelection("/user/julio") ! "Lavate JULIO!"

    case _ =>
      log.warning("Ciccio - Unknown message")
  }

  def respondWithResults: Unit = {
    val msg: Option[LocalResults] = results map { r =>
      val localAddress = akka.cluster.Cluster(context.system).selfAddress.toString
      val isLocalNodeWinner = r.answers.exists(answer => answer.ipAddress == localAddress && answer.isCorrect)
      LocalResults(r, isLocalNodeWinner, questionFinished)
    }
    sender() ! msg
  }
}
