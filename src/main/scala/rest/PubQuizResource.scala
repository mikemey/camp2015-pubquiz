package rest

import akka.actor.{ActorRef, ActorRefFactory}
import akka.pattern.ask
import akka.util.Timeout
import spray.http.MediaTypes._
import spray.http.StatusCodes
import spray.httpx.SprayJsonSupport
import spray.httpx.marshalling.MetaMarshallers
import spray.json.{DefaultJsonProtocol, _}
import spray.routing.directives.ContentTypeResolver
import spray.routing.{Directives, RoutingSettings}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

class PubQuizResource(clusterBroadcaster: ActorRef, julio: ActorRef, ciccio: ActorRef)
                     (implicit settings: RoutingSettings, resolver: ContentTypeResolver, refFactory: ActorRefFactory)
  extends Directives with DefaultJsonProtocol with SprayJsonSupport with MetaMarshallers {

  import UIModel._
  import actors.QuizMessages._

  implicit val timeout = Timeout(5.seconds)

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
    pathPrefix("js") {
      get {
        path(RestPath) {
          file => respondWithMediaType(`application/javascript`) {
            getFromFile(s"src/main/resources/js/$file")
          }
        }
      }
    } ~
      pathPrefix("question") {
        pathEnd {
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
                  redirect("question/result", StatusCodes.SeeOther)
              }
            }
        } ~
          path("result") {
            get {
              respondWithMediaType(`text/html`) {
                getFromFile("src/main/resources/html/result.html")
              }
            }
          }
      } ~
      path("quiz" / "question") {
        get {
          respondWithMediaType(`application/json`) {
            complete {
              (julio ? PullQuestion).mapTo[Option[Question]].map { optionalQuestion =>
                optionalQuestion.map(q => UIQuestion(q.question, q.choices)).toJson.prettyPrint
              }
            }
          }
        }
      } ~
      pathPrefix("answer") {
        pathEnd {
          get {
            respondWithMediaType(`text/html`) {
              getFromFile("src/main/resources/html/answer.html")
            }
          } ~
            post {
              formFields('answer) {
                (answer) =>
                  julio ! Answer(answer)
                  redirect("answer", StatusCodes.SeeOther)
              }
            }
        } ~
          path("result") {
            get {
              respondWithMediaType(`application/json`) {
                complete {
                  (ciccio ? PullResults).mapTo[Option[LocalResults]].map { optionalResults =>
                    val uiResults = optionalResults.map { r =>
                      val results = r.results.answers.map { answer => UIResult(answer.participantName, answer.isCorrect) }
                      UIResults(r.results.question, results, r.localIsWinner, r.questionFinished)
                    }
                    uiResults.toJson.prettyPrint
                  }
                }
              }
            }
          }
      }
}