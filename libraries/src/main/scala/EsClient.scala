package ptc.libraries

import java.lang.reflect.Field

import com.sksamuel.elastic4s._
import com.sksamuel.elastic4s.source.JsonDocumentSource
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse
import org.elasticsearch.action.bulk.BulkResponse
import org.elasticsearch.common.settings._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.Future
import io.circe._
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import org.elasticsearch.action.update.UpdateResponse

import scala.util.{Failure, Success, Try}

// TODO: Gestion des exceptions

trait EsClient extends ElasticDsl with CirceHelper {

  val settings: Settings = ImmutableSettings.settingsBuilder().put("cluster.name", "elasticsearch").build()
  val uri = ElasticsearchClientUri("localhost:9300")
  val client: ElasticClient = ElasticClient.remote(settings, uri)

  def upsertIndex(esIndexDefinition: EsIndexDefinition): Future[Boolean] = {

    val indexName = esIndexDefinition.indexName
    val indexDefinition = esIndexDefinition.indexDefinition

    for {
      isExists <- ensureIndexExists(indexName)
      _ <- if (isExists) eventuallyDeleteIndex(indexName) else Future()
    } yield {
      eventuallyCreateIndexWithMapping(indexDefinition)
    }
  }

  def updateIndex(esIndexDefinition: EsIndexDefinition): Future[Boolean] = {

    val indexName = esIndexDefinition.indexName
    val indexDefinition = esIndexDefinition.indexDefinition

    Future(eventuallyCreateIndexWithMapping(indexDefinition))
  }

  def ensureIndexExists(index: String): Future[Boolean] = {
    client execute {
      indexExists(index)
    }
  }.map(response => response.isExists)

  def eventuallyDeleteIndex(index: String): Future[Boolean] = {
    client execute deleteIndex(index)
  }.map{ response =>
    println(response); true
  }.recover {
    case t: Throwable => println(t); false
  }

  // Faire un Try sinon ça plante sans erreur
  def eventuallyCreateIndexWithMapping(mapping: CreateIndexDefinition): Boolean = {
    Try(client execute mapping await (1 second)) match {
      case Success(_) => println("SUCCESS: Index créé"); true
      case Failure(ex) => println(ex); false
    }
  }

  @deprecated("simply insert into -- prefer upserDocument")
  def bulkIndex[A](esIndex: String, esType: String, element: A)(implicit encoder: Encoder[A]): Future[BulkResponse] = {
    val json = element.asJson.noSpaces
    println(json)
    client execute {
      bulk(
        index into s"$esIndex/$esType" doc JsonDocumentSource(json)
      )
    }
  }

  private def parseDocument[T <: Product](element: T)(implicit m: Manifest[T])= {
    m.runtimeClass.getDeclaredFields.map(_.getName).zip(element.productIterator.toSeq)
  }

  // Il serait mieux d'utiliser une Future et un recover
  // MAIS la boucle sera lancée de toute façon parceque la réponse de ES ne sera pas encore arrivée
  // Le await permet d'avoir la réponse de la part de ES avant de continuer
  def upsertDocument[T <: Product](esIndex: String, esType: String, element: T, docId: Any)(implicit encoder: Encoder[T], m: Manifest[T]): Boolean = {
    Try(client execute {
      update id docId in s"$esIndex/$esType" docAsUpsert parseDocument[T](element)
    } await 1000.second)
  } match {
    case Success(result) => println(s"SUCCESS Upserting movie ! $result"); true
    case Failure(ex) => println(ex.getMessage); false
  }

  // Retry with recursion
  // no tail recursion here because no risk of blowing the stack (Futures operates on multiple stacks)
  def upsertDocumentWithRetry[T <: Product {val id: Option[Any]}](element: T, retry: Int = 5)(implicit encoder: Encoder[T], m: Manifest[T], index: IndexDefinition): Boolean = {

    upsertDocument[T](index.IndexName, index.TypeName, element, element.id.getOrElse(0)) match {
      case isIndexed if isIndexed => true
      case _ if retry == 1 => println(s"ERROR: while indexing movie ${element.id.getOrElse("Unkown Movie")} --nbTries = ${6 - retry}"); false
      case _ => upsertDocumentWithRetry(element, retry - 1); false
    }
  }

  def countMovies(esIndex: String): Future[Long] = {
    client execute {
      count from esIndex
    }
  }.map(_.getCount)


  // TODO: Try to use Monoid to convert inner case classes into Maps
  @deprecated
  def convertCaseClassToMap[A <: Product](element: A)(implicit m: Manifest[A]): Map[String, Any] = {
    val keys: Seq[String] = m.runtimeClass.getDeclaredFields.map(_.getName).toVector
    val values: Seq[Any] = element.productIterator.toVector
    val mapping = keys.zip(values).toMap
    mapping.foreach(println)
    mapping
  }
}
