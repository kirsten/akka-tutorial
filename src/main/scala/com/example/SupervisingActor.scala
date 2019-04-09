package com.example

import akka.actor.{Actor, ActorSystem, Props}

// Supervision
// https://doc.akka.io/docs/akka/current/general/supervision.html

object SupervisingActor {
  def props: Props =
    Props(new SupervisingActor)
}

// Parent / Supervisor
class SupervisingActor extends Actor {
  val child = context.actorOf(SupervisedActor.props, "supervised-actor")

  override def receive: Receive = {
    case "failChild" => child ! "fail"
  }
}

object SupervisedActor {
  def props: Props =
    Props(new SupervisedActor)
}

// Child / Supervised
class SupervisedActor extends Actor {
  override def preStart(): Unit = println("supervised actor started")
  override def postStop(): Unit = println("supervised actor stopped")

  override def receive: Receive = {
    case "fail" =>
      println("supervised actor fails now")
      // The supervising actor automatically stops and then restarts this actor.
      // This behavior is the default supervision strategy.
      // If you don’t change the default strategy all failures result in a restart
      throw new Exception("I failed!")
  }
}

object SupervisingActorDemo extends App {
  val system = ActorSystem("supervisingActorDemo")
  val supervisingActor = system.actorOf(SupervisingActor.props, "supervising-actor")
  supervisingActor ! "failChild"
}
