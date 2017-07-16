package controllers

import javax.inject.{Inject, Named, Singleton}

import akka.actor.{ActorRef, ActorSystem}
import indexer.MovieEnricher.StartEnrichment
import indexer.MovieIndexer.StartIndexing
import indexer.{MovieEnricher, MovieIndexer, MovieSuggestionIndexer}
import play.api.mvc.{Action, Controller}
import services.EnricherService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._


@Singleton
class IndexerController @Inject()(@Named(MovieEnricher.Name) movieEnricher: ActorRef,
                                  @Named(MovieIndexer.Name) movieIndexer: ActorRef,
                                  @Named(MovieSuggestionIndexer.Name) movieSuggestionIndexer: ActorRef,
                                  enricherService: EnricherService,
                                  system: ActorSystem) extends Controller {

  def enrichMovies = Action {
    enricherService.getAllIds
    Ok("Enriching without backpressure")
  }

  def startEnrichingMovies = Action {
    system.scheduler.scheduleOnce(
      5.seconds, movieEnricher, StartEnrichment
    )

    system.scheduler.schedule(
      15.seconds, 10.seconds, movieEnricher, MovieEnricher.FetchNextBatch
    )
    Ok("Starting Enrichment")
  }

  def startIndexingMovies = Action {
    system.scheduler.scheduleOnce(
      5.seconds, movieIndexer, StartIndexing
    )
    system.scheduler.schedule(
      10.seconds, 1.seconds, movieIndexer, MovieIndexer.RequestNextBatch
    )
    Ok("Starting Indexing")
  }

  def startIndexingSuggestions = Action {
    system.scheduler.scheduleOnce(
      5.seconds, movieSuggestionIndexer, MovieSuggestionIndexer.StartIndexing
    )

    Ok("Starting Indexing Suggestions")
  }

}
