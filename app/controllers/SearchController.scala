package controllers

import javax.inject.{Inject, Singleton}

import models.FullMovie
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import services.SearchService

import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class SearchController @Inject()(
                                  searchService: SearchService
                                ) extends Controller {

  lazy val Movies_Index = "movies_index"

  def searchMovie(q: String) = Action.async {
    val eventuallySearchResult = searchService.searchMovie(q)
    for {
      searchResponse <- eventuallySearchResult
    } yield {
      val hits = (Json.parse(searchResponse.toString) \ "_hits" \ "total").as[Int]
      val fullMovies = Json.parse(searchResponse.toString) \ "hits" \\ "_source" map (_.as[FullMovie])
      val movies = fullMovies.distinct.map { fullMovie =>
        Json.obj(
          "title" -> fullMovie.movie.title,
          "poster" -> fullMovie.movieDetails.headOption.map(_.poster_url),
          "overview" -> fullMovie.movieDetails.headOption.map(_.overview.getOrElse("Aucune description")),
          "genres" -> fullMovie.movieDetails.headOption.map(_.genres.map(_.name).mkString(" "))
        )
      }
      Ok(Json.obj("hits" -> hits, "movies" -> movies)
      )

      // TODO create a json object for display
    }
  }

  def countMovies = Action.async {
    searchService.countMovies(Movies_Index).map { counter =>
      Ok(s"Il y a $counter films indexés")
    }
  }
}
