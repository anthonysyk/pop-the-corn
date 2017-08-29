package models

import play.api.libs.json.Json

case class Movie(
                externalId: String,
                id: Option[Int] = None,
                title: String,
                color: String,
                duration: Option[Int],
                budget: Option[Double],
                gross: Option[Double],
                genres: Seq[String],
                contentRating: String,
                faceNumbersInPoster: Option[Int],
                language: String,
                country: String,
                titleYear: String,
                aspectRatio: String,
                castTotalFacebookLikes: Option[Int],
                plotKeywords: Seq[String],
                movieUrl: String,
                casting: Seq[Casting],
                rating: Rating
                )

object Movie {
  implicit val formatMovie = Json.format[Movie]
}

case class Casting(
                    role: String,
                    name: String,
                    facebookLikes: Option[Int]
                  )

object Casting {
  implicit val formatCasting = Json.format[Casting]

  object Role {
    val Director = "Director"
    val ActorPrincipal = "ActorPrincipal"
    val ActorSecondary = "ActorSecondary"
    val ActorOther = "ActorOther"
  }
}

case class Rating(
                   score: Option[Double],
                   numberOfReviews: Option[Int],
                   numberOfVotes: Option[Int],
                   numberOfCriticForReviews: Option[Int]
                 )

object Rating {
  implicit val formatRating = Json.format[Rating]
}
