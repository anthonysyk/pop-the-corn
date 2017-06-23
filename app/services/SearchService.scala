package services

import javax.inject.{Inject, Singleton}

import com.sksamuel.elastic4s.ElasticDsl._
import indexer.EsClient
import models.kaggle.MovieData
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

  def getMoviesExternalIds: Future[Seq[String]] = {
    client execute {
      search in "movies_index" -> "movie" limit 3000
    }
  }
    .map{searchResult =>
      val test = (Json.parse(searchResult.toString) \ "hits" \ "hits" \\ "_source")
      test.map(movie =>
        movie.as[MovieData].id
        )
    }

}
