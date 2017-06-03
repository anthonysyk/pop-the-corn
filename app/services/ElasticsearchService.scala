package services

import javax.inject.{Inject, Singleton}

import helpers.ReadCsvHelper
import indexer.EsClient
import com.sksamuel.elastic4s.ElasticDsl._
import play.api.libs.json.Json
import com.sksamuel.elastic4s.source.JsonDocumentSource


@Singleton
class ElasticsearchService @Inject()() extends ReadCsvHelper with EsClient {



}
