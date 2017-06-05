package indexer

import javax.inject.{Inject, Singleton}

import configs.AppConfig
import play.api.libs.json.JsValue
import play.api.libs.ws.WSClient

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class MovieEnricherService @Inject()(
                                      wSClient: WSClient,
                                      appConfig: AppConfig
                                    ) {

  private def getMovieIdFromExternalId(externalId: String): Future[Seq[String]] = {
    val tmdbFindUrl: String = appConfig.tmdbFindBaseUri + externalId + appConfig.tmdbFindParameters

    val eventuallyMovies = wSClient.url(tmdbFindUrl).get
      .map(_.json \\ "movie_results")

    for {
      movies <- eventuallyMovies
    } yield {
      movies.map { movie =>
        (movie \ "id").as[String]
      }
    }
  }

  private def getDetailsFromId(id: String): Future[JsValue] = {

    val tmdbSearchUrl: String = appConfig.tmdbSearchBaseUri + id + appConfig.tmdbSearchParameters

    wSClient.url(tmdbSearchUrl).get
      .map(_.json)

  }

  def getMovieDetails(externalId: String): Future[Seq[JsValue]] = {

    val eventuallyIds = getMovieIdFromExternalId(externalId)

    for {
      ids <- eventuallyIds
    } yield {
      Future.sequence(
        ids.map { id =>
          getDetailsFromId(id)
        }
      )
    }

  }.flatMap(identity)


}
