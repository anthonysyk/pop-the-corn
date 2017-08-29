package indexer

import javax.inject.{Inject, Singleton}

import akka.actor.{Actor, ActorRef, Props}
import indexer.MovieEnricher._
import indexer.MovieEnricherWorker._
import indexer.mapping.FullMovieIndexDefinition
import models.{FullMovie, Movie}
import play.api.Logger
import services.{EnricherService, SearchService}

import scala.concurrent.ExecutionContext.Implicits.global


@Singleton
class MovieEnricher @Inject()(
                               searchService: SearchService,
                               enricherService: EnricherService
                             ) extends Actor with EsClient {

  var incompleteTasks = 0
  var failures = 0
  var batch: Batch[Movie] = Batch.empty[Movie]
  val workers: Seq[ActorRef] = createWorkers(2)
  var from = 0
  var unindexedElements = 0
  val size = 18
  val Index = "full_movie"

  def createWorkers(numberOfWorkers: Int): Seq[ActorRef] = {
    (0 until numberOfWorkers) map (_ => context.actorOf(Props(new MovieEnricherWorker(self, enricherService))))
  }

  private def startWorkers() = workers.foreach(_ ! StartWorking)

  def waiting: Receive = {
    case StartEnrichment =>
      sender() ! "Start enriching movies ..."
      Logger.info("Start enriching movies ...")

      for {
        _ <- upsertIndex(FullMovieIndexDefinition.esIndexConfiguration)
        movies <- searchService.getMovies(from, size)
      } yield {
        batch = Batch(movies.toVector)
        incompleteTasks = batch.size
        context.become(busy)

        if (!batch.isDone) startWorkers()
      }
    case FetchNextBatch =>
      from = from + size
      for {
        movies <- searchService.getMovies(from, size)
      } yield {
        batch = Batch(movies.toVector)
        context.become(busy)
        if (!batch.isDone) startWorkers()
        else {
          Logger.warn("System Shutting Down")
          Logger.error(s"Movies Not Indexed : $failures")
          context.system.terminate()
        }
      }

  }

  def busy: Receive = {
    case FullMovieIndexed(indexed, totalRetries) =>
      failures = totalRetries
      if (indexed) Logger.info(s"Full Movie indexed. Movies indexed: $incompleteTasks")
      else {
        unindexedElements = unindexedElements + 1
        Logger.error(s"Movie Not Indexed, Error ... Moving On ...")
      }
      incompleteTasks = incompleteTasks + 1
    case GetMovieDetails =>
      batch.next.fold({
        Logger.info(s"No more movie to enrich")
        context.become(waiting)
        sender() ! StartWorkingAgain
      }) {
        case (id, nextIds) =>
          Logger.info(s"Sending id $id to worker")
          sender() ! FetchMovieDetails(id)
          batch = nextIds
      }
  }

  override def receive = waiting

}

object MovieEnricher {
  final val Name = "movie-enricher"

  val props: Props = Props[MovieEnricher]

  case object StartEnrichment

  case object GetMovieDetails

  case object FetchNextBatch

  case class FullMovieIndexed(indexed: Boolean, totalRetries: Int)

  case class FetchMovieDetails(movie: Movie)

  @Singleton
  class MovieEnricherWorker @Inject()(
                                       indexer: ActorRef,
                                       enricherService: EnricherService
                                     ) extends Actor with EsClient {

    var retry = 0
    implicit var totalRetries = 0
    val Index = "full_movie"
    val EsType = "movie"

    def onFailureIndexingRetry(retryMessage: Object, notifySupervisorMessage: Object, nextElementMessage: Object): Unit = {
      if (retry <= 5) {
        retry = retry + 1
        totalRetries = totalRetries + 1
        self ! retryMessage
      } else {
        retry = 0
        indexer ! notifySupervisorMessage
        indexer ! nextElementMessage
      }
    }

    def waiting: Receive = {
      case StartWorking =>
        Logger.info(s"Indexing movie details started ...")
        context.become(working)
        indexer ! GetMovieDetails
    }

    def working: Receive = {
      case FetchMovieDetails(movie) =>
        Logger.info(s"Fetching extra details for movie: ${movie.title}")
        val eventuallyId = enricherService.getMovieIdFromExternalId(movie.externalId)
        eventuallyId.map{
          case Some(id) =>
            for {
              maybeMovieDetails <- enricherService.getMovieDetailsFromId(id)
            } yield {
              maybeMovieDetails match {
                case Some(movieDetails) =>
                  Logger.info(s"SUCCESS on retrieving extra details for movie ${movieDetails.original_title}")
                  self ! IndexFullMovie(FullMovie.fromMovieDetails(movie, movieDetails))
                case None =>
                  onFailureIndexingRetry(FetchMovieDetails(movie), FullMovieIndexed(indexed = false, totalRetries), GetMovieDetails)
              }
            }
          case None =>
            Logger.info(s"NO_ID_FOUND on retrieving extra details for movie ${movie.title}")
            onFailureIndexingRetry(FetchMovieDetails(movie), FullMovieIndexed(indexed = false, totalRetries), GetMovieDetails)
        }
      case IndexFullMovie(fullMovie) =>
        Logger.info(s"Indexing Movie ${fullMovie.title}")
        for {
          hasFailure <- bulkIndex(Index, EsType, fullMovie).map(_.hasFailures)
        } yield {
          if (hasFailure) {
            onFailureIndexingRetry(IndexFullMovie(fullMovie), FullMovieIndexed(indexed = false, totalRetries), GetMovieDetails)
          } else {
            indexer ! FullMovieIndexed(indexed = true, totalRetries)
            indexer ! GetMovieDetails
          }
        }
      case StartWorkingAgain =>
        Logger.info("WORKER ASKING FOR DUTY !!!!")
        context.become(waiting)
    }

    override def receive = waiting

  }

}

object MovieEnricherWorker {

  case object StartWorking

  case object StartWorkingAgain

  case class IndexFullMovie(fullMovie: FullMovie)
}