package actors

import actors.QuizMessages._
import akka.actor.{Actor, ActorLogging}

class Ciccio extends Actor with ActorLogging {

  var results: Option[Results] = None
  var counters =  collection.mutable.Map[String, Int]()
  var questionFinished = false

  def incrementCounters(results: Seq[AnswerResult]): Unit = {
    results.foreach {
      case AnswerResult(address, _, _, true) =>
        val currentCount = counters.getOrElse(address, 0)
        counters put (address, currentCount+1)
      case _ => Unit
    }
  }

  override def receive: Receive = {

    case results: Results =>
      log.info(s"ciccio received the results: $results")
      this.results = Some(results)
      incrementCounters(results.answers)

    case PullResults =>
      respondWithResults()
      results = None

    case PullPartialResults =>
      respondWithResults()
      if (questionFinished) {
        results = None
        questionFinished = false
      }

    case ResultsComplete =>
      questionFinished = true

    case "System is started! Tell Julio!" =>
      log.info("Julio tiene que lavarse")
      context.actorSelection("/user/julio") ! "Lavate JULIO!"

    case _ =>
      log.warning("Ciccio - Unknown message")
  }

  def respondWithResults(): Unit = {
    val msg: Option[LocalResults] = results map { r =>
      val localAddress = akka.cluster.Cluster(context.system).selfAddress.toString
      val isLocalNodeWinner = r.answers.exists(answer => answer.ipAddress == localAddress && answer.isCorrect)
      LocalResults(r, isLocalNodeWinner, questionFinished, counters.toMap)
    }
    sender() ! msg
  }
}
