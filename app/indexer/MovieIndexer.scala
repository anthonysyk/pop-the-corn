package indexer

import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s._
import com.sksamuel.elastic4s.mappings.FieldType._
import com.sksamuel.elastic4s.source.JsonDocumentSource
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse
import play.api.libs.json.Json

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

object CreateIndex extends App with EsClient {

  val createIndexAction: Future[CreateIndexResponse] = client execute {
    create index "movies_index" mappings {
      mapping("movie") as(
        "id" typed StringType index "not_analyzed",
        "title" typed StringType analyzer "default",
        "color" typed StringType index "not_analyzed",
        "duration" typed IntegerType,
        "budget" typed DoubleType,
        "gross" typed DoubleType,
        "genres" typed StringType,
        "contentRating" typed IntegerType index "not_analyzed",
        "faceNumbersInPoster" typed IntegerType,
        "language" typed StringType index "not_analyzed",
        "country" typed StringType index "not_analyzed",
        "titleYear" typed StringType index "not_analyzed",
        "aspectRatio" typed StringType index "not_analyzed",
        "castTotalFacebookLikes" typed IntegerType,
        "plotKeywords" typed StringType index "not_analyzed",
        "movieUrl" typed StringType index "not_analyzed",
        nestedField("casting") as(
          "role" typed StringType index "not_analyzed",
          "name" typed StringType index "not_analyzed",
          "facebookLikes" typed IntegerType
        ),
        nestedField("rating") as(
          "score" typed DoubleType,
          "numberOfReviews" typed IntegerType,
          "numberOfVotes" typed IntegerType,
          "numberOfCriticsForReviews" typed IntegerType
        )
      )
    } analysis {
      CustomAnalyzerDefinition(
        "default",
        WhitespaceTokenizer,
        LowercaseTokenFilter,
        AsciiFoldingTokenFilter)
    }

  }

  val await = Await.result(createIndexAction, 10.seconds)
  println(await)

}

object IndexMovies extends App with EsClient with ReadCsvHelper {

  val indexMoviesAction = movies.foreach { movie =>
    val movieString = Json.stringify(Json.toJson(movie))

    client execute {
      bulk(
        index into "movies_index/movie" doc JsonDocumentSource(movieString)
      )
    }
  }

  indexMoviesAction
}

object DeleteIndex extends App with EsClient {

  val deleteIndexAction = client execute {
    deleteIndex("movies_index")
  }

  deleteIndexAction
}