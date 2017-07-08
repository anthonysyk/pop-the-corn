package models

import models.kaggle.Movie
import models.themoviedb.MovieDetails
import play.api.libs.json.Json

case class FullMovie(
                      movie: Movie,
                      movieDetails: Seq[MovieDetails] = Seq.empty[MovieDetails]
                    )

object FullMovie {

  implicit val formatFullMovie = Json.format[FullMovie]

}

