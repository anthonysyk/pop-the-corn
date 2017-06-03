package indexer

import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.mappings.FieldType._
import com.sksamuel.elastic4s.source.JsonDocumentSource
import com.sksamuel.elastic4s.{ElasticClient, ElasticsearchClientUri}
import helpers.ReadCsvHelper
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse
import org.elasticsearch.common.settings.ImmutableSettings
import play.api.libs.json.Json

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}


case class Artist(name: String)

trait EsClient {
  val settings = ImmutableSettings.settingsBuilder().put("cluster.name", "elasticsearch_Anthony").build()
  val uri = ElasticsearchClientUri("localhost:9300")
  val client = ElasticClient.remote(settings, uri)
}

object CreateIndex extends App with EsClient {

  val createIndexAction: Future[CreateIndexResponse] = client execute {
    create index "movies_index" mappings {
      mapping("movie") as(
        "id" typed LongType,
        "title" typed StringType index "not_analyzed",
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
        "movieLink" typed StringType index "not_analyzed",
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
    }
  }

  val await = Await.result(createIndexAction, 10.seconds)
  println(await)

}

object IndexMovies extends App with EsClient with ReadCsvHelper {

  val indexMoviesAction = movies.foreach{ movie =>
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