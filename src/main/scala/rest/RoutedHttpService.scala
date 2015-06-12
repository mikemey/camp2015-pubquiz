package rest

import akka.actor.{Actor, ActorContext, Props}
import actors.{Startup, CiccioAkaResultsManager, ClusterBroadcaster, JulioAkaAnswerManager}
import com.typesafe.config.ConfigFactory
import spray.routing._

class RoutedHttpService extends Actor with HttpService {

  implicit def actorRefFactory: ActorContext = context

  val participantName = ConfigFactory.load("application").getString("akka.participant-name")
  val clusterBroadcaster = context.system.actorOf(Props[ClusterBroadcaster], "clusterBroadcaster")
  val ciccio = context.system.actorOf(Props[CiccioAkaResultsManager], "ciccio")
  val julio = context.system.actorOf(Props(classOf[JulioAkaAnswerManager], participantName), "julio")
  val startup = context.system.actorOf(Props(classOf[Startup]), "startup")

  ciccio ! "System is started! Tell Julio!"

  def receive: Receive =
    runRoute(new PubQuizResource(clusterBroadcaster, julio, ciccio).route)
}