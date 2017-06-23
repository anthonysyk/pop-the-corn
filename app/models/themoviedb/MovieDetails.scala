package models.themoviedb

import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json.{JsPath, Json, Reads}


case class MovieDetails(
                      adult: Option[Boolean],
//                      backdrop_path: Option[String],
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
                      tagline: Option[String],
                      title: Option[String],
//                      video: Option[Boolean],
                      vote_average: Option[Float],
                      vote_count: Option[Int]
                    )

object MovieDetails {
  implicit val movieDetailsFormats = Json.format[MovieDetails]

//  implicit val tMDBMovieReads : Reads[TMDBMovie] = (
//    (JsPath \ "adult").readNullable[Boolean] and
//      (JsPath \ "backdrop_path").readNullable[String] and
//      (JsPath \ "belongs_to_collection").readNullable[BelongsToCollection] and
//      (JsPath \ "budget").readNullable[Int] and
//      (JsPath \ "genres").readNullable[Seq[Genre]] and
//      (JsPath \ "homepage").readNullable[String] and
//      (JsPath \ "id").readNullable[Int]
//  )(TMDBMovie.apply _)
}

//{
//  adult: false,
//  backdrop_path: "/hbn46fQaRmlpBuUrEiFqv0GDL6Y.jpg",
//  belongs_to_collection: {
//  id: 86311,
//  name: "The Avengers Collection",
//  poster_path: "/qJawKUQcIBha507UahUlX0keOT7.jpg",
//  backdrop_path: "/zuW6fOiusv4X9nnW3paHGfXcSll.jpg"
//},
//  budget: 220000000,
//  genres: [
//{
//  id: 878,
//  name: "Science Fiction"
//},
//{
//  id: 28,
//  name: "Action"
//},
//{
//  id: 12,
//  name: "Adventure"
//}
//  ],
//  homepage: "http://marvel.com/avengers_movie/",
//  id: 24428,
//  imdb_id: "tt0848228",
//  original_language: "en",
//  original_title: "The Avengers",
//  overview: "When an unexpected enemy emerges and threatens global safety and security, Nick Fury, director of the international peacekeeping agency known as S.H.I.E.L.D., finds himself in need of a team to pull the world back from the brink of disaster. Spanning the globe, a daring recruitment effort begins!",
//  popularity: 11.97797,
//  poster_path: "/cezWGskPY5x7GaglTTRN4Fugfb8.jpg",
//  production_companies: [
//{
//  name: "Paramount Pictures",
//  id: 4
//},
//{
//  name: "Marvel Studios",
//  id: 420
//}
//  ],
//  production_countries: [
//{
//  iso_3166_1: "US",
//  name: "United States of America"
//}
//  ],
//  release_date: "2012-04-25",
//  revenue: 1519557910,
//  runtime: 143,
//  spoken_languages: [
//{
//  iso_639_1: "en",
//  name: "English"
//}
//  ],
//  status: "Released",
//  tagline: "Some assembly required.",
//  title: "The Avengers",
//  video: false,
//  vote_average: 7.4,
//  vote_count: 10458
//}