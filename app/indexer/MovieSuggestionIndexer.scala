package indexer

import javax.inject.{Inject, Singleton}

import akka.actor.{Actor, ActorRef}
import indexer.MovieSuggestionIndexer.{FetchNextBatch, GetElement, MovieSuggestionWorker, NotifySupervisor, StartIndexing}
import indexer.MovieSuggestionWorker.{IndexSuggestion, StartWorking, StartWorkingAgain}
import indexer.mapping.SuggestIndexDefinition
import models.{FullMovie, Suggestion}
import play.api.Logger
import play.api.libs.json.Json
import services.SearchService

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class MovieSuggestionIndexer @Inject()(
                                        searchService: SearchService
                                      ) extends Actor with EsClient with AkkaHelper {
  var incompleteTasks = 0
  var completeTasks = 0
  var failures = 0
  var batches: Batches[FullMovie] = Batches.empty[FullMovie]
  val workers: Seq[ActorRef] = createWorkers[MovieSuggestionWorker](context, 3, { () => new MovieSuggestionWorker(self) })
  var from = 0
  val size = 1000

  override def receive: Receive = waiting

  def waiting: Receive = {
    case StartIndexing =>
      Logger.info("Indexing start ...")
      for {
        _ <- upsertIndex(SuggestIndexDefinition.esIndexConfiguration)
        movies <- fetchNextBatch(from, size)
      } yield {
        batches = Batches(movies.toVector)
        context.become(working)
        if (!batches.isDone) startWorkers()
      }
  }

  def working: Receive = {
    case NotifySupervisor(isIndexed) =>
      if (isIndexed) {
        incompleteTasks = incompleteTasks + 1
        Logger.error(s"ERROR: $incompleteTasks elements not indexed")
      } else {
        completeTasks = completeTasks + 1
        Logger.info(s"SUCCESS: $completeTasks elements indexed")
      }
    case GetElement =>
      batches.next.foreach {
        case (fullMovie, remainingMovies) =>
          batches = remainingMovies
          if(remainingMovies.isDone) {
            from = from + size
            for {
              movies <- fetchNextBatch(from, size)
            } yield {
              startWorkersAgain()
              batches = Batches(movies.toVector)
              if (!batches.isDone) startWorkers() else shutdownSystem()
            }
          }
          sender() ! IndexSuggestion(fullMovie.suggestion)
      }
  }

  def fetchNextBatch(from: Int, size: Int): Future[Seq[FullMovie]] = {
    Logger.info("Fetching Batch")
    searchService.getFullMovies(from, size)
  }

  def shutdownSystem() = {
    Logger.info("Shutting Down")
    Logger.warn(s"$completeTasks elements indexed")
    Logger.warn(s"$incompleteTasks elements not indexed")
    context.system.terminate()
  }

  private def startWorkers() = workers.foreach(_ ! StartWorking)
  private def startWorkersAgain() = workers.foreach(_ ! StartWorkingAgain)

}

object MovieSuggestionIndexer {
  final val Name = "movie-suggestion"

  val Index = "suggest_movies"
  val EsType = "suggest"

  case object StartIndexing

  case object FetchNextBatch

  case object GetElement

  case class NotifySupervisor(isIndexed: Boolean)

  @Singleton
  class MovieSuggestionWorker @Inject()(indexer: ActorRef) extends Actor with EsClient {

    override def receive: Receive = waitingForDuty

    def waitingForDuty: Receive = {
      case StartWorking =>
        Logger.info("Worker Activated ...")
        indexer ! GetElement
        context.become(working)
    }

    def working: Receive = {
      case IndexSuggestion(suggestion) =>

        val suggestionJsonObject = Json.obj(
          "suggest" -> Json.toJson(suggestion),
          "ngram" -> suggestion.title,
          "ngram_folded" -> suggestion.title,
          "votes" -> suggestion.votes
        )

        bulkIndex(Index, EsType, suggestionJsonObject).map { response =>
          indexer ! GetElement
          indexer ! NotifySupervisor(response.hasFailures)
        }
      case StartWorkingAgain =>
        Logger.info("Worker asking for DUTY !!!!")
        context.become(waitingForDuty)
    }

  }

}

object MovieSuggestionWorker {

  case object StartWorking

  case object StartWorkingAgain

  case class IndexSuggestion(suggestion: Suggestion)

}
