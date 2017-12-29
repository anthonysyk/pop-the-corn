import akka.actor.ActorSystem
import main.scala.{DiscoveredMovieIndexer, DiscoveredMovieSupervisor}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.util.Try

object LaunchDiscoveryIndexer {

  def main(args: Array[String]): Unit = {

    val system = ActorSystem()

    val discoveredMovieIndexer = system.actorOf(DiscoveredMovieIndexer.props, "discovered_movie_indexer")

    args.sliding(2,2).flatten match {
      case ("--year", year: String) if Try(year.toInt).isSuccess =>
        system.scheduler.schedule(2.seconds, 10.seconds, discoveredMovieIndexer, DiscoveredMovieSupervisor.FetchNextBatch(year.toInt))
      case _ => println("Error please use parameter --year"); System.exit(0)
    }

  }

}

object LauchEnricherIndexer {

  def main(args: Array[String]): Unit = {

    val system = ActorSystem()

    val enricheredMovieIndexer = system.actorOf(EnricherMovieIndexer.props, "enriched_movie_indexer")

    system.scheduler.schedule(2.seconds, 10.seconds, enricheredMovieIndexer, EnricherMovieSupervisor.FetchNextBatch)

  }

}
