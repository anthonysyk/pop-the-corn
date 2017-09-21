package models

case class DiscoveredMovie(
                            poster_path: Option[String],
                            adult: Option[Boolean],
                            overview: Option[String],
                            release_date: Option[String],
                            genre_ids: Seq[Int],
                            id: Option[Int],
                            original_title: Option[String],
                            original_language: Option[String],
                            title: Option[String],
                            backdrop_path: Option[String],
                            popularity: Option[Double],
                            vote_count: Option[Int],
                            video: Option[Boolean],
                            vote_average: Option[Double]
                          )
