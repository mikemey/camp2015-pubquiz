package core

import akka.actor.{ActorSystem, Props}
import cluster.ClusterBroadcaster
import com.typesafe.config.ConfigFactory

/**
 * Core is type containing the ``system: ActorSystem`` member. This enables us to use it in our
 * apps as well as in our tests.
 */
trait Core {

  implicit def system: ActorSystem

}

/**
 * This trait implements ``Core`` by starting the required ``ActorSystem`` and registering the
 * termination handler to stop the system when the JVM exits.
 */
trait BootedCore extends Core {

  /**
   * Construct the ActorSystem we will use in our application
   */

  // Create an Akka system
  val config = ConfigFactory.load()
  implicit lazy val system = ActorSystem("ClusterSystem", config)

  /**
   * Ensure that the constructed ActorSystem is shut down when the JVM shuts down
   */
  sys.addShutdownHook(system.terminate())

}

/**
 * This trait contains the actors that make up our application; it can be mixed in with
 * ``BootedCore`` for running code or ``TestKit`` for unit and integration tests.
 */
trait CoreActors {
  this: Core =>

  val clusterBroadcaster = system.actorOf(Props[ClusterBroadcaster])

}