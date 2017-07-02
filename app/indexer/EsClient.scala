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
import play.api.libs.json.{Format, Json}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait EsClient extends FutureHelpers {
  val settings: Settings = ImmutableSettings.settingsBuilder().put("cluster.name", "elasticsearch_Anthony").build()
  val uri = ElasticsearchClientUri("localhost:9300")
  val client: ElasticClient = ElasticClient.remote(settings, uri)

  def ensureIndexExists(index: String): Future[Boolean] = {
    client execute {
      indexExists(index)
    }
  }.map(response => response.isExists)

  def eventuallyDeleteIndex(index: String): Future[DeleteIndexResponse] = client execute {
    deleteIndex(index)
  }

  val MovieDetailsMapping: CreateIndexDefinition = create index "movies_details" mappings {
    mapping("movie") as(
      "adult" typed BooleanType,
      nestedField("belongs_to_collection") as(
        "id" typed IntegerType,
        "name" typed StringType index "not_analyzed",
        "poster_path" typed StringType index "not_analyzed",
        "backdropath" typed StringType index "not_analyzed"
      ),
      "budget" typed FloatType,
      nestedField("genres") as(
        "id" typed IntegerType,
        "name" typed StringType index "not_analyzed"
      ),
      "id" typed IntegerType,
      "imdb_id" typed StringType index "not_analyzed",
      "original_language" typed StringType index "not_analyzed",
      "original_title" typed StringType index "not_analyzed",
      "overview" typed StringType index "not_analyzed",
      "popularity" typed FloatType,
      "poster_path" typed StringType,
      nestedField("production_companies") as(
        "name" typed StringType index "not_analyzed",
        "id" typed IntegerType
      ),
      nestedField("production_countries") as(
        "iso_3166_1" typed StringType index "not_analyzed",
        "name" typed StringType index "not_analyzed"
      ),
      "release_date" typed StringType index "not_analyzed",
      "revenue" typed IntegerType,
      "runtime" typed IntegerType,
      nestedField("spoken_languages") as(
        "iso_639_1" typed StringType index "not_analyzed",
        "name" typed StringType index "not_analyzed"
      ),
      "status" typed StringType index "not_analyzed",
      "title" typed StringType index "not_analyzed",
      "vote_average" typed FloatType,
      "vote_count" typed IntegerType
    )
  } analysis {
    CustomAnalyzerDefinition(
      "default",
      WhitespaceTokenizer,
      LowercaseTokenFilter,
      AsciiFoldingTokenFilter)
  }

  val MovieMapping: CreateIndexDefinition = create index "movies_index" mappings {
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

  def eventuallyCreateIndexWithMapping(mapping: CreateIndexDefinition): Future[CreateIndexResponse] = client execute {
    mapping
  }

  def bulkIndex[A](esIndex: String, esType: String, element: A)(implicit format: Format[A]): Future[BulkResponse] = {
    val json = Json.stringify(Json.toJson(element))

    client execute {
      bulk(
        index into s"$esIndex/$esType" doc JsonDocumentSource(json)
      )
    }
  }

  def countMovies(esIndex: String): Future[Long] = {
    client execute {
      count from esIndex
    }
  }.map(_.getCount)
}
