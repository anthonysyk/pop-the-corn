import akka.actor.ActorSystem
import main.scala.{DiscoveredMovieIndexer, DiscoveredMovieSupervisor}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

object LaunchDiscoveryIndexer {

  def main(args: Array[String]): Unit = {

    val system = ActorSystem()

    val discoveredMovieIndexer = system.actorOf(DiscoveredMovieIndexer.props, "discovered_movie_indexer")

    system.scheduler.schedule(2.seconds, 10.seconds, discoveredMovieIndexer, DiscoveredMovieSupervisor.FetchNextBatch)

  }

}

object LauchEnricherIndexer {

  def main(args: Array[String]): Unit = {

    val system = ActorSystem()

    val enricheredMovieIndexer = system.actorOf(EnricherMovieIndexer.props, "enriched_movie_indexer")

    system.scheduler.schedule(2.seconds, 10.seconds, enricheredMovieIndexer, EnricherMovieSupervisor.FetchNextBatch)

  }

}
