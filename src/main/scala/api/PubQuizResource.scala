package api

import akka.actor.{ActorRef, ActorRefFactory}
import cluster.ClusterBroadcaster
import spray.http.MediaTypes._
import spray.httpx.SprayJsonSupport
import spray.httpx.marshalling.MetaMarshallers
import spray.json.DefaultJsonProtocol
import spray.routing.directives.ContentTypeResolver
import spray.routing.{Directives, RoutingSettings}

class PubQuizResource(clusterBroadcaster: ActorRef)
                     (implicit settings: RoutingSettings, resolver: ContentTypeResolver, refFactory: ActorRefFactory)
  extends Directives with DefaultJsonProtocol with SprayJsonSupport with MetaMarshallers {

  import ClusterBroadcaster._

  implicit val answerFormat = jsonFormat2(Choice)
  implicit val questionFormat = jsonFormat2(BroadcastQuestion)

  val route =
    path("question") {
      get {
        respondWithMediaType(`text/html`) {
          getFromFile("src/main/resources/html/index.html")
        }
      } ~
        post {
          handleWith {
            case question: BroadcastQuestion => {
              println("received question: " + question)
              clusterBroadcaster ! question
              "{ 'result': 'ok'}"
            }
            case any => println("request not recognized: " + any)
          }
        }
    }
}