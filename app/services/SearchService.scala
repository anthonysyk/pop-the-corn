package services

import javax.inject.{Inject, Singleton}

import com.sksamuel.elastic4s.ElasticDsl._
import indexer.EsClient
import models.kaggle.Movie
import play.api.libs.json.{JsValue, Json}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


@Singleton
class SearchService @Inject()() extends EsClient {

  def searchMovie(q: String): Future[JsValue] = {
    client execute {
      search in "movies_index" -> "movie" query {
        bool {
          must(
            termQuery("title", q)
          )
        }
      }
    }
  }.map(searchResult => Json.parse(searchResult.toString))

  def getMoviesExternalIds(from: Int, size: Int): Future[Seq[String]] = {
    client execute {
      search in "movies_index" -> "movie" from from size size
    }
  }.map { searchResult =>
    (Json.parse(searchResult.toString) \ "hits" \ "hits" \\ "_source").map { source =>
      (source \ "externalId").as[String]
    }
  }

  def getMoviesIds(from: Int, size: Int): Future[Seq[Option[Int]]] = {
    client execute {
      search in "movies_index" -> "movie" from from size size
    }
  }.map { searchResult =>
    (Json.parse(searchResult.toString) \ "hits" \ "hits" \\ "_source").map { source =>
      (source \ "id").asOpt[Int]
    }
  }

}
