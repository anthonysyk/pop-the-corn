package main.scala

import akka.actor.{ActorRef, Props}
import io.circe._
import io.circe.generic.auto._
import io.circe.parser._
import main.scala.DiscoveredMovieWorker.IndexDiscoveredMovie
import models.{Batch, DiscoveredMovie}
import ptc.libraries.{AkkaHelper, DiscoveredMovieIndexDefinition, EsClient}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


object DiscoveredMovieIndexer {

  def createDiscoverUrl(page: Int, year: Int) = {
    s"https://api.themoviedb.org/3/discover/movie?api_key=5a12f551fdaa854030d1bea7e45217a2&language=en-US&page=$page&year=$year"
  }

  def getDiscoveredMovies(page: Int, year: Int): Future[Seq[DiscoveredMovie]] = {
    Future.fromTry(WebClient.doGet(createDiscoverUrl(page, year), Map.empty[String, String]))
      .map { result =>
        parse(result).right.toOption.getOrElse(Json.Null).hcursor.downField("results").as[Seq[DiscoveredMovie]].right.toOption match {
          case Some(results) => results
          case None => Nil
        }
      }
  }

  def getNumberOfPagesForThisYear(year: Int): Future[Option[Int]] = {
    Future.fromTry(WebClient.doGet(DiscoveredMovieIndexer.createDiscoverUrl(1, year), Map.empty[String, String])
      .map { result =>
        parse(result).right.toOption.getOrElse(Json.Null).hcursor.downField("total_pages").as[Int].right.toOption
      })
  }

  val props = Props(new DiscoveredMovieSupervisor)

}

class DiscoveredMovieSupervisor extends EsClient with AkkaHelper {

  var incompleteTasks = 0
  var failures = 0
  var batch: Batch[DiscoveredMovie] = Batch.empty[DiscoveredMovie]
  implicit val workers: Seq[ActorRef] = createWorkers(2, Props(new DiscoveredMovieWorker(self)))
  var unindexedElements = 0
  val size = 35
  var page = 1
  var numberOfPages: Future[Int] = Future.successful(-1)

  def updatePage(): Future[Unit] = {
    numberOfPages.map {
      case -1 =>
        numberOfPages = DiscoveredMovieIndexer.getNumberOfPagesForThisYear(2016).map(_.getOrElse(0))
        logger.info(s"récupération du nombre de pages total")
      case value if page >= value =>
        logger.info(s"$page pages indexees sur $value")
        logger.error(s"$failures films non indexés")
        context.system.terminate()
      case value if page < value =>
        page = page + 1
        logger.info(s"$page pages indexees sur $value")
    }
  }

  def receive: Receive = {
    case DiscoveredMovieSupervisor.FetchNextBatch =>

      logger.info("Fetch new batch")

      updatePage()

      val eventuallyDiscoveredMovies = Future.sequence(
        (page to page + size).map(p => DiscoveredMovieIndexer.getDiscoveredMovies(p, 2016))
      ).map(_.flatten.toSeq)

      page = page + size

      for {
        _ <- if (page == -1) upsertIndex(DiscoveredMovieIndexDefinition.esIndexConfiguration).map {
          case true => logger.warning("Index Created")
          case false => logger.error("Index not created")
        }
        else Future()
        discoveredMovies <- eventuallyDiscoveredMovies
      } yield {
        batch = Batch(discoveredMovies.toVector)
        incompleteTasks = batch.size
        context.become(busy)
        if (!batch.isDone) startWorkers(DiscoveredMovieWorker.StartWorking)
      }
  }

  def busy: Receive = {
    case DiscoveredMovieSupervisor.NotifySupervisor(isIndexed) =>
      if(isIndexed) logger.info("Movie indexed correctly")
      else {
        failures = failures + 1
        logger.error("Movie not indexed")
      }
    case DiscoveredMovieSupervisor.GetDiscoveredMoviesPage =>
      batch.next match {
        case Some((discoveredMovie, nextBatch)) =>
          logger.info(s"Sending movie: ${discoveredMovie.title.getOrElse("Unkown Movie")} to worker")
          sender() ! DiscoveredMovieWorker.IndexDiscoveredMovie(discoveredMovie)
          batch = nextBatch
        case None =>
          logger.info("Batch Indexed :: Asking for next batch")
          context.become(receive)
          sender() ! DiscoveredMovieWorker.WaitForNextBatch
      }

  }

}

object DiscoveredMovieSupervisor {

  sealed trait SupervisorMessage

  case object GetDiscoveredMoviesPage extends SupervisorMessage

  case object FetchNextBatch extends SupervisorMessage

  case class NotifySupervisor(isIndexed: Boolean) extends SupervisorMessage

}

class DiscoveredMovieWorker(supervisor: ActorRef) extends EsClient with AkkaHelper {

  var retry = 0

  def receive: Receive = {
    case DiscoveredMovieWorker.StartWorking =>
      logger.info(s"Indexing movie details started ...")
      context.become(working)
      supervisor ! DiscoveredMovieSupervisor.GetDiscoveredMoviesPage
  }

  def working: Receive = {
    case IndexDiscoveredMovie(discoveredMovie) =>
      logger.info(s"Indexing Movie ${discoveredMovie.title.getOrElse("Unkown Movie")}")
      for {
        hasFailure <- bulkIndex(DiscoveredMovieIndexDefinition.IndexName, DiscoveredMovieIndexDefinition.TypeName, discoveredMovie).map(_.hasFailures)
      } yield {
        if (hasFailure && retry < 5) {
          retry = retry + 1
          logger.error(s"Error while indexing movie ${discoveredMovie.title.getOrElse("Unkown Movie")}")
          self ! IndexDiscoveredMovie(discoveredMovie)
        } else {
          if (retry == 5) {
            supervisor ! DiscoveredMovieSupervisor.NotifySupervisor(isIndexed = false)
          } else supervisor ! DiscoveredMovieSupervisor.NotifySupervisor(isIndexed = true)
          supervisor ! DiscoveredMovieSupervisor.GetDiscoveredMoviesPage
          retry = 0
        }
      }
    case DiscoveredMovieWorker.WaitForNextBatch =>
      logger.info("WORKER ASKING FOR DUTY !!!!")
      context.become(receive)
  }

}

object DiscoveredMovieWorker {

  sealed trait WorkerMessage

  case object StartWorking extends WorkerMessage

  case object WaitForNextBatch extends WorkerMessage

  case class IndexDiscoveredMovie(discoveredMovie: DiscoveredMovie) extends WorkerMessage

}

