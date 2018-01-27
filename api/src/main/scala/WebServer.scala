import java.io.File

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import controllers.{ApiController, GraphQLController}
import io.circe.Json
import io.circe.generic.auto._
import io.circe.parser.parse
import io.circe.syntax._
import models.{MovieDetails, Recommendation}
import org.elasticsearch.index.translog.Translog.Source
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server._
import StatusCodes._
import Directives._

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.util.{Failure, Success}

object WebServer {
  def main(args: Array[String]) {

    val params = args.sliding(2, 2).map(arr => arr(0) -> arr(1)).toMap

    val maybeHost = args.sliding(2, 2).map(arr => arr(0) -> arr(1)).toMap.get("--host")
    val maybePort = args.sliding(2, 2).map(arr => arr(0) -> arr(1)).toMap.get("--port")

    println(s"host: $maybeHost port: $maybePort")
    if (maybeHost.isEmpty || maybePort.isEmpty) {
      println("error with params, please specify an --host and a --port")
      System.exit(0)
    }

    val host = maybeHost.get
    val port = maybePort.get.toInt
    println(s"starting server with config: http://$host:$port")


    implicit val system: ActorSystem = ActorSystem("my-system")
    implicit val materializer: ActorMaterializer = ActorMaterializer()
    // needed for the future flatMap/onComplete in the end
    implicit val executionContext: ExecutionContextExecutor = system.dispatcher

    val rootDir: String = new File(".").getCanonicalPath

    var recommendations: Vector[Recommendation] = Vector.empty

    ApiController.startService() // To launch actors

    implicit def rejectionHandler = RejectionHandler.newBuilder()
      .handleNotFound{ getFromFile(s"$rootDir/assets/index.html")}
      .result

    val route =
      pathSingleSlash {
        println(rootDir)
        getFromFile(s"$rootDir/assets/index.html")
      } ~
        path("favicon.ico") {
          println(rootDir)
          getFromFile(s"$rootDir/assets/images/favicon.ico")
        } ~
        pathPrefix("assets") {
          getFromDirectory(s"$rootDir/assets")
        } ~
        path("search") {
          get {
            parameter('q.as[String]) { q =>
              println(s"Search de $q")
              val results = ApiController.searchMovie(q)
              onComplete(results) {
                case Success(result) => complete(result)
                case Failure(ex) => complete(StatusCodes.InternalServerError, s"$ex")
              }
            }
          }
        } ~
        path("suggest") {
          get {
            parameter('q.as[String]) { q =>
              println(s"Suggestion de $q")
              val results = ApiController.suggestMovies(q)
              onComplete(results) {
                case Success(result) => complete(result)
                case Failure(ex) => complete(StatusCodes.InternalServerError, s"$ex")
              }
            }
          }
        } ~
        pathPrefix("movie") {
          pathEnd {
            complete("/movie")
          } ~
            path(IntNumber) { id =>
              println(s"Details du film $id")
              val results = ApiController.getMovie(id.toString)
              onComplete(results) {
                case Success(result) => complete(result)
                case Failure(ex) => complete(StatusCodes.InternalServerError, s"$ex")
              }
            }
        } ~
        path("popular") {
          get {
            println(s"Récupération des films les plus populaires")
            val results = ApiController.getPopularMovies()
            onComplete(results) {
              case Success(result) => complete(result)
              case Failure(ex) => complete(StatusCodes.InternalServerError, s"$ex")
            }
          }
        } ~
        path("bestrated") {
          get {
            println(s"Récupération des films les mieux notés")
            val results = ApiController.getBestRatedMovies()
            onComplete(results) {
              case Success(result) => complete(result)
              case Failure(ex) => complete(StatusCodes.InternalServerError, s"$ex")
            }
          }
        } ~
        path("popularByGenre") {
          get {
            println(s"Récupération des films populaires par Genre")
            val results = ApiController.getPopularMoviesByGenre()
            onComplete(results) {
              case Success(result) => complete(result)
              case Failure(ex) => complete(StatusCodes.InternalServerError, s"$ex")
            }
          }
        } ~
        pathPrefix("tfidf") {
          pathEnd {
            complete("/tfidf")
          } ~
            path(IntNumber) { id =>
              println(s"Details du film $id")
              val results = GraphQLController.getMovieSimilaritiesById(id)
              onComplete(results) {
                case Success(result) => complete(result)
                case Failure(ex) => complete(StatusCodes.InternalServerError, s"$ex")
              }
            }
        } ~
        path("quickrating") {
          get {
            println(s"Récupération de films pour un quick rating")
            val results = ApiController.getQuickRatingMovies()
            onComplete(results) {
              case Success(result) => complete(result)
              case Failure(ex) => complete(StatusCodes.InternalServerError, s"$ex")
            }
          }
        } ~
        path("quickrating") {
          post {
            entity(as[String]) { response =>
              val quickRatingResults: Seq[MovieDetails] = parse(response).right.toOption.getOrElse(Json.Null).as[Seq[MovieDetails]].right.toOption.getOrElse(Nil)
              val userProfile = GraphQLController.convertQuickResultsIntoUserProfile(quickRatingResults)
              val eventuallyRecommendations: Future[Recommendation] = GraphQLController.getMoviesBasedOnTaste(quickRatingResults, userProfile._2.movieId)
              eventuallyRecommendations.onSuccess { case result => recommendations = Vector.empty :+ result }
              onComplete(eventuallyRecommendations) {
                case Success(result) => complete(result.asJson.noSpaces)
                case Failure(ex) => complete(StatusCodes.InternalServerError, s"$ex")
              }
            }
          }
        } ~
        pathPrefix("recommendation") {
          pathEnd {
            complete("/recommendation")
          } ~
            path(JavaUUID) { uuid =>
              complete(recommendations.find(_.userProfile.movieId == uuid.toString) match {
                case Some(reco) => reco.asJson.noSpaces
                case None => StatusCodes.NotFound
              })
            }
        }

    val bindingFuture = Http().bindAndHandle(route, host, port)

    println(s"Server online at http://$host:$port")
  }
}