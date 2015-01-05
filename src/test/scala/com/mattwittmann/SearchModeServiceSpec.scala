package com.mattwittmann

import akka.actor.{ActorRef, Inbox, Props}
import akka.util.Timeout
import com.github.tomakehurst.wiremock.client.WireMock._
import org.scalatest.{BeforeAndAfterEach, WordSpec}

import scala.concurrent.duration._
import scala.io.Source

class SearchModeServiceImplSpec extends WordSpec with WireMockTest with BeforeAndAfterEach {
  implicit val timeout: Timeout = 5.seconds
  var impl: ActorRef = _

  override def beforeEach = {
    super.beforeEach
    impl = system.actorOf(Props[SearchModeServiceImpl], classOf[SearchModeServiceImpl].getSimpleName)
  }

  "SearchModeServiceImpl" when {
    "parsing HTML" should {
      "build a map of options" in {
        val file = Source.fromURL(getClass().getResource("/index.html")).mkString
        stubFor(get(urlEqualTo("/")).willReturn(aResponse.withStatus(200).withHeader("Content-Type", "text/html; charset=UTF-8").withBody(file)))
        val inbox = Inbox.create(system)
        inbox.send(impl, GetSearchModes)
        inbox.receive(5.seconds) match {
          case SearchModes(options) =>
            assert(4 === options.size)
            assert("Natural Language" === options(SearchMode("nl")))
            assert("Single Term" === options(SearchMode("term")))
            assert("Any Terms" === options(SearchMode("or")))
            assert("Exact Match" === options(SearchMode("phrase")))
            assert(false === options.isDefinedAt(SearchMode("none")))
          case x => fail(s"Unknown message received: $x")
        }
        stop
      }
    }
  }
}