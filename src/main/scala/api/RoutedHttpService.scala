package api

import akka.actor.{Actor, ActorContext, Props}
import cluster.{Ciccio, ClusterBroadcaster, Julio}
import spray.routing._

class RoutedHttpService extends Actor with HttpService {

  implicit def actorRefFactory: ActorContext = context

  val clusterBroadcaster = context.system.actorOf(Props[ClusterBroadcaster], "clusterBroadcaster")
  val ciccio = context.system.actorOf(Props[Ciccio], "ciccio")
  val julio = context.system.actorOf(Props[Julio], "julio")

  ciccio ! "System is started! Tell Julio!"

  def receive: Receive =
    runRoute(new PubQuizResource(clusterBroadcaster, julio).route)
}