package api

import akka.actor.{ActorRef, ActorRefFactory}
import akka.pattern.ask
import akka.util.Timeout
import cluster.ClusterBroadcaster
import spray.http.MediaTypes._
import spray.httpx.SprayJsonSupport
import spray.httpx.marshalling.MetaMarshallers
import spray.json.DefaultJsonProtocol
import spray.routing.directives.ContentTypeResolver
import spray.routing.{Directives, RoutingSettings}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

class PubQuizResource(clusterBroadcaster: ActorRef, julio: ActorRef, ciccio: ActorRef)
                     (implicit settings: RoutingSettings, resolver: ContentTypeResolver, refFactory: ActorRefFactory)
  extends Directives with DefaultJsonProtocol with SprayJsonSupport with MetaMarshallers {

  import ClusterBroadcaster._

  implicit val answerFormat = jsonFormat2(Choice)
  implicit val broadcastQuestionFormat = jsonFormat2(BroadcastQuestion)
  implicit val timeout = Timeout(5 seconds)

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
    path("app.js") {
      get {
        respondWithMediaType(`application/javascript`) {
          getFromFile("src/main/resources/js/app.js")
        }
      }
    } ~
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
      } ~
      path("quiz" / "question") {
        get {
          respondWithMediaType(`application/json`) {
            complete {
              (julio ? PullQuestion).mapTo[Option[Question]].map { optionalQuestion =>
                optionalQuestion.fold("{}")(question =>
                  s"""{"question": "${question.question}", "answers": ${choicesToJson(question.choices)}}"""
                )
              }
            }
          }
        }
      } ~
      path("answer") {
        post {
          formFields('answer) {
            (answer) =>
              julio ! Answer(answer)
              complete("{ 'result': 'ok' }")
          }
        } ~
          get {
            respondWithMediaType(`application/json`) {
              complete {
                (ciccio ? PullResults).mapTo[Option[Results]].map { optionalResults =>
                  optionalResults.fold("{}")(results =>
                    s"""{"question": "${results.question}", "answers": ${resultAnswersToJson(results.answers)}}"""
                  )
                }
              }
            }
          }
      }

  def choicesToJson(choices: Seq[String]): String = {
    "[" + choices.map(c => s""" "$c" """).mkString(",") + "]"
  }


  def resultAnswersToJson(answers: Map[String, Boolean]): String = {
    "{" + answers.map{case (actor, result) => s""" "$actor" : $result """}.mkString(",") + "}"
  }
}