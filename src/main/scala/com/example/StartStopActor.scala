package com.example

import akka.actor.{Actor, ActorSystem, Props}

// More on the Actor Lifecycle:
// https://doc.akka.io/docs/akka/current/actors.html#actor-lifecycle

object StartStopActor1 {
  def props: Props =
    Props(new StartStopActor1)
}

class StartStopActor1 extends Actor {
  // Invoked after the actor has started but before it processes its first message
  override def preStart(): Unit = {
    println("first started")
    context.actorOf(StartStopActor2.props, "second")
  }

  // Invoked just before the actor stops. No messages are processed after this point.
  override def postStop(): Unit = println("first stopped")

  override def receive: Receive = {
    // Stops the actor. Best practice is to send `self` to `stop()` - don't use
    // an `actorRef` to stop arbitrary actors (send a `PoisonPill` instead).
    case "stop" => context.stop(self)
  }
}

// The second actor doesn't implement any behavior for `receive`, but since the
// first actor is its *parent*, it is automatically stopped before the first
// actor is stopped.

object StartStopActor2 {
  def props: Props =
    Props(new StartStopActor2)
}

class StartStopActor2 extends Actor {
  override def preStart(): Unit = println("second started")
  override def postStop(): Unit = println("second stopped")

  // Actor.emptyBehavior is a useful placeholder when we don't
  // want to handle any messages in the actor.
  override def receive: Receive = Actor.emptyBehavior
}

object StartStopActor extends App {
  val system = ActorSystem("startStopActorSystem")
  val first = system.actorOf(StartStopActor1.props, "first")
  first ! "stop"
}

// OUTPUT:
// akka-tutorial first started
// akka-tutorial second started
// akka-tutorial second stopped
// akka-tutorial first stopped
