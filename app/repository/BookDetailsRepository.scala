package repository

import javax.inject.{Inject, Singleton}

import models.BookDetails
import play.modules.reactivemongo.{MongoController, ReactiveMongoApi, ReactiveMongoComponents}
import reactivemongo.api.indexes.{Index, IndexType}
import reactivemongo.play.json.collection.JSONCollection

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class BookDetailsRepository @Inject()(val reactiveMongoApi: ReactiveMongoApi) extends MongoController with ReactiveMongoComponents {

  ensureIndexes()

  def indexes: Seq[Index] = Seq(
    Index(key = Seq(("title", IndexType.Text)), name = Some("titleIndex"), unique = true),
    Index(key = Seq(("title", IndexType.Text), ("author", IndexType.Text)), name = Some("titleAuthorIndex")),
    Index(key = Seq(("$**", IndexType.Text)), name = Some("allTextFieldIndex"))
  )

  lazy val collection: Future[JSONCollection] = database.map(_.collection[JSONCollection]("bookDetails"))

  def ensureIndexes(): Future[Seq[Boolean]] = {
    collection.flatMap { collection => Future.sequence(indexes.map(collection.indexesManager.ensure(_))) }
  }

  def saveBookDetails(bookDetails: BookDetails): Future[Unit] = {
    collection.flatMap(_.insert(bookDetails)).map { lastError =>
      println(s"Successfully inserted with LastError: $lastError")
    }
  }
}
