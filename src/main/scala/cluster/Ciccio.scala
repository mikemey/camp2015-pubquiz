package cluster

import akka.actor.{Actor, ActorLogging}
import akka.cluster.Cluster
import cluster.ClusterBroadcaster._

class Ciccio extends Actor with ActorLogging {

  val cluster = Cluster(context.system)
  var results: Results = null

  override def receive: Receive = {

    case results: Results =>
      this.results = results

    case PullResults =>
      sender() ! Option(results)

    case "System is started! Tell Julio!" =>
      log.info("Julio tiene que lavarse")
      context.actorSelection("/user/julio") ! "Lavate JULIO!"

    case _ => log.warning("Ciccio - Unknown message")
  }

}