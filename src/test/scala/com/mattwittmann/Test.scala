package com.mattwittmann

import akka.actor.ActorSystem
import akka.io.IO
import akka.pattern.ask
import spray.can.Http
import spray.client.pipelining._
import spray.http._
import spray.util._
import spray.http.Uri._

import scala.concurrent.Future
import scala.concurrent.duration._

object Test extends App {
  implicit val system = ActorSystem()
  import com.mattwittmann.Test.system.dispatcher // execution context for futures

  val pipeline: HttpRequest => Future[HttpResponse] = sendReceive

//  val response: Future[HttpResponse] = pipeline(Get("http://www.etymonline.com/"))
  val response: Future[HttpResponse] = pipeline(Get(Uri("http://www.etymonline.com/").copy(query = Query("search" -> "test", "searchmode" -> "none"))))

  response.foreach { response =>
    println(response.entity.asString)
    IO(Http).ask(Http.CloseAll)(1.second).await
    system.shutdown
  }
}
