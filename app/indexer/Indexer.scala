package indexer

import akka.actor.ActorSystem
import indexer.MovieIndexer.StartIndexing
import play.api.Logger

object Indexer extends App with EsClient {

  Logger.info(s"Indexation starting ...")

  val movieIndexer = ActorSystem().actorOf(MovieIndexer.props, "movie-indexer")

  movieIndexer ! StartIndexing

}
