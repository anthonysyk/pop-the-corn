package indexer

import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s._
import com.sksamuel.elastic4s.source.JsonDocumentSource
import helpers.FutureHelpers
import indexer.mapping.EsIndexDefinition
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse
import org.elasticsearch.action.bulk.BulkResponse
import org.elasticsearch.common.settings.{ImmutableSettings, Settings}
import play.api.Logger
import play.api.libs.json.{Format, Json}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait EsClient extends FutureHelpers{
  val settings: Settings = ImmutableSettings.settingsBuilder().put("cluster.name", "elasticsearch_Anthony").build()
  val uri = ElasticsearchClientUri("localhost:9300")
  val client: ElasticClient = ElasticClient.remote(settings, uri)

  def upsertIndex(esIndexDefinition: EsIndexDefinition) = {

    val indexName = esIndexDefinition.indexName
    val indexDefinition = esIndexDefinition.indexDefinition

    for {
      isExists <- ensureIndexExists(indexName)
      _ <- if (isExists) eventuallyDeleteIndex(indexName) else Future.successful(())
      isAcknowledged <- eventuallyCreateIndexWithMapping(indexDefinition).map(_.isAcknowledged)
    } yield {
      Logger.info(s"Index created: $isAcknowledged")
    }
  }

  def ensureIndexExists(index: String): Future[Boolean] = {
    client execute {
      indexExists(index)
    }
  }.map(response => response.isExists)

  def eventuallyDeleteIndex(index: String): Future[DeleteIndexResponse] = client execute deleteIndex(index)

  def eventuallyCreateIndexWithMapping(mapping: CreateIndexDefinition): Future[CreateIndexResponse] = client execute mapping

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
