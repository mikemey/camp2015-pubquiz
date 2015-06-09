package api

import akka.actor.{Actor, Props}
import cluster.{Julio, ClusterBroadcaster}
import spray.routing._

class RoutedHttpService extends Actor with HttpService {

  implicit def actorRefFactory = context

  val clusterBroadcaster = context.actorOf(Props[ClusterBroadcaster])
  val julio = context.actorOf(Props[Julio])

  def receive: Receive =
    runRoute(new PubQuizResource(clusterBroadcaster, julio).route)
}