package controllers

import javax.inject.{Inject, Singleton}

import models.FullMovie
import models.kaggle.Movie
import models.themoviedb.MovieDetails
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, Controller}
import services.SearchService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

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
      Json.parse(searchResponse.toString) \ "hits" \\ "_source" map (_.as[Movie])
      Ok
    }
  }

  def countMovies = Action.async {
    searchService.countMovies(Movies_Index).map { counter =>
      Ok(s"Il y a $counter films indexés")
    }
  }
}
