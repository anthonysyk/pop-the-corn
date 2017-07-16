package models.themoviedb

import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json.{JsPath, Json, Reads}


case class MovieDetails(
                      adult: Option[Boolean],
                      backdrop_path: Option[String],
                      belongs_to_collection: Option[BelongsToCollection],
                      budget: Option[Float],
                      genres: Seq[Genre],
//                      homepage: Option[String],
                      id: Option[Int],
                      imdb_id: Option[String],
                      original_language: Option[String],
                      original_title: Option[String],
                      overview: Option[String],
                      popularity: Option[Float],
                      poster_path: Option[String],
                      production_companies: Seq[ProductionCompany],
                      production_countries: Seq[ProductionCountry],
                      release_date: Option[String],
                      revenue: Option[Int],
                      runtime: Option[Int],
                      spoken_languages: Seq[SpokenLanguage],
                      status: Option[String],
//                      tagline: Option[String],
                      title: Option[String],
//                      video: Option[Boolean],
                      vote_average: Option[Float],
                      vote_count: Option[Int]
                    ) {
  lazy val poster_url: Option[String] = poster_path.map(url => "https://image.tmdb.org/t/p/w1280" + url)
}

object MovieDetails {
  implicit val movieDetailsFormats = Json.format[MovieDetails]
}