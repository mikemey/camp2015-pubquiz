package actors

import actors.QuizMessages.{ClusterConnected, DefaultQuestionExpirationInMinutes, Question}
import akka.actor._
import akka.cluster.Cluster
import akka.cluster.ClusterEvent.{InitialStateAsEvents, MemberUp}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

class ClusterBroadcaster extends Actor with ActorLogging {

  import QuizMessages.{BroadcastQuestion, QuestionTimeOut}

  import scala.collection.JavaConverters._

  val cluster = Cluster(context.system)
  var connected = false

  override def preStart(): Unit = {
    cluster.subscribe(self, initialStateMode = InitialStateAsEvents, classOf[MemberUp])
  }

  def receive = {
    case MemberUp(member) =>
      if (cluster.selfUniqueAddress == member.uniqueAddress) {
        connected = true
      }

    case ClusterConnected => sender() ! connected
      
    case BroadcastQuestion(question, choices) =>

      val correctAnswer = choices.find(_.isCorrect).getOrElse(throw new RuntimeException("There must be a correct choice")).value
      val selfAddress = cluster.selfAddress
      val activeMembers = cluster.state.getMembers.asScala.filterNot(_.address == selfAddress)

      val questionManager = cluster.system.actorOf(Props(
        classOf[QuestionManager],
        question,
        correctAnswer,
        activeMembers), s"question-manager-${(Math.random() * 100).toInt}")

      activeMembers.foreach { member =>
        val remoteJulio = cluster.system.actorSelection(s"${member.address.toString}/user/julio")
        val data = Question(question, choices.map(_.value), questionManager)
        log.info(s"sending question $question to member $remoteJulio")
        remoteJulio ! data
      }

      context.system.scheduler.scheduleOnce(DefaultQuestionExpirationInMinutes.minutes, questionManager, QuestionTimeOut)

    case _ =>
      log.warning(s"Unknown message")
  }
}
