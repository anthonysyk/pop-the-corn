package models

import models.kaggle.Movie
import models.themoviedb.MovieDetails
import play.api.libs.json.{Json, OFormat}

case class FullMovie(
                      movie: Movie,
                      movieDetails: Seq[MovieDetails] = Seq.empty[MovieDetails]
                    ) {
  val suggestion = Suggestion(
    movie.id,
    movie.title,
    movieDetails.headOption.flatMap(_.poster_url),
    movieDetails.headOption.flatMap(_.vote_average),
    movieDetails.headOption.flatMap(_.vote_count).getOrElse(0)
  )
}

object FullMovie {
  implicit val formatFullMovie: OFormat[FullMovie] = Json.format[FullMovie]
}

