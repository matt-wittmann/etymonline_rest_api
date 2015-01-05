package com.mattwittmann

import akka.actor.{ActorRef, Inbox, Props}
import akka.util.Timeout
import com.github.tomakehurst.wiremock.client.WireMock._
import org.scalatest.{BeforeAndAfterEach, WordSpec}

import scala.concurrent.duration._
import scala.io.Source

class SearchServiceImplSpec extends WordSpec with WireMockTest with BeforeAndAfterEach {
  implicit val timeout: Timeout = 5.seconds
  var impl: ActorRef = _

  override def beforeEach = {
    super.beforeEach
    impl = system.actorOf(Props[SearchServiceImpl], classOf[SearchServiceImpl].getSimpleName)
  }

  "SearchServiceImpl" when {
    "parsing HTML" should {
      "Show HTML" in {
        val file = Source.fromURL(getClass().getResource("/test.html")).mkString
        stubFor(get(urlEqualTo("/?search=test&searchmode=none&p=0"))
          .withQueryParam("search", equalTo("test"))
          .withQueryParam("searchmode", equalTo("none"))
          .withQueryParam("p", equalTo("0"))
          .willReturn(aResponse.withStatus(200)
          .withHeader("Content-Type", "text/html; charset=UTF-8").withBody(file)))
        val inbox = Inbox.create(system)
        inbox.send(impl, Search("test", SearchMode("none")))
        inbox.receive(5.seconds) match {
          case DictionaryEntries(entries) =>
            assert(3 === entries.length)
            for (entry <- entries) {
              log.info("\nTerm: {}\nDefinition: {}", entry.term, entry.definition)
            }
          case x => fail(s"Unknown message received: $x")
        }
        stop
      }
    }
  }
}
