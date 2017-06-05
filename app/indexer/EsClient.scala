package indexer

import com.sksamuel.elastic4s.{ElasticClient, ElasticsearchClientUri}
import org.elasticsearch.common.settings.ImmutableSettings


trait EsClient {
  val settings = ImmutableSettings.settingsBuilder().put("cluster.name", "elasticsearch_Anthony").build()
  val uri = ElasticsearchClientUri("localhost:9300")
  val client = ElasticClient.remote(settings, uri)
}
