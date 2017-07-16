package indexer

import javax.inject.{Inject, Singleton}

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import indexer.MovieIndexer._
import indexer.MovieWorker._
import indexer.mapping.MovieIndexDefinition
import models.kaggle.Movie
import play.api.{Logger}
import services.EnricherService

import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class MovieIndexer @Inject()(
                              enricherService: EnricherService
                            ) extends Actor with EsClient with ReadCsvHelper with ActorLogging {

  var incompleteTasks = 0
  var data = Vector.empty[Movie]
  var batch: Batch[Movie] = Batch.empty[Movie]
  val workers: Seq[ActorRef] = createWorkers(1)
  var errors = 0
  val Index: String = "movies_index"

  private def startWorkers() = workers.foreach(_ ! StartWorking)

  def createWorkers(numberOfWorkers: Int): Seq[ActorRef] = {
    (0 until numberOfWorkers) map (_ => context.actorOf(Props(new MovieWorker(self, enricherService))))
  }

  def waiting: Receive = {
    case StartIndexing =>
      Logger.info("Retrieving Movies from csv source")
      for {
        _ <- upsertIndex(MovieIndexDefinition.esIndexConfiguration)
        movies <- serializeMoviesFromCsv
      } yield {
        data = movies.toVector
        batch = Batch(data.take(3))
        incompleteTasks = batch.size
        context.become(busy)
        Logger.warn(s"MOVIES TO INDEX : $incompleteTasks ")
        if (!batch.isDone) startWorkers()
      }
    case RequestNextBatch =>
      Logger.info("Fetching next batch")
      Logger.warn(s"$errors for the moment")
      data = data.drop(3)
      batch = Batch(data.take(3))
      incompleteTasks = batch.size
      context.become(busy)

      if (!batch.isDone) startWorkers()
      else {
        Logger.warn("System shutting down ... All data processed")
        context.system.terminate()
      }

  }

  def busy: Receive = {
    case MovieIndexed(indexed) =>
      if (indexed) {
        errors = errors + 1
        Logger.info(s"Movie indexed. Remaining Movies : $incompleteTasks")
      }
      else Logger.error(s"Movie NOT indexed. Moving on Remaining Movies : $incompleteTasks")
      incompleteTasks = incompleteTasks - 1
    case GetMovie =>
      batch.next.fold({
        Logger.info("No more movies to index")
        context.become(waiting)
        sender() ! StartWorkingAgain
      }) {
        case (movie, remainingMovies) =>
          Logger.info(s"Sending movie ${movie.title} to a worker")
          sender() ! EnrichElement(movie)
          batch = remainingMovies
      }
  }

  override def receive: Receive = waiting

}

object MovieIndexer {
  final val Name = "movie-indexer"

  var retry = 0
  val props: Props = Props[MovieIndexer]

  case class MovieIndexed(indexed: Boolean)

  case object GetMovie

  case object StartIndexing

  case object RequestNextBatch

  @Singleton
  class MovieWorker @Inject()(
                               indexer: ActorRef,
                               enricherService: EnricherService
                             ) extends Actor with EsClient with ActorLogging {

    val index: String = "movies_index"
    val EsType: String = "movie"

    def waiting: Receive = {
      case StartWorking =>
        log.info("Indexing Movies Started ...")
        context.become(working)
        indexer ! GetMovie
    }

    def working: Receive = {
      case EnrichElement(movie) =>
        for {
         maybeId <- enricherService.getMovieIdFromExternalId(movie.externalId)
        } yield {
          self ! IndexElement(movie.copy(id = maybeId))
        }
      case IndexElement(movie) =>
        Logger.info(s"Indexing movie: ${movie.title}")
        for {
          hasFailure <- bulkIndex(index, EsType, movie).map(response => response.hasFailures)
        } yield {
          if (hasFailure) {
            Logger.info("Too many tries, moving on ...")
            indexer ! MovieIndexed(false)
            indexer ! GetMovie
          } else {
            Logger.info("SUCCESS")
            indexer ! MovieIndexed(true)
            indexer ! GetMovie
          }
        }
      case StartWorkingAgain =>
        Logger.info("Calling for more tasks")
        context.become(waiting)
    }

    override def receive: Receive = waiting
  }

}

object MovieWorker {

  case object StartWorking

  case object StartWorkingAgain

  case class IndexElement(movie: Movie)

  case class EnrichElement(movie: Movie)

}