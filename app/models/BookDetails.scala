package models

import play.api.libs.json.Json

case class BookDetails(title: String, author: String, year: Option[Int], contents: String)

object BookDetails {
  implicit val formats = Json.format[BookDetails]
}