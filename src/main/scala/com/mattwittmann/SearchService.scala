package com.mattwittmann

import akka.actor.{Actor, ActorLogging}
import akka.pattern.pipe
import de.hars.scalaxml.Html5Parser
import spray.client.pipelining._
import spray.http.Uri._
import spray.http.{HttpResponse, Uri}

import scala.concurrent.Future
import scala.xml.NodeSeq

/**
 * The actor message for an etymology search.
 *
 * @param terms The search terms
 * @param mode The [[SearchMode]]
 * @param page The optional page in the results
 */
case class Search(terms: String, mode: SearchMode = SearchMode.term, page: Option[Int] = None)

/**
 * A dictionary entry.
 *
 * @param term The term being defined
 * @param definition The term's definition
 */
case class DictionaryEntry(term: String, definition: String)

/**
 * The actor message representing etymology search results.
 *
 * @param entries A sequence of [[DictionaryEntry]]
 */
case class DictionaryEntries(entries: Seq[DictionaryEntry])

/**
 * Queries the Online Etymology Dictionary for etymologies.
 */
class SearchServiceImpl extends Actor with EtymonlineClient with ActorLogging {
  val parser = new Html5Parser
  import context.dispatcher

  /**
   * Searches for etymologies, screen-scraping etymonline.com.
   *
   * @param terms The search terms
   * @param mode The [[SearchMode]]
   * @param page The optional page in the results
   * @return A future of [[DictionaryEntries]]
   */
  def search(terms: String, mode: SearchMode, page: Option[Int]): Future[DictionaryEntries] = {
    val response: Future[HttpResponse] = pipeline.flatMap(_(Get(Uri("/").copy(query =
      Query("search" -> terms, "searchmode" -> mode.value, "p" -> page.getOrElse(0).toString)))))
    response.map(r => DictionaryEntries(parseEntries(r.entity.asString)))
  }

  /**
   * Parses out the definitions from the input HTML.
   *
   * @param input The HTML of the search results
   * @return A sequence of [[DictionaryEntry]]
   */
  def parseEntries(input: String): Seq[DictionaryEntry] = {
    val root = parser.loadXML(input)
    val dls = root \\ "dl"
    // Assuming one dt to each dd
    // TODO This is obviously not the most efficient
    val dts = extractChildren(dls, "dt")
    val dds = extractChildren(dls, "dd")

    if (dts.length == dds.length) {
      for (i <- 0 until dts.length)
        yield DictionaryEntry(dts(i), dds(i))
    }
    else {
      log.error("The number of terms and definitions are different!")
      Nil
    }
  }

  /**
   * Extracts child nodes from a parent NodeSeq matching a label.
   *
   * @param parents The parent NodeSeq
   * @param elementName The label to match children on
   * @return A sequence of the text values of the matching child nodes
   */
  def extractChildren(parents: NodeSeq, elementName: String) =
    parents.flatMap(_.child.filter(_.label == elementName).map(_.text))

  def receive = {
    case Search(terms, mode, page) => pipe(search(terms, mode, page)) to sender
  }
}
