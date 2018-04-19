import org.scalatestplus.play._
import play.api.http.HeaderNames
import play.api.libs.json.{JsValue, Json}
import play.api.test.Helpers._
import play.api.test._

class ApplicationSpec extends PlaySpec with OneAppPerTest {

  "BoogleController - POST /boogle/books" should {

    "upload a new book successfully and return 200 Ok" in {
      val bookDetails: JsValue = Json.parse(
        """{
          |"title" : "Functional Programming in Scala",
          |"author" : "PAUL CHIUSANO",
          |"year" : 2015,
          |"contents" : "Functional programming (FP) is based on a simple premise with far-reaching implications"
          |}""".stripMargin)

      val result = route(app, FakeRequest(POST, "/boogle/books").withJsonBody(bookDetails).withHeaders(HeaderNames.CONTENT_TYPE -> "application/json")).get

      status(result) mustBe 201
      contentAsString(result) must include("Book : Functional Programming in Scala uploaded successfully")
    }

    "return 400 BadRequest when missing title in request" in {
      val bookDetails: JsValue = Json.parse(
        """{
          |"author" : "PAUL CHIUSANO",
          |"year" : 2015,
          |"contents" : "Functional programming (FP) is based on a simple premise with far-reaching implications"
          |}""".stripMargin)

      val result = route(app, FakeRequest(POST, "/boogle/books").withJsonBody(bookDetails).withHeaders(HeaderNames.CONTENT_TYPE -> "application/json")).get

      status(result) mustBe 400
    }
  }
}
