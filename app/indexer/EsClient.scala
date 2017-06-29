package indexer

import com.sksamuel.elastic4s.ElasticDsl.{create, mapping, nestedField, _}
import com.sksamuel.elastic4s._
import com.sksamuel.elastic4s.mappings.FieldType._
import com.sksamuel.elastic4s.source.JsonDocumentSource
import helpers.FutureHelpers
import models.kaggle.Movie
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse
import org.elasticsearch.action.bulk.BulkResponse
import org.elasticsearch.common.settings.{ImmutableSettings, Settings}
import play.api.libs.json.Json

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait EsClient extends FutureHelpers {
  val settings: Settings = ImmutableSettings.settingsBuilder().put("cluster.name", "elasticsearch_Anthony").build()
  val uri = ElasticsearchClientUri("localhost:9300")
  val client: ElasticClient = ElasticClient.remote(settings, uri)

  def ensureIndexExists: Future[Boolean] = {
    client execute {
      indexExists("movies_index")
    }
  }.map(response => response.isExists)

  def eventuallyDeleteIndex: Future[DeleteIndexResponse] = client execute {
    deleteIndex("movies_index")
  }

  def eventuallyCreateIndexWithMapping: Future[CreateIndexResponse] = client execute {
    create index "movies_index" mappings {
      mapping("movie") as(
        "externalId" typed StringType index "not_analyzed",
        "id" typed IntegerType,
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

  def bulkIndexMovie(movie: Movie): Future[BulkResponse] = {
    val movieString = Json.stringify(Json.toJson(movie))

    client execute {
      bulk(
        index into "movies_index/movie" doc JsonDocumentSource(movieString)
      )
    }
  }

  def countMovies: Future[Long] = {
    client execute {
      count from "movies_index"
    }
  }.map(_.getCount)
}
