package services

import com.sksamuel.elastic4s.{FieldValueFactorDefinition, MatchAllQueryDefinition, MatchQueryDefinition}
import models.{MovieDetails, Suggestion, SuggestionES, TmdbMovie}
import org.elasticsearch.action.search.SearchResponse
import org.elasticsearch.common.lucene.search.function.FieldValueFactorFunction
import ptc.libraries.{EsClient, MovieIndexDefinition, SuggestIndexDefinition}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits._
import io.circe._
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import org.elasticsearch.search.sort.SortOrder
import services.ApiService.{client, field, parseSearchResponseWithHits, search}
import services.ExploreES.{client, search, termsQuery}

import scala.util.{Failure, Success}

object ApiService extends EsClient {

  private def checkIndexExists(indexName: String) = {
    ensureIndexExists(MovieIndexDefinition.IndexName).onComplete {
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
      search in SuggestIndexDefinition.IndexName -> SuggestIndexDefinition.TypeName limit 5 query {
        finalQuery
      }
    }
  }.map { searchResponse =>
    parseSearchResponseWithHits[SuggestionES](searchResponse.toString).results.map(_.suggest)
  }

  def getMovie(id: String): Future[Option[MovieDetails]] = {
    client execute {
      search in MovieIndexDefinition.IndexName -> MovieIndexDefinition.TypeName query {
        termQuery("id", id)
      }
    }
  }.map { searchResponse =>
    parseSearchResponseWithHits[TmdbMovie](searchResponse.toString).results.headOption.map(MovieDetails.fromTmdbMovie)
  }

  def getPopularMovies(limit: Int): Future[Seq[MovieDetails]] = {
    client execute {
      search in MovieIndexDefinition.IndexName -> MovieIndexDefinition.TypeName sort (field sort "popularity" order SortOrder.DESC) limit limit
    }
  }.map { searchResponse =>
    parseSearchResponseWithHits[TmdbMovie](searchResponse.toString).results.map(MovieDetails.fromTmdbMovie)
  }

  def getBestRatedMovies: Future[Seq[MovieDetails]] = {
    client execute {
      search in MovieIndexDefinition.IndexName -> MovieIndexDefinition.TypeName sort(field sort "vote_count" order SortOrder.DESC, field sort "vote_average" order SortOrder.DESC) limit
        50
    }
  }.map { searchResponse =>
    parseSearchResponseWithHits[TmdbMovie](searchResponse.toString).results.map(MovieDetails.fromTmdbMovie)
  }

  def getMovies(ids: Seq[String]): Future[Seq[MovieDetails]] = {
    client execute {
      search in MovieIndexDefinition.IndexName -> MovieIndexDefinition.TypeName query {
        termsQuery("_id", ids: _*)
      } limit 100
    }
  }.map { searchResponse =>
    parseSearchResponseWithHits[TmdbMovie](searchResponse.toString).results.map(MovieDetails.fromTmdbMovie)
  }

}


object ExploreES extends EsClient {

  def main(args: Array[String]): Unit = {

    val typesOfGenres = {
      client execute {
        search in MovieIndexDefinition.IndexName -> MovieIndexDefinition.TypeName query matchAllQuery limit 100000
      }
    }.map { searchResponse =>
      parseSearchResponseWithHits[TmdbMovie](searchResponse.toString).results.map(MovieDetails.fromTmdbMovie)
        .flatMap(_.genres.split(" "))
        .map(genre => genre -> 1)
        .groupBy(_._1)
        .mapValues(_.map(_._2).sum)
        .toSeq
        .sortWith((a, b) => a._2 > b._2)
    }.await

    val genreRefential = {
      client execute {
        search in MovieIndexDefinition.IndexName -> MovieIndexDefinition.TypeName query matchAllQuery limit 100000
      }
    }.map { searchResponse =>
      parseSearchResponseWithHits[TmdbMovie](searchResponse.toString)
        .results.flatMap(_.genres).distinct
    }.await

    //    (Drama,3645)
    //    (,3213)
    //    (Comedy,2314)
    //    (Documentary,2170)
    //    (Thriller,1244)
    //    (Horror,1012)
    //    (Romance,951)
    //    (Action,799)
    //    (Animation,572)
    //    (Crime,501)
    //    (Family,462)
    //    (Science,456)
    //    (Fiction,456)
    //    (Adventure,447)
    //    (Music,436)
    //    (Mystery,360)
    //    (Fantasy,315)
    //    (Movie,257)
    //    (TV,257)
    //    (History,214)
    //    (War,128)
    //    (Western,54)
    //    (Foreign,22)

    typesOfGenres.foreach(println)
    genreRefential.foreach(println)

  }

}