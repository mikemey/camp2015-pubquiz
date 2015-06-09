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

  def questionFromFields
  (question: String, answerA: String, answerB: String, answerC: String, answerD: String, correct: String) = {
    val choices = Seq(
      Choice(answerA, "A" == correct),
      Choice(answerB, "B" == correct),
      Choice(answerC, "C" == correct),
      Choice(answerD, "D" == correct))

    BroadcastQuestion(question, choices)
  }

  val route =
    path("question") {
      get {
        respondWithMediaType(`text/html`) {
          getFromFile("src/main/resources/html/index.html")
        }
      } ~
        post {
          formFields('question, 'answerA, 'answerB, 'answerC, 'answerD, 'correct) {
            (question, answerA, answerB, answerC, answerD, correct) =>
              val broadcastQuestion = questionFromFields(question, answerA, answerB, answerC, answerD, correct)
              clusterBroadcaster ! broadcastQuestion
              complete("{ 'result': 'ok' }")
          }
        }
    }
}