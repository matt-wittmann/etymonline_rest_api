package de.hars.scalaxml

import scala.xml._
import scala.xml.parsing._

/**
 * From [[http://www.hars.de/2009/01/html-as-xml-in-scala.html]].
 */
class Html5Parser extends NoBindingFactoryAdapter {

  override def loadXML(source : InputSource, _p: SAXParser) = {
    loadXML(source)
  }

  def loadXML(source : InputSource): Node = {
    import nu.validator.htmlparser.common.XmlViolationPolicy
    import nu.validator.htmlparser.sax.HtmlParser
    import nu.validator.htmlparser.{common, sax}

    val reader = new HtmlParser
    reader.setXmlPolicy(XmlViolationPolicy.ALLOW)
    reader.setContentHandler(this)
    reader.parse(source)
    rootElem
  }

  def loadXML(string: String): Node = loadXML(Source.fromString(string))
}

