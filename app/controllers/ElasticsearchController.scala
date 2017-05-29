package controllers

import javax.inject.{Inject, Singleton}

import play.api.mvc._
import services.ElasticsearchService
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class ElasticsearchController @Inject()(
                                       elasticsearchService: ElasticsearchService
                                       ) extends Controller {


  def indexMovies = Action.async{

    val futureResult = elasticsearchService.indexMovies

    futureResult.map(Ok(_))
  }

}
