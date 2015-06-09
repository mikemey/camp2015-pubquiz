package api

import akka.actor.ActorRef
import cluster.ClusterBroadcaster
import spray.routing.Directives

import scala.concurrent.ExecutionContext

class PubQuizResource(messenger: ActorRef)(implicit executionContext: ExecutionContext)
  extends Directives with DefaultJsonFormats {

  import ClusterBroadcaster._

  implicit val answerFormat = jsonFormat2(Choice)
  implicit val questionFormat = jsonFormat2(BroadcastQuestion)

  val route =
    path("message") {
      post {
        handleWith { sm: BroadcastQuestion => messenger ! sm; "{}" }
      }
    }

}