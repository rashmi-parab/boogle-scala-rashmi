package controllers

import javax.inject._

import models.BookDetails
import play.api.libs.json._
import play.api.mvc._
import repository.BookDetailsRepository

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}


@Singleton
class BoogleController @Inject()(bookDetailsRepository: BookDetailsRepository) extends Controller {

  def uploadBook = Action.async(parse.json) { implicit request =>
    withValidJsonBody[BookDetails] { bookDetails =>
      bookDetailsRepository.save(bookDetails).map { _ =>
        Created(s"Book : ${bookDetails.title} uploaded successfully")
      } recover {
        case ex: Exception => InternalServerError(ex.getMessage)
      }
    }
  }

  def findBook(searchString: Option[String]) = Action.async { implicit request =>
    val result = searchString match {
      case Some(s) => bookDetailsRepository.find(s)
      case None => bookDetailsRepository.findAll()
    }
    result.map { books => if (books.nonEmpty) Ok(Json.toJson(books)) else NotFound }
  }

  private def withValidJsonBody[T](f: (T) => Future[Result])(implicit request: Request[JsValue], m: Manifest[T], reads: Reads[T]) =
    Try(request.body.validate[T]) match {
      case Success(JsSuccess(payload, _)) => f(payload)
      case Success(JsError(errs)) => Future.successful(BadRequest(JsError.toJson(errs)))
      case Failure(e) => Future.successful(BadRequest(Json.obj("message" -> s"could not parse body due to ${e.getMessage}")))
    }
}
