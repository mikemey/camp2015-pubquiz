package api

import akka.actor.ActorRef
import cluster.ClusterBroadcaster
import spray.routing.Directives

import scala.concurrent.ExecutionContext

class PubQuizResource(clusterBroadcaster: ActorRef)(implicit executionContext: ExecutionContext)
  extends Directives with DefaultJsonFormats {

  import ClusterBroadcaster._

  implicit val answerFormat = jsonFormat2(Choice)
  implicit val questionFormat = jsonFormat2(BroadcastQuestion)

  val route =
    path("question") {
      post {
        handleWith {
          question: BroadcastQuestion =>
            println("received question: " + question)
            clusterBroadcaster ! question
            "{ 'result': 'Question posted.'}"
        }
      }
    }

}