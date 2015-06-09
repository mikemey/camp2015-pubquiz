package cluster

import akka.actor.{Actor, ActorLogging}
import cluster.ClusterBroadcaster._

class Ciccio extends Actor with ActorLogging {

  var results: Results = null

  override def receive: Receive = {

    case results: Results =>
      this.results = results

    case PullResults =>
      sender() ! Option(results)
  }

}
