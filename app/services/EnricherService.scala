package services

import javax.inject.{Inject, Singleton}

import configs.AppConfig
import indexer.EsClient
import models.themoviedb.MovieDetails
import play.api.Logger
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

  def getMovieIdFromExternalId(externalId: String): Future[Option[Int]] = {
    val tmdbFindUrl: String = appConfig.tmdbFindBaseUri + externalId + appConfig.tmdbFindParameters

    println(tmdbFindUrl)
    wSClient.url(tmdbFindUrl).get
      .map { response =>
        (response.json \ "movie_results").as[JsArray].value.headOption.map { result =>
          (result \ "id").as[Int]
        }
      }.recover {
      case error =>
        Logger.error(s" Une erreur est survenue : ${error.getMessage}")
        None
    }
  }

  def getMovieDetailsFromId(id: Int): Future[Option[MovieDetails]] = {

    val tmdbSearchUrl: String = appConfig.tmdbSearchBaseUri + id + appConfig.tmdbSearchParameters

    wSClient.url(tmdbSearchUrl)
      .get
      .map(_.json.asOpt[MovieDetails])
      .recover {
        case error =>
          Logger.error(s"Une erreur est survenue : ${error.getMessage}")
          None
      }

  }

  def getAllIds: Future[Seq[Option[Int]]] = {
    val eventuallyExternalIds: Future[Seq[String]] = searchService.getMoviesExternalIds(0, 6000)
    for {
      externalIds <- eventuallyExternalIds
    } yield {
      Future.sequence(
        externalIds.map { externalId =>
          getMovieIdFromExternalId(externalId)
        }
      )
    }
  }.flatMap(identity)

}
