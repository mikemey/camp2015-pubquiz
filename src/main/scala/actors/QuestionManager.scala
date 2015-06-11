package actors

import actors.QuizMessages._
import akka.actor.{Actor, ActorLogging}
import akka.cluster.ClusterEvent._
import akka.cluster.{Cluster, Member}
import spray.http.DateTime


class QuestionManager(question: String, correctAnswer: String, var participants: Seq[Member]) extends Actor with ActorLogging {
  val cluster = Cluster(context.system)
  var recordedAnswers: collection.mutable.Buffer[AnswerResult] = collection.mutable.Buffer[AnswerResult]()
  var timesUp = false
  val quizStartTime = DateTime.now

  override def preStart(): Unit = {
    cluster.subscribe(self, initialStateMode = InitialStateAsEvents,
      classOf[MemberEvent], classOf[UnreachableMember])
  }

  override def postStop(): Unit = cluster.unsubscribe(self)

  override def receive: Receive = {

    case UnreachableMember(member) =>
      log.info("Member detected as unreachable: {}", member)
      participants = participants.filterNot(_ == member)
      finishIfGameIsOver()

    case MemberRemoved(member, _) =>
      log.info("Member detected as removed: {}", member)
      participants = participants.filterNot(_ == member)
      finishIfGameIsOver()

    case MemberUp(member) =>
      log.info("Member detected as up: {}", member)

    case Answer(value, participantName) =>
      val senderAddress = sender().path.address.toString
      log.info(s"received answer from $senderAddress - '$value'")

      val result: AnswerResult = AnswerResult(senderAddress, participantName, value == correctAnswer)
      recordedAnswers += result
      finishIfGameIsOver()

    case QuestionTimeOut =>
      timesUp = true
      finishIfGameIsOver()
    case s => log.warning("QuestionManager - Unknown message: " + s)
  }

  private def finishIfGameIsOver(): Unit = {
    val currentResult = Results(question, recordedAnswers.toSeq)
    println("sending result to local ciccio: " + currentResult)
    context.actorSelection("/user/ciccio") ! currentResult
    if (recordedAnswers.size >= participants.size || timesUp) {
      context.actorSelection("/user/ciccio") ! ResultsComplete
      broadcastResults(currentResult)
      context.stop(self)
    } else Unit
  }

  private def broadcastResults(results: Results) = {
    participants.foreach { member =>
      val remoteCiccio = cluster.system.actorSelection(s"${member.address.toString}/user/ciccio")
      log.info(s"sending results $results to member $remoteCiccio")
      remoteCiccio ! results
    }
  }

}
