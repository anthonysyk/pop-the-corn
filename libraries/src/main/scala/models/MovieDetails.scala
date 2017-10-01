package models

case class MovieDetails(
                         id: Option[Int],
                         title: Option[String],
                         poster: Option[String],
                         backdrop: Option[String],
                         vote_average: Option[Double],
                         vote_count: Option[Int],
                         popularity: Option[Double],
                         release_date: Option[String],
                         runtime: Option[Int],
                         homepage: Option[String],
                         genres: String,
                         overview: Option[String],
                         tagline: Option[String],
                         tfidfSimilarity: Option[Double] = None
                       )

object MovieDetails {

  def fromTmdbMovie(tmdbMovie: TmdbMovie) = {
    import tmdbMovie._
    MovieDetails(
      id = id,
      title = title,
      poster = poster_url,
      backdrop = backdrop_url,
      vote_average = vote_average,
      vote_count = vote_count,
      popularity = popularity,
      release_date = release_date,
      runtime = runtime,
      homepage = homepage,
      genres = genres.map(_.name).mkString(" "),
      overview = overview,
      tagline = tagline
    )
  }

}