package com.example

import akka.actor.{Actor, ActorSystem, Props}

import scala.io.StdIn

object PrintMyActorRefActor {

  def props: Props = Props(new PrintMyActorRefActor)
}

class PrintMyActorRefActor extends Actor {

  override def receive: Receive = {
    case "printit" =>
      // Create child, or non-top-level, actors by invoking context.actorOf() from an existing actor.
      // The context.actorOf() method has a signature identical to system.actorOf(), its top-level counterpart.
      // This creates an actor that is a *child* of `firstRef` (below).
      val secondRef = context.actorOf(Props.empty, "second-actor")
      println(s"Second: $secondRef")
  }
}

object ActorHierarchyExperiments extends App {

  val system = ActorSystem("testSystem")

  val firstRef = system.actorOf(PrintMyActorRefActor.props, "first-actor")
  println(s"First: $firstRef")
  firstRef ! "printit"

  println(">>> Press ENTER to exit <<<")
  try StdIn.readLine()
  finally system.terminate()
}

// OUTPUT:
// First: Actor[akka://testSystem/user/first-actor#-663967645]
// >>> Press ENTER to exit <<<
// Second: Actor[akka://testSystem/user/first-actor/second-actor#1065373572]
