package controllers

import java.net.URLDecoder
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
    val eventuallySearchResult = searchService.searchMovie(URLDecoder.decode(q, "UTF-8"))
    for {
      searchResponse <- eventuallySearchResult
    } yield {
      val fullMovies = Json.parse(searchResponse.toString) \ "hits" \\ "_source" map (_.as[FullMovie])
      val movieDistinctIds: Seq[Int] = fullMovies.flatMap(_.id).distinct
      val moviesDistinct = movieDistinctIds.flatMap { id =>
        fullMovies.find(_.id == Option(id))
      }
      val movies = moviesDistinct.map { fullMovie =>
        Json.obj(
          "title" -> fullMovie.title,
          "poster" -> fullMovie.poster_url,
          "overview" -> fullMovie.overview,
          "genres" -> fullMovie.genres.mkString(" ")
        )
      }

      Ok(Json.obj("hits" -> movies.length, "movies" -> movies)
      )

      // TODO create a json object for display
    }
  }

  def countMovies = Action.async {
    searchService.countMovies(Movies_Index).map { counter =>
      Ok(s"Il y a $counter films indexés")
    }
  }

  def suggestMovies(q: String) = Action.async {
    searchService.suggest(q).map(suggestions => Ok(Json.toJson(suggestions)))
  }
}
