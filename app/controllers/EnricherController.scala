package controllers

import javax.inject.{Inject, Singleton}

import play.api.mvc.{Action, Controller}
import services.EnricherService

import scala.concurrent.ExecutionContext.Implicits.global


@Singleton
class EnricherController @Inject()(
                                    enricherService: EnricherService
                                  ) extends Controller {

  // For tests : avengers id = 24428 ou imdbid = tt0399877


  def enrichMovies = Action.async {
    enricherService.getAllIds.map(ids => Ok(ids.toString))
  }


}
