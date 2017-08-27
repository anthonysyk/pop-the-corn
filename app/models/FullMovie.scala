package models

import models.kaggle.{Casting, Movie, Rating}
import models.themoviedb._
import play.api.libs.json.{Format, Json}

case class MovieStats(
                       color: String,
                       contentRating: Option[Int],
                       faceNumbersInPoster: Option[Int],
                       aspectRatio: String,
                       castTotalFacebookLikes: Option[Int],
                       plotKeywords: Seq[String],
                       casting: Seq[Casting],
                       rating: Rating
                     )

object MovieStats {
  implicit val formatMovieStat = Json.format[MovieStats]
}

case class FullMovie(
                      id: Option[Int],
                      adult: Option[Boolean],
//                      backdrop_path: Option[String],
//                      belongs_to_collection: Option[BelongsToCollection],
                      budget: Option[Float],
                      genres: Seq[String],
                      imdb_id: Option[String],
                      original_language: Option[String],
                      original_title: Option[String],
                      overview: Option[String],
                      popularity: Option[Float],
                      poster_path: Option[String],
                      production_companies: Seq[String],
                      production_countries: Seq[String],
                      release_date: Option[String],
                      revenue: Option[Int],
                      runtime: Option[Int],
                      spoken_languages: Seq[String],
                      status: Option[String],
                      title: Option[String],
                      vote_average: Option[Float],
                      vote_count: Option[Int],
                      movieStats: MovieStats
                    ) {
  lazy val poster_url: Option[String] = poster_path.map(url => "https://image.tmdb.org/t/p/w1280" + url)

  val suggestion = Suggestion(
    id,
    title.getOrElse("No Title"),
    poster_url,
    vote_average,
    vote_count.getOrElse(0)
  )
}

object FullMovie {

  implicit val formatFullMovie: Format[FullMovie] = Json.format[FullMovie]

  def fromMovieDetails(movie: Movie, movieDetails: MovieDetails) = {
    FullMovie(
      id = movie.id,
      adult = movieDetails.adult,
//      backdrop_path = movieDetails.backdrop_path,
//      belongs_to_collection = movieDetails.belongs_to_collection,
      budget = movieDetails.budget,
      genres = movieDetails.genres.map(_.name),
      imdb_id = movieDetails.imdb_id,
      original_language = movieDetails.original_language,
      original_title = movieDetails.original_title,
      overview = movieDetails.overview,
      popularity = movieDetails.popularity,
      poster_path = movieDetails.poster_path,
      production_companies = movieDetails.production_companies.map(_.name),
      production_countries = movieDetails.production_countries.map(_.name),
      release_date = movieDetails.release_date,
      revenue = movieDetails.revenue,
      runtime = movieDetails.runtime,
      spoken_languages = movieDetails.spoken_languages.map(_.iso_639_1),
      status = movieDetails.status,
      title = movieDetails.title,
      vote_average = movieDetails.vote_average,
      vote_count = movieDetails.vote_count,
      movieStats = MovieStats(
        color = movie.color,
        contentRating = movie.contentRating,
        faceNumbersInPoster = movie.faceNumbersInPoster,
        aspectRatio = movie.aspectRatio,
        castTotalFacebookLikes = movie.castTotalFacebookLikes,
        plotKeywords = movie.plotKeywords,
        casting = movie.casting,
        rating = movie.rating
      )
    )
  }
}

