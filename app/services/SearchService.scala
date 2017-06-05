package services

import javax.inject.{Inject, Singleton}

import com.sksamuel.elastic4s.ElasticDsl._
import indexer.EsClient
import play.api.libs.json.{JsValue, Json}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


@Singleton
class SearchService @Inject()() extends EsClient {

  def searchMovie(q: String): Future[JsValue] = {

    val eventuallySearchResults = client execute {
      search in "movies_index" -> "movie" query {
        bool {
          must(
            termQuery("title", q)
          )
        }
      }
    }

    eventuallySearchResults.map { searchResult =>
      Json.parse(searchResult.toString)
    }

  }

}
