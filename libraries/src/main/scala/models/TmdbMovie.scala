package models

case class TmdbMovie(
                      adult: Option[Boolean],
                      backdrop_path: Option[String],
                      belongs_to_collection: Option[BelongsToCollection],
                      budget: Option[Double],
                      genres: Seq[Genre] = Nil,
                      homepage: Option[String],
                      id: Option[Int],
                      imdb_id: Option[String],
                      original_language: Option[String],
                      original_title: Option[String],
                      overview: Option[String],
                      popularity: Option[Double],
                      poster_path: Option[String],
                      production_companies: Seq[ProductionCompany] = Nil,
                      production_countries: Seq[ProductionCountry] = Nil,
                      release_date: Option[String],
                      revenue: Option[Int],
                      runtime: Option[Int],
                      spoken_languages: Seq[SpokenLanguage] = Nil,
                      status: Option[String],
                      tagline: Option[String],
                      title: Option[String],
                      video: Option[Boolean],
                      vote_average: Option[Double],
                      vote_count: Option[Int],
                      tfidfSimilarity: Option[Double] = None
                    ) {
  lazy val poster_url: Option[String] = poster_path.map(url => "https://image.tmdb.org/t/p/w1280" + url)
  lazy val backdrop_url: Option[String] = backdrop_path.map(url => "https://image.tmdb.org/t/p/w1280" + url)


  val suggestion = Suggestion(
    id,
    title.getOrElse("No Title"),
    poster_url,
    vote_average,
    vote_count.getOrElse(0)
  )

  val suggestionES = SuggestionES(
    suggest = suggestion,
    ngram = title.getOrElse("No Title"),
    ngram_folded = title.getOrElse("No Title"),
    votes = vote_count.getOrElse(0)
  )
}

case class BelongsToCollection(
                                id: Int,
                                name: String,
                                poster_path: String,
                                backdrop_path: String
                              )

case class Genre(
                  id: Int,
                  name: String
                )

object Genre extends Enumeration {
  val Drama, Comedy, Documentary, Thriller, Horror, Romance, Action, Animation, Crime, Family, ScienceFiction, Adventure = Value
}

case class ProductionCompany(
                              name: String,
                              id: Int
                            )

case class ProductionCountry(
                              iso_3166_1: String,
                              name: String
                            )

case class SpokenLanguage(
                           iso_639_1: String,
                           name: String
                         )
