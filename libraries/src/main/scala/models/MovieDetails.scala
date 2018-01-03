package models

case class Recommendation(
                         userProfile: DisplayProfile,
                         recommendations: Seq[MovieDetails]
                         )

case class MovieDetails(
                         id: Option[Int],
                         title: Option[String],
                         posterHigh: Option[String],
                         poster: Option[String],
                         backdrop: Option[String],
                         adult: Option[Boolean],
                         vote_average: Option[Double],
                         vote_count: Option[Int],
                         popularity: Option[Double],
                         release_date: Option[String],
                         runtime: Option[Int],
                         homepage: Option[String],
                         genres: String,
                         overview: Option[String],
                         tagline: Option[String],
                         companies: Seq[ProductionCompany],
                         tfidfSimilarity: Option[Double] = None,
                         highestSimilarityProfile: Option[Double] = None,
                         companySqdist: Option[Double] = None,
                         rating: Option[Double] = None,
                         genreWeights: Map[String, Double] = Map.empty[String, Double],
                         companiesWeights: Map[String, Double] = Map.empty[String, Double],
                         cosineSimilarity: Option[Double] = None
                       )

object MovieDetails {

  def fromTmdbMovie(tmdbMovie: TmdbMovie): MovieDetails = {
    import tmdbMovie._
    MovieDetails(
      id = id,
      title = title,
      posterHigh = poster_url_original,
      poster = poster_url_small,
      backdrop = backdrop_url,
      adult = adult,
      vote_average = vote_average,
      vote_count = vote_count,
      popularity = popularity,
      release_date = release_date,
      runtime = runtime,
      homepage = homepage,
      genres = genres.map(_.name).mkString(" "),
      overview = overview,
      tagline = tagline,
      companies = production_companies
    )
  }

}

