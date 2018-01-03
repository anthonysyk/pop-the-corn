package controllers

import java.net.URLDecoder

import io.circe.Json
import io.circe._
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import models.{Genre, MovieDetails}
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
          "id" -> movie.id.asJson,
          "title" -> movie.title.asJson,
          "poster" -> movie.poster_url_small.asJson,
          "overview" -> movie.overview.asJson,
          "genres" -> movie.genres.map(_.name).mkString(" ").asJson,
          "vote_count" -> movie.vote_count.asJson,
          "vote_average" -> movie.vote_average.asJson
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

  def getMovie(id: String) = {
    for {
      movie <- ApiService.getMovie(id)
    } yield movie.asJson.noSpaces
  }

  def getPopularMovies() = {
    for {
      popularMovies <- ApiService.getPopularMovies(50)
    } yield popularMovies.asJson.noSpaces
  }

  def getBestRatedMovies() = {
    for {
      bestRatedMovies <- ApiService.getBestRatedMovies
    } yield bestRatedMovies.asJson.noSpaces
  }

  def getPopularMoviesByGenre() = {

    for {
      popularMovies <- ApiService.getPopularMovies(500)
    } yield {
      Json.fromFields(Genre.values.map(genre => genre.toString -> popularMovies.filter(_.genres.contains(genre.toString)).take(20).asJson)).noSpaces
    }

  }

  def getQuickRatingMovies() = {
    for {
      popularMovies <- ApiService.getPopularMovies(5000).map(_.filterNot(_.id contains 416148))
    } yield {
      val allMovies: Seq[(String, Seq[MovieDetails])] = Genre.values.map(genre =>
        genre.toString -> popularMovies.filter(_.genres.contains(genre.toString))
      ).toSeq.sortBy(_._2.size)

      allMovies.foldLeft(Seq.empty[MovieDetails]) {
        (acc, next) => acc ++ next._2.take(3).filterNot(movie => acc contains movie)
      }.asJson.noSpaces

    }
  }

}
