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


@Singleton
class MovieEnricher @Inject()(
                               wSClient: WSClient,
                               searchService: SearchService,
                               configuration: Configuration,
                               enricherService: EnricherService
                             ) extends Actor with EsClient with ActorLogging {

  var incompleteTasks = 0
  var batches: Batches[String] = Batches.empty[String]
  val workers: Seq[ActorRef] = createWorkers(3)
  val config = configuration.getString("my.config").getOrElse("none")
  var from = 0
  var vector = Vector.empty[String]

  def createWorkers(numberOfWorkers: Int): Seq[ActorRef] = {
    (0 until numberOfWorkers) map (_ => context.actorOf(Props(new MovieEnricherWorker(self, enricherService))))
  }

  private def startWorkers() = workers.foreach(_ ! StartWorking)

  def waiting: Receive = {
    case StartEnrichment =>
      sender() ! config
      log.info("Start enriching movies ...")

      for {
        externalIds <- searchService.getMoviesExternalIds(0, 100)
      } yield {
        vector = externalIds.toVector
        batches = Batches(vector.take(21))
        incompleteTasks = batches.size
        context.become(busy)

        if (!batches.isDone) startWorkers()
      }
    case RequestNextBatch =>
      vector = vector.drop(21)
      batches = Batches(vector.take(21))
      context.become(busy)
      if (!batches.isDone) startWorkers()
      else {
        Logger.warn("System Shutting Down")
        context.system.terminate()
      }

  }

  def busy: Receive = {
    case MovieDetailsIndexed(indexed) =>
      if (indexed) Logger.info(s"Movie details indexed. Remaining movies : $incompleteTasks")
      else Logger.error(s"Movie Not Indexed, Error ... Moving On ...")
      incompleteTasks = incompleteTasks - 1
    case GetMovieDetails =>
      batches.next.fold({
        Logger.info(s"No more movie to enrich")
        context.become(waiting)
        sender() ! StartWorkingAgain
      }) {
        case (id, nextIds) =>
          Logger.info(s"Sending id $id to worker")
          sender() ! RetrieveId(id)
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

  case object RequestNextBatch

  case class MovieDetailsIndexed(indexed: Boolean)

  case class DoNothing(batches: Batches[String])

  var retry = 0
  var totalRetries = 0

  @Singleton
  class MovieEnricherWorker @Inject()(
                                       indexer: ActorRef,
                                       enricherService: EnricherService
                                     ) extends Actor with EsClient with ActorLogging {

    def waiting: Receive = {
      case StartWorking =>
        log.info(s"Indexing movie details started ...")
        context.become(working)
        indexer ! GetMovieDetails
    }

    def working: Receive = {
      case RetrieveId(externalId) =>
        Logger.info(s"Indexing movie: $externalId")
        for {
          maybeId <- enricherService.getMovieIdFromExternalId(externalId)
        } yield {
          if(maybeId.isDefined) {
            Logger.error("Critical Failure ... Moving on ...")
            indexer ! MovieDetailsIndexed(false)
            indexer ! GetMovieDetails
          } else {
            Logger.info("SUCCESS RetrieveId")
            indexer ! MovieDetailsIndexed(true)
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

  case class RetrieveId(externalId: String)

  case class IndexMovieDetails(movieDetails: MovieDetails)

}