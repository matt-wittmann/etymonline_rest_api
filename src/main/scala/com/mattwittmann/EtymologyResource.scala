package com.mattwittmann

import akka.actor.Props
import akka.pattern.ask
import akka.util.Timeout
import spray.httpx.SprayJsonSupport
import spray.json.DefaultJsonProtocol
import spray.routing.HttpServiceActor

import scala.concurrent.duration._

class EtymologyResource extends HttpServiceActor with SprayJsonSupport {
  implicit  val time: Timeout = 5.seconds
  import context.dispatcher
  object JsonProtocol extends DefaultJsonProtocol {
    implicit val entryFormat = jsonFormat2(DictionaryEntry)
  }
  import JsonProtocol._

  def receive = runRoute {
    path("etymologies" / Segment) { word =>
      get {
        complete {
          val searchService = context.actorOf(Props[SearchServiceImpl])
          val result = (searchService ? Search(word)).mapTo[DictionaryEntries].map(_.entries)
          result
        }
      }
    }
  }
}
