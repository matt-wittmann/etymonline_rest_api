package com.mattwittmann

import akka.actor.ActorSystem
import akka.event.{Logging, LoggingAdapter}
import akka.io.IO
import akka.pattern.ask
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration._
import com.mattwittmann.{ConnectionConfig => config}
import org.scalatest.{BeforeAndAfterEach, Suite}
import spray.can.Http
import spray.util._

import scala.concurrent.duration._

trait WireMockTest extends BeforeAndAfterEach {
  self: Suite =>
  protected implicit var system: ActorSystem = _
  protected var log: LoggingAdapter = _
  protected val server: WireMockServer = new WireMockServer(wireMockConfig().port(config.port))

  override def beforeEach = {
    system = ActorSystem(getClass.getSimpleName)
    log = Logging(system, getClass.getName)
    server.start
    WireMock.configureFor(config.hostName, config.port)
  }

  override def afterEach = {
    server.stop
  }

  def stop = {
    IO(Http).ask(Http.CloseAll)(1.second).await
    system.shutdown
  }
}
