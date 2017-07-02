package indexer

import javax.inject.{Inject, Singleton}

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import indexer.MovieEnricher._
import indexer.MovieEnricherWorker._
import models.themoviedb.MovieDetails
import play.api.libs.ws.WSClient
import play.api.{Configuration, Logger}
import services.{EnricherService, SearchService}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


@Singleton
class MovieEnricher @Inject()(
                               wSClient: WSClient,
                               searchService: SearchService,
                               configuration: Configuration,
                               enricherService: EnricherService
                             ) extends Actor with EsClient with ActorLogging {

  var incompleteTasks = 0
  var failures = 0
  var batches: Batches[Int] = Batches.empty[Int]
  val workers: Seq[ActorRef] = createWorkers(3)
  var from = 0
  var unindexedElements = 0
  val size = 38
  val Index = "movies_details"

  def createWorkers(numberOfWorkers: Int): Seq[ActorRef] = {
    (0 until numberOfWorkers) map (_ => context.actorOf(Props(new MovieEnricherWorker(self, enricherService))))
  }

  private def startWorkers() = workers.foreach(_ ! StartWorking)

  def waiting: Receive = {
    case StartEnrichment =>
      sender() ! "Start enriching movies ..."
      Logger.info("Start enriching movies ...")

      for {
        indexExists <- ensureIndexExists(Index)
        _ <- if (indexExists) eventuallyDeleteIndex(Index).map( _ => ()) else Future.successful(()) // Todo add case index exists
        ids <- searchService.getMoviesIds(from, size)
      } yield {
        batches = Batches(ids.flatten.toVector)
        incompleteTasks = batches.size
        context.become(busy)

        if (!batches.isDone) startWorkers()
      }
    case FetchNextBatch =>
      from = from + size
      for {
        maybeIds <- searchService.getMoviesIds(from, 30)
      } yield {
        batches = Batches(maybeIds.flatten.toVector)
        context.become(busy)
        if (!batches.isDone) startWorkers()
        else {
          Logger.warn("System Shutting Down")
          Logger.error(s"Total failures : $failures")
          context.system.terminate()
        }
      }



  }

  def busy: Receive = {
    case MovieDetailsIndexed(indexed, totalRetries) =>
      failures = totalRetries
      if (indexed) Logger.info(s"Movie details indexed. Remaining movies : $incompleteTasks")
      else {
        unindexedElements = unindexedElements + 1
        Logger.error(s"Movie Not Indexed, Error ... Moving On ...")
      }
      incompleteTasks = incompleteTasks - 1
    case GetMovieDetails =>
      batches.next.fold({
        Logger.info(s"No more movie to enrich")
        context.become(waiting)
        sender() ! StartWorkingAgain
      }) {
        case (id, nextIds) =>
          Logger.info(s"Sending id $id to worker")
          sender() ! FetchMovieDetails(id)
          batches = nextIds
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

  case class MovieDetailsIndexed(indexed: Boolean, totalRetries: Int)

  case class FetchMovieDetails(id: Int)

  @Singleton
  class MovieEnricherWorker @Inject()(
                                       indexer: ActorRef,
                                       enricherService: EnricherService
                                     ) extends Actor with EsClient with ActorLogging {

    var retry = 0
    implicit var totalRetries = 0
    val Index = "movies_details"
    val EsType = "movie"

    def onFailureRetry(retryMessage: Object, notifyFailureMessage: Object, nextElementMessage: Object): Unit = {
      if (retry <= 5) {
        retry = retry + 1
        totalRetries = totalRetries + 1
        self ! retryMessage
      } else {
        retry = 0
        indexer ! notifyFailureMessage
        indexer ! nextElementMessage
      }
    }

    def waiting: Receive = {
      case StartWorking =>
        log.info(s"Indexing movie details started ...")
        context.become(working)
        indexer ! GetMovieDetails
    }

    def working: Receive = {
      case FetchMovieDetails(id) =>
        Logger.info(s"Fetching extra details for movie: $id")
        for {
          maybeMovieDetails <- enricherService.getMovieDetailsFromId(id)
        } yield {
          maybeMovieDetails.fold({
            onFailureRetry(FetchMovieDetails(id), MovieDetailsIndexed(indexed = false, totalRetries), GetMovieDetails)
          }) {
            case (movieDetails) =>
              Logger.info(s"SUCCESS on retrieving extra details for movie ${movieDetails.original_title}")
              self ! IndexMovieDetails(movieDetails)
          }
        }
      case IndexMovieDetails(movieDetails) =>
        Logger.info(s"Indexing Movie ${movieDetails.original_title}")
        for {
          hasFailure <- bulkIndex(Index, EsType, movieDetails).map(response => response.hasFailures)
        } yield {
          if (hasFailure) {
            onFailureRetry(IndexMovieDetails(movieDetails), MovieDetailsIndexed(indexed = false, totalRetries), GetMovieDetails)
          } else {
            indexer ! MovieDetailsIndexed(indexed = true, totalRetries)
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

  case class IndexMovieDetails(movieDetails: MovieDetails)

}