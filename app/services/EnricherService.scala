package services

import java.util.concurrent.Executors
import javax.inject.{Inject, Singleton}

import configs.AppConfig
import indexer.EsClient
import models.themoviedb.MovieDetails
import play.api.Logger
import play.api.libs.json.JsArray
import play.api.libs.ws.WSClient

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.{Failure, Success}
import concurrent.duration._

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


  // Serial and blocking way to enrich movies just for testing withouy akka actors
  def getAllIdsBlocking = {

    val customExecutor = ExecutionContext.fromExecutor(
      Executors.newFixedThreadPool(1))

    def serialiseFutures[A, B](l: Iterable[A])(fn: A ⇒ Future[B])
                              (implicit ec: ExecutionContext): Future[List[B]] =
      l.foldLeft(Future(List.empty[B])) {
        (previousFuture, next) ⇒
          for {
            previousResults ← previousFuture
            next ← fn(next)
          } yield previousResults :+ next
      }


    val eventuallyExternalIds: Future[Seq[String]] = searchService.getMoviesExternalIds(0, 6000)
    eventuallyExternalIds.map { externalIds =>
      def future(externalIds: Seq[String])(implicit ec: ExecutionContext): Unit = {

        def fn(externalId: String) = Future {
          println("start")
          Thread.sleep(3000)
          getMovieIdFromExternalId(externalId).onComplete {
            case Success(response) => Logger.info(s"SUCCESS : ${response.toString}")
            case Failure(e) => Logger.error(s" ERREUR : $e")
          }
          println("stop")
        }

        serialiseFutures(externalIds)(fn)(customExecutor)

      }

      future(externalIds)
    }
  }

}
