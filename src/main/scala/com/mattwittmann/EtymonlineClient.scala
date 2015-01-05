package com.mattwittmann

import akka.actor.Actor
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import com.mattwittmann.{ConnectionConfig => config}
import spray.can.Http
import spray.client.pipelining.{SendReceive, _}

import scala.concurrent.Future
import scala.concurrent.duration._

trait EtymonlineClient {
  self: Actor =>
  import context.{dispatcher, system}
  implicit val timeout: Timeout = 5.seconds

  val pipeline: Future[SendReceive] = for (
    Http.HostConnectorInfo(connector, _) <-
    IO(Http) ? Http.HostConnectorSetup(config.hostName, port = config.port)
  ) yield sendReceive(connector)
}
