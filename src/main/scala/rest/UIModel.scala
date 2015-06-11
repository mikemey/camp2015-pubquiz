package rest

import spray.json.DefaultJsonProtocol._

object UIModel {

  case class UIQuestion(question: String, answers: Seq[String])

  case class UIResult(id: String, answer: String, isCorrect: Boolean)

  case class UIResults(question: String, results: Seq[UIResult], localIsWinner: Boolean, allResults: Boolean)

  case class UIConnected(connected: Boolean)

  implicit val questionUiFormat = jsonFormat2(UIQuestion)
  implicit val uiResultFormat = jsonFormat3(UIResult)
  implicit val uiResultsFormat = jsonFormat4(UIResults)
  implicit val uiConnectedFormat = jsonFormat1(UIConnected)

}
