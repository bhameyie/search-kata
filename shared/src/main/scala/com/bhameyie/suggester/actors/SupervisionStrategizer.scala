package com.bhameyie.suggester.api.actors

object SupervisionStrategizer {

  import akka.actor.{ActorRef, ActorSystem, Props}
  import akka.pattern.{Backoff, BackoffSupervisor}
  import akka.routing.BalancingPool

  import scala.concurrent.duration._

  def basicSupervision(props: Props, name: String)(implicit actorSystem: ActorSystem): ActorRef = {
    val supervisor = BackoffSupervisor.props(
      Backoff.onStop(props,
        childName = name,
        minBackoff = 3.seconds,
        maxBackoff = 30.seconds,
        randomFactor = 0.2 // adds 20% "noise" to vary the intervals slightly
      ))

    val pool = BalancingPool(5).props(supervisor)

    actorSystem.actorOf(pool, s"${name}Supervisor")
  }

}

