package services

import com.sksamuel.elastic4s.{FieldValueFactorDefinition, MatchQueryDefinition}
import models.{Suggestion, SuggestionES, TmdbMovie}
import org.elasticsearch.action.search.SearchResponse
import org.elasticsearch.common.lucene.search.function.FieldValueFactorFunction
import ptc.libraries.{EsClient, MovieIndexDefinition, SuggestIndexDefinition}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits._
import io.circe._
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._

import scala.util.{Failure, Success}

object ApiService extends EsClient {

  private def checkIndexExists(indexName: String) = {
    ensureIndexExists(MovieIndexDefinition.IndexName).onComplete{
      case Success(result) if result => println(s"SUCCESS: index $indexName existe")
      case Success(result) if !result => println(s"ERROR: index $indexName n'existe pas")
      case Failure(ex) => println(ex)
    }
  }

  def start(): Unit = {
      checkIndexExists(MovieIndexDefinition.IndexName)
      checkIndexExists(SuggestIndexDefinition.IndexName)
  }

  def searchMovie(q: String): Future[ApiService.SearchResponseResponse[TmdbMovie]] = {
    client execute {
      search in MovieIndexDefinition.IndexName -> MovieIndexDefinition.TypeName query {
        bool {
          must(
            matchQuery("title", q)
          )
        }
      }
    }
  }.map { searchResponse =>
    parseSearchResponseWithHits[TmdbMovie](searchResponse.toString)
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
  }.map { searchResponse =>
    parseSearchResponseWithHits[SuggestionES](searchResponse.toString).results.map(_.suggest)
  }

}
