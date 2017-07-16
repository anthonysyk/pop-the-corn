package services

import javax.inject.{Inject, Singleton}

import com.sksamuel.elastic4s.ElasticDsl.{fieldFactorScore, _}
import com.sksamuel.elastic4s.{FieldValueFactorDefinition, MatchQueryDefinition}
import indexer.EsClient
import models.{FullMovie, Suggestion}
import models.kaggle.Movie
import org.elasticsearch.action.search.SearchResponse
import org.elasticsearch.common.lucene.search.function.FieldValueFactorFunction
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilder
import play.api.libs.json.Json

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


@Singleton
class SearchService @Inject()() extends EsClient {

  def searchMovie(q: String): Future[SearchResponse] = {
    client execute {
      search in "full_movie" -> "movie" query {
        bool {
          must(
            matchQuery("title", q)
          )
        }
      }
    }
  }

  @deprecated("use full_movie index now", "1")
  def searchMovieDetails(id: Int): Future[SearchResponse] = {
    client execute {
      search in "movies_details" -> "movie" query {
        bool {
          must(
            matchQuery("id", id)
          )
        }
      }
    }
  }

  def getFullMovies(from: Int, size: Int): Future[Seq[FullMovie]] = {
    client execute {
      search in "full_movie" -> "movie" from from size size
    }
  }.map { searchResult =>
    (Json.parse(searchResult.toString) \ "hits" \ "hits" \\ "_source").map { source =>
      source.as[FullMovie]
    }
  }


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

  def getMovies(from: Int, size: Int): Future[Seq[Movie]] = {
    client execute {
      search in "movies_index" -> "movie" from from size size
    }
  }.map { searchResult =>
    (Json.parse(searchResult.toString) \ "hits" \ "hits" \\ "_source").map { source =>
      source.as[Movie]
    }
  }

  def suggest(q: String): Future[Seq[Suggestion]] = {
    val query: MatchQueryDefinition = matchQuery("ngram", q)
    val fieldValueFactor: FieldValueFactorDefinition = fieldFactorScore("votes").modifier(FieldValueFactorFunction.Modifier.LOG1P)
    val finalQuery = functionScoreQuery(query).scorers(fieldValueFactor)
    client execute {
      search in "suggest_movies" -> "suggest" limit 5 query {
        finalQuery
      }
    }
  }.map { searchResults =>
    (Json.parse(searchResults.toString) \ "hits" \ "hits" \\ "_source").map(result => (result \ "suggest").as[Suggestion])
  }

}
