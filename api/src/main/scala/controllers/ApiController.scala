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
      val dramaMovies = Genre.Drama.toString -> popularMovies.filter(_.genres.contains(Genre.Drama.toString)).take(20).asJson
      val comedyMovies = Genre.Comedy.toString -> popularMovies.filter(_.genres.contains(Genre.Comedy.toString)).take(20).asJson
      val documentaryMovies = Genre.Documentary.toString -> popularMovies.filter(_.genres.contains(Genre.Documentary.toString)).take(20).asJson
      val thrillerMovies = Genre.Thriller.toString -> popularMovies.filter(_.genres.contains(Genre.Thriller.toString)).take(20).asJson
      val horrorMovies = Genre.Horror.toString -> popularMovies.filter(_.genres.contains(Genre.Horror.toString)).take(20).asJson
      val romanceMovies = Genre.Romance.toString -> popularMovies.filter(_.genres.contains(Genre.Romance.toString)).take(20).asJson
      val actionMovies = Genre.Action.toString -> popularMovies.filter(_.genres.contains(Genre.Action.toString)).take(20).asJson
      val familyMovies = Genre.Family.toString -> popularMovies.filter(_.genres.contains(Genre.Family.toString)).take(20).asJson

      Json.fromFields(Seq(dramaMovies, comedyMovies, documentaryMovies, thrillerMovies, horrorMovies, romanceMovies, actionMovies, familyMovies)).noSpaces
    }
  }


}
