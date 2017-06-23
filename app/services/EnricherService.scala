package services

import javax.inject.{Inject, Singleton}

import configs.AppConfig
import indexer.EsClient
import models.themoviedb.MovieDetails
import play.api.libs.json.JsArray
import play.api.libs.ws.WSClient

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class EnricherService @Inject()(
                                 wSClient: WSClient,
                                 appConfig: AppConfig,
                                 searchService: SearchService
                               ) extends EsClient {

  def getMovieIdFromExternalId(externalId: String): Future[Seq[Int]] = {
    val tmdbFindUrl: String = appConfig.tmdbFindBaseUri + externalId + appConfig.tmdbFindParameters

    println(tmdbFindUrl)
    wSClient.url(tmdbFindUrl).get
      .map { response =>
        (response.json \ "movie_results").as[JsArray].value.map { result =>
          (result \ "id").as[Int]
        }
      }

  }

  private def getDetailsFromId(id: Int): Future[MovieDetails] = {

    val tmdbSearchUrl: String = appConfig.tmdbSearchBaseUri + id + appConfig.tmdbSearchParameters

    wSClient.url(tmdbSearchUrl).get
      .map(_.json.as[MovieDetails])

  }

  private def getMovieDetails(externalId: String): Future[Seq[MovieDetails]] = {
    val eventuallyIds = Future.successful(Seq.empty)
    for {
      ids <- eventuallyIds
    } yield {
      ids.foreach(println)
      Future.sequence(
        ids.map { id =>
          getDetailsFromId(id)
        }
      )
    }

  }.flatMap(identity)

  def getAllIds: Future[Seq[Int]] = {
    val eventuallyExternalIds: Future[Seq[String]] = searchService.getMoviesExternalIds
    for {
      externalIds <- eventuallyExternalIds
    } yield {
      Future.sequence(
        externalIds.map { externalId =>
          getMovieIdFromExternalId(externalId)
        }
      ).map(_.flatten)
    }
  }.flatMap(identity)

}
