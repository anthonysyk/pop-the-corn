package models.kaggle

import play.api.libs.json.Json

case class MovieData(
                id: String,
                title: String,
                color: String,
                duration: Option[Int],
                budget: Option[Double],
                gross: Option[Double],
                genres: Seq[String],
                contentRating: Option[Int],
                faceNumbersInPoster: Option[Int],
                language: String,
                country: String,
                titleYear: String,
                aspectRatio: String,
                castTotalFacebookLikes: Option[Int],
                plotKeywords: Seq[String],
                movieLink: String,
                casting: Seq[Casting],
                rating: Rating
                )

object MovieData {
  implicit val formatMovie = Json.format[MovieData]
}