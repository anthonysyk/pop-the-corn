package models

case class Movie(
                title: String,
                color: String,
                duration: String,
                budget: Float,
                gross: Float,
                genres: Seq[String],
                contentRating: String,
                faceNumbersInPoster: String,
                language: String,
                country: String,
                titleYear: String,
                aspectRatio: String,
                castTotalFacebookLikes: String,
                plotKeywords: String,
                movieLink: String,
                casting: Seq[Casting],
                rating: Rating
                )

object