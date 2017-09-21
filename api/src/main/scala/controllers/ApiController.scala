package controllers

import java.net.URLDecoder

import io.circe.Json
import io.circe._
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import services.ApiService

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits._

object ApiController {

  def startService(): Unit = {
    ApiService.start()
  }

  def searchMovie(q: String): Future[String] = {
    val eventuallySearchResult = ApiService.searchMovie(URLDecoder.decode(q, "UTF-8"))

    for {
      searchResponse <- eventuallySearchResult
    } yield {

      val displayedMovie = searchResponse.results.map { movie =>
        Json.obj(
          "title" -> movie.title.asJson,
          "poster" -> movie.poster_url.asJson,
          "overview" -> movie.overview.asJson,
          "genres" -> movie.genres.map(_.name).mkString(" ").asJson,
          "votes" -> movie.vote_count.asJson,
          "note" -> movie.vote_average.asJson
        )
      }

      Json.obj("hits" -> displayedMovie.size.asJson, "movies" -> displayedMovie.asJson).toString()

      // TODO create a json object for display
    }
  }

  def suggestMovies(q: String): Future[String] = {
    val eventuallySuggestions = ApiService.suggest(q)

    for {
      suggestions <- eventuallySuggestions
    } yield suggestions.asJson.noSpaces

  }


}
