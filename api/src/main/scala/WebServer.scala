import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import controllers.ApiController

import scala.io.StdIn
import scala.util.{Failure, Success}

object WebServer {
  def main(args: Array[String]) {

    implicit val system = ActorSystem("my-system")
    implicit val materializer = ActorMaterializer()
    // needed for the future flatMap/onComplete in the end
    implicit val executionContext = system.dispatcher

    ApiController.startService // To launch actors

    val route =
      pathSingleSlash {
        getFromFile("/sideproject/pop-the-corn/app/index.html")
      } ~
        path("favicon.ico") {
          getFromFile("/sideproject/pop-the-corn/app/assets/images/favicon.ico")
        } ~
        path("search") {
          get {
            parameter('q.as[String]) { q =>
              println(s"Search de $q")
              val results = ApiController.searchMovie(q)
              //            complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, s"$results"))
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
        }

    val bindingFuture = Http().bindAndHandle(route, "localhost", 9000)

    println(s"Server online at http://localhost:9000/\nPress RETURN to stop...")
    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done
  }
}