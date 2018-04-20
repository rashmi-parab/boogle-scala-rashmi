package repository

import javax.inject.{Inject, Singleton}

import models.BookDetails
import play.modules.reactivemongo.{MongoController, ReactiveMongoApi, ReactiveMongoComponents}
import reactivemongo.api.ReadPreference
import reactivemongo.api.indexes.{Index, IndexType}
import reactivemongo.bson._
import reactivemongo.play.json.ImplicitBSONHandlers
import reactivemongo.play.json.collection.JSONCollection

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class BookDetailsRepository @Inject()(val reactiveMongoApi: ReactiveMongoApi) extends MongoController with ReactiveMongoComponents {

  import ImplicitBSONHandlers._

  ensureIndexes()

  def indexes: Seq[Index] = Seq(
    Index(key = Seq(("title", IndexType.Text), ("author", IndexType.Text), ("contents", IndexType.Text)), name = Some("titleAuthorContentIndex"))
  )

  lazy val collection: Future[JSONCollection] = database.map(_.collection[JSONCollection]("bookDetails"))

  def ensureIndexes(): Future[Seq[Boolean]] =
    collection.flatMap { collection => Future.sequence(indexes.map(collection.indexesManager.ensure(_))) }

  def save(bookDetails: BookDetails): Future[Unit] =
    collection.flatMap(_.insert(bookDetails)).map { lastError =>
      println(s"Here we can log error : $lastError")
    }

  def find(searchString: String): Future[List[BookDetails]] = {
    val selector = BSONDocument("$text" -> BSONDocument("$search" -> s"$searchString"))
    collection.flatMap(_.find(selector).cursor[BookDetails](ReadPreference.primaryPreferred).collect[List]())
  }

  def findAll(): Future[List[BookDetails]] =
    collection.flatMap(_.find(BSONDocument.empty).cursor[BookDetails](ReadPreference.primaryPreferred).collect[List]())
}
