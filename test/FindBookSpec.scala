import models.BookDetails
import org.scalatestplus.play.{OneAppPerTest, PlaySpec}
import play.api.http.HeaderNames
import play.api.libs.json.{JsValue, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._


class FindBookSpec extends PlaySpec with OneAppPerTest {

  "BoogleController - GET /boogle/books" should {

    "return 200 OK with list of all Books if query is empty" in {
      val bookDetails: JsValue = Json.parse(
        """{
          |"title" : "Functional Programming in Scala",
          |"author" : "PAUL CHIUSANO",
          |"year" : 2015,
          |"contents" : "Functional programming (FP) is based on a simple premise with far-reaching implications"
          |}""".stripMargin)

      val saveBookResult = route(app, FakeRequest(POST, "/boogle/books").withJsonBody(bookDetails).withHeaders(HeaderNames.CONTENT_TYPE -> "application/json")).get
      status(saveBookResult) mustBe 201

      val findBookResult = route(app, FakeRequest(GET, "/boogle/books")).get

      status(findBookResult) mustBe 200

      val books = contentAsJson(findBookResult).as[Seq[BookDetails]]
      books.contains(bookDetails.as[BookDetails]) mustBe true

    }

    "return 200 OK with books that matches the given query" in {
      val book1 = Json.parse(
        """{
          |"title" : "Book1",
          |"author" : "author1",
          |"year" : 2015,
          |"contents" : "Book1 is a good read"
          |}""".stripMargin)

      val book2 = Json.parse(
        """{
          |"title" : "Book2",
          |"author" : "author2",
          |"year" : 2015,
          |"contents" : "Book1 is a better read"
          |}""".stripMargin)

      route(app, FakeRequest(POST, "/boogle/books").withJsonBody(book1).withHeaders(HeaderNames.CONTENT_TYPE -> "application/json")).get
      route(app, FakeRequest(POST, "/boogle/books").withJsonBody(book2).withHeaders(HeaderNames.CONTENT_TYPE -> "application/json")).get

      val findBookResult = route(app, FakeRequest(GET, "/boogle/books?searchString=author2")).get

      status(findBookResult) mustBe 200

      val books = contentAsJson(findBookResult).as[Seq[BookDetails]]
      books.contains(book2.as[BookDetails]) mustBe true
    }
  }
}
