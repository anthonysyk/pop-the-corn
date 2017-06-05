package controllers

import javax.inject.{Inject, Singleton}

import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, Controller}
import services.SearchService

import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class SearchController @Inject()(
                                searchService: SearchService
                                ) extends Controller {

  def searchMovie(q: String) = Action.async{
    val eventuallySearchResult = searchService.searchMovie(q)

    for {
      searchResult <- eventuallySearchResult
    } yield {
      val fullMovies: Seq[JsValue] = (searchResult \ "hits" \\ "_source")
      val movies = fullMovies.map { fullMovie =>
        Json.obj(
          "id" -> (fullMovie \ "id").as[String],
          "title" -> (fullMovie \ "title").as[String],
          "genres" -> (fullMovie \ "genres").as[String]
        )
      }
      Ok(Json.toJson(movies))
    }
  }

}
