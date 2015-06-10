package rest

import spray.json.DefaultJsonProtocol._

object UIModel {

  case class UIQuestion(question: String, answers: Seq[String])

  case class UIResult(id: String, isCorrect: Boolean)

  case class UIResults(question: String, results: Seq[UIResult], localIsWinner: Boolean)

  implicit val questionUiFormat = jsonFormat2(UIQuestion)
  implicit val uiResultFormat = jsonFormat2(UIResult)
  implicit val uiResultsFormat = jsonFormat3(UIResults)

}
