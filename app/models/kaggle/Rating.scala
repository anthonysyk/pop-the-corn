package models.kaggle

import play.api.libs.json.Json

case class Rating(
                 score: Option[Double],
                 numberOfReviews: Option[Int],
                 numberOfVotes: Option[Int],
                 numberOfCriticForReviews: Option[Int]
                 )

object Rating {
  implicit val formatRating = Json.format[Rating]
}
