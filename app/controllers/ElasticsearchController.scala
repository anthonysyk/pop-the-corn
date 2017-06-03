package controllers

import javax.inject.{Inject, Singleton}

import play.api.mvc._
import services.ElasticsearchService

@Singleton
class ElasticsearchController @Inject()(
                                       elasticsearchService: ElasticsearchService
                                       ) extends Controller {


  def indexMovies = Action {
    Ok
  }

}
