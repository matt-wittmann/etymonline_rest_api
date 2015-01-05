package com.mattwittmann

import akka.actor.Actor
import akka.pattern.pipe
import de.hars.scalaxml.Html5Parser
import spray.client.pipelining._
import spray.http._

import scala.concurrent.Future

/**
 * Actor message asking for available search modes.
 */
case object GetSearchModes

/**
 * A value type for a search mode.
 *
 * @param value The value of the search mode
 */
case class SearchMode(value: String) extends AnyVal

/**
 * Actor message representing available search modes.
 *
 * @param modes A map of [[SearchMode]]s to human-readable descriptions of the search modes
 */
case class SearchModes(modes: Map[SearchMode, String])

object SearchMode {
  val term = SearchMode("term")
}

/**
 * Queries the Online Etymology Dictionary for available search modes.
 */
class SearchModeServiceImpl extends Actor with EtymonlineClient {
  val parser = new Html5Parser
  import context.dispatcher

  /**
   * Gets the search modes, screen-scraping etymonline.com.
   *
   * @return A future of [[SearchModes]]
   */
  def getSearchModes: Future[SearchModes] = {
    val response: Future[HttpResponse] = pipeline.flatMap(_(Get("/")))
    response.map(r => SearchModes(parseSearchModes(r.entity.asString)))
  }

  /**
   * Parses the search modes from the HTML.
   *
   * @param input The index page's HTML as a string
   * @return A map from [[SearchMode]] to its string value
   */
  def parseSearchModes(input: String): Map[SearchMode, String] = {
    val root = parser.loadXML(input)
    val select = (root \\ "select").filter(e => (e \ "@name").text == "searchmode")
    (select \ "option")
      // Do not include the helper option
      .filterNot(_.attribute("selected").isDefined)
      // Map each option to a SearchMode -> value
      .map { option =>
      val value = SearchMode((option \ "@value").text)
      val text = option.text
      value -> text
    }.toMap
  }

  def receive = {
    case GetSearchModes => pipe(getSearchModes) to sender
  }
}