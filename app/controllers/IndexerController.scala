package controllers

import javax.inject.{Inject, Named, Singleton}

import akka.actor.{ActorRef, ActorSystem}
import indexer.MovieEnricher.StartEnrichment
import indexer.{MovieEnricher, MovieSuggestionIndexer}
import play.api.mvc.{Action, Controller}
import services.EnricherService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._


@Singleton
class IndexerController @Inject()(@Named(MovieEnricher.Name) movieEnricher: ActorRef,
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

  def startIndexingSuggestions = Action {
    system.scheduler.scheduleOnce(
      5.seconds, movieSuggestionIndexer, MovieSuggestionIndexer.StartIndexing
    )

    Ok("Starting Indexing Suggestions")
  }

}
