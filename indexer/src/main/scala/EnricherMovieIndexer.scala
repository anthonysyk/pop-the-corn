import akka.actor.{ActorRef, Props}
import io.circe._
import io.circe.generic.auto._
import io.circe.parser._
import main.scala.WebClient
import models.{Batch, DiscoveredMovie, TmdbMovie}
import ptc.libraries.{AkkaHelper, DiscoveredMovieIndexDefinition, EsClient, MovieIndexDefinition}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

// TODO: Rajouter des tests mokkés pour les indexers

object EnricherMovieIndexer extends EsClient {

  def createFetchDetailsUrl(id: Int) = {
    s"https://api.themoviedb.org/3/movie/$id?api_key=5a12f551fdaa854030d1bea7e45217a2&language=en-US"
  }

  def getMovieId(from: Int, size: Int): Future[Seq[DiscoveredMovie]] = {
    client execute {
      search in DiscoveredMovieIndexDefinition.IndexName -> DiscoveredMovieIndexDefinition.TypeName from from size size
    }
  }.map { searchResponse =>
    parseSearchResponse(searchResponse.toString).flatMap(_.as[DiscoveredMovie].right.toOption)
  }

  def getMovieDetailsById(id: Int): Option[TmdbMovie] = {
    WebClient.doGet(createFetchDetailsUrl(id), Map.empty[String, String]).map { response =>
      parse(response).right.toOption.getOrElse(Json.Null).as[TmdbMovie].right.toOption
    }.toOption
  }.flatten

  def tryGettingMovieDetailsById(id: Int, retry: Int = 5): Option[TmdbMovie] = {
    getMovieDetailsById(id) match {
      case Some(movie) => Some(movie)
      case _ if retry == 1 => println(s"ERROR: retrieving details for movie $id --nbTries = ${6 - retry}"); None
      case _ => tryGettingMovieDetailsById(id, retry - 1)
    }
  }

  val props = Props(new EnricherMovieSupervisor)
}

class EnricherMovieSupervisor extends EsClient with AkkaHelper {

  var incompleteTasks = 0
  var failures = 0
  var batch: Batch[Int] = Batch.empty[Int]
  implicit val workers: Seq[ActorRef] = createWorkers(2, Props(new EnricherMovieWorker(self)))
  var unindexedElements = 0
  val size = 20
  var from = 0

  def receive: Receive = {
    case EnricherMovieSupervisor.FetchNextBatch =>
      val eventuallyMoviesDiscovered: Future[Seq[DiscoveredMovie]] = EnricherMovieIndexer.getMovieId(from, size)

      for {
        _ <- if (from == 0) updateIndex(MovieIndexDefinition.esIndexConfiguration).map {
          case true => println("Index Created")
          case false => println("Error : Index not created")
        } else Future()
        movieDiscovered <- eventuallyMoviesDiscovered
      } yield {
        batch = Batch(movieDiscovered.flatMap(_.id).toVector)
        //        batch = Batch(Vector(473814))
        println(s"Batch de ${batch.size} films disponible")
        incompleteTasks = batch.size
        context.become(busy)
        if (!batch.isDone) startWorkers(EnricherMovieWorker.StartWorking) else {
          println(s"$failures films non indexés")
          context.system.terminate()
        }
        from = from + size
      }
  }

  def busy: Receive = {
    case EnricherMovieSupervisor.NotifySupervisor(isIndexed) =>
      if (isIndexed) println("SUCCESS: Movie indexed correctly")
      else {
        failures = failures + 1
        println("ERROR : Movie not indexed")
      }
    case EnricherMovieSupervisor.GetMovieId =>
      batch.next match {
        case Some((id, nextBatch)) =>
          println(s"Sending movie: $id to worker")
          sender() ! EnricherMovieWorker.IndexMovie(id)
          batch = nextBatch
        case None =>
          println("Batch Indexed :: Asking for next batch")
          println(s"${from - failures} films indexés")
          context.become(receive)
          sender() ! EnricherMovieWorker.WaitForNextBatch
      }

  }

}

object EnricherMovieSupervisor {

  sealed trait SupervisorMessage

  case object FetchNextBatch extends SupervisorMessage

  case class NotifySupervisor(isIndexed: Boolean) extends SupervisorMessage

  case object GetMovieId

}

class EnricherMovieWorker(supervisor: ActorRef) extends EsClient with AkkaHelper {

  def receive: Receive = {
    case EnricherMovieWorker.StartWorking =>
      println(s"Indexing movie details started ...")
      context.become(working)
      supervisor ! EnricherMovieSupervisor.GetMovieId
  }

  // Retry with recursion
  // no tail recursion here because no risk of blowing the stack (Futures operates on multiple stacks)
  def indexMovie(movie: TmdbMovie, retry: Int = 5): Boolean = {
    upsertDocument(MovieIndexDefinition.IndexName, MovieIndexDefinition.TypeName, movie, movie.id.getOrElse(0)) match {
      case isIndexed if isIndexed => true
      case _ if retry == 1 => println(s"ERROR: while indexing movie ${movie.title.getOrElse("Unkown Movie")} --nbTries = ${6 - retry}"); false
      case _ => indexMovie(movie, retry - 1); false
    }
  }

  def working: Receive = {
    case EnricherMovieWorker.IndexMovie(id) =>
      println(s"movie $id received")
      val maybeMovie = EnricherMovieIndexer.tryGettingMovieDetailsById(id)
      println(maybeMovie)
      val isIndexed = maybeMovie match {
        case Some(movie) => indexMovie(movie)
        case None => false
      }
      println(s"Indexing Movie ${maybeMovie.flatMap(_.title).getOrElse("Unkown Movie")}")
      supervisor ! EnricherMovieSupervisor.NotifySupervisor(isIndexed = isIndexed)
      supervisor ! EnricherMovieSupervisor.GetMovieId
    case EnricherMovieWorker.WaitForNextBatch =>
      println("WORKER ASKING FOR DUTY !!!!")
      context.become(receive)
  }

}

object EnricherMovieWorker {

  sealed trait WorkerMessage

  case object StartWorking

  case object WaitForNextBatch

  case class IndexMovie(id: Int) extends WorkerMessage

}

