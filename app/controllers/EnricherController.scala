package controllers

import javax.inject.{Inject, Named, Singleton}

import akka.actor.{ActorRef, ActorSystem}
import indexer.MovieEnricher.{RequestNextBatch, StartEnrichment}
import indexer.MovieIndexer.{StartIndexing, RequestNextBatch}
import indexer.{MovieEnricher, MovieIndexer}
import play.api.mvc.{Action, Controller}
import services.EnricherService

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._


@Singleton
class EnricherController @Inject()(@Named(MovieEnricher.Name) movieEnricher: ActorRef,
                                   @Named(MovieIndexer.Name) movieIndexer: ActorRef,
                                   enricherService: EnricherService,
                                   system: ActorSystem) extends Controller {

  def enrichMovies = Action {
    enricherService.getAllIds
    Ok("Enriching without backpressure")
  }

  def enrichBlocking = Action {
    enricherService.getAllIdsBlocking
    Ok("Enriching without backpressure")
  }

  def startEnrichment = Action {
    system.scheduler.scheduleOnce(
      5 seconds, movieEnricher, StartEnrichment
    )

    system.scheduler.schedule(
      10 seconds, 10 seconds, movieEnricher, MovieEnricher.RequestNextBatch
    )
    Ok("Starting Enrichment")
  }

  def startIndexing = Action {
    system.scheduler.scheduleOnce(
      5 seconds, movieIndexer, StartIndexing
    )
    system.scheduler.schedule(
      10 seconds, 1 seconds, movieIndexer, MovieIndexer.RequestNextBatch
    )
    Ok("Starting Indexing")
  }

}
