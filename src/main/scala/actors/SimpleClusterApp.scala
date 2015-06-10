package actors

import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory

object SimpleClusterApp {
  def main(args: Array[String]): Unit = {
    // Override the configuration of the port
    val config = ConfigFactory.load()

    // Create an Akka system
    val system = ActorSystem("ClusterSystem", config)
    // Create an actor that handles cluster domain events
    system.actorOf(Props[ClusterBroadcaster], name = "clusterListener")
  }
}

