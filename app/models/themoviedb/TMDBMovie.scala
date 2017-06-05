package models.themoviedb

case class TMDBMovie(
                      adult: Option[Boolean],
                      backdropPath: Option[String],
                      belongsToCollection: Option[BelongsToCollection],
                      budget: Option[Float],
                      genres: Seq[Genre],
                      homepage: Option[String],
                      id: Option[Int],
                      imdbId: Option[String],
                      originalLanguage: Option[String],
                      originalTitle: Option[String],
                      overview: Option[String],
                      popularity: Option[Float],
                      posterPath: Option[String],
                      productionCompanies: Seq[ProductionCompany],
                      productionCountries: Seq[ProductionCountry],
                      releaseDate: Option[String],
                      revenue: Option[Int],
                      runtime: Option[Int],
                      spokenLanguages: Seq[SpokenLanguage],
                      status: Option[String],
                      tagline: Option[String],
                      title: Option[String],
                      video: Option[Boolean],
                      voteAverage: Option[Float],
                      voteCount: Option[Integer]
                    )

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