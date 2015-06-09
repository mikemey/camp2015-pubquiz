package api

import akka.actor.{Actor, Props}
import cluster.ClusterBroadcaster
import spray.routing._

class RoutedHttpService extends Actor with HttpService {

  implicit def actorRefFactory = context

  val clusterBroadcaster = context.actorOf(Props[ClusterBroadcaster])

  def receive: Receive =
    runRoute(new PubQuizResource(clusterBroadcaster).route)
}