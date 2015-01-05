package com.mattwittmann

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import spray.can.Http

object Boot extends App {
  implicit val system = ActorSystem("etymonline_rest_api")
  val etymologyResource = system.actorOf(Props[EtymologyResource], classOf[EtymologyResource].getSimpleName)
  IO(Http) ! Http.Bind(etymologyResource, "localhost", port = 8080)
}
