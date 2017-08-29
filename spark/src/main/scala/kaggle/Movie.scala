package kaggle

case class Movie(
                externalId: String,
                id: Option[Int] = None,
                title: String,
                color: String,
                duration: Option[Double],
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

case class Casting(
                    role: String,
                    name: String,
                    facebookLikes: Option[Int]
                  )

object Casting {
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


object Movie {

  def fromKaggleInput(movie: KaggleInput) = {

      val casting = Seq(
        Casting(Casting.Role.Director, movie.directorName, movie.directorFacebookLikes),
        Casting(Casting.Role.ActorPrincipal, movie.actor1Name, movie.actor1FacebookLikes),
        Casting(Casting.Role.ActorSecondary, movie.actor2Name, movie.actor2FacebookLikes),
        Casting(Casting.Role.ActorOther, movie.actor3Name, movie.actor3FacebookLikes)
      )

      val rating = Rating(movie.imdbScore, movie.numUserForReviews, movie.numVotedUser, movie.numCriticForReviews)

      Movie(
        externalId = movie.imdbId,
        title = movie.movieTitle.dropRight(1),
        color = movie.color,
        duration = movie.duration,
        budget = movie.budget,
        gross = movie.gross,
        genres = movie.genres.split('|'),
        contentRating = movie.contentRating,
        faceNumbersInPoster = movie.faceNumberInPoster,
        language = movie.language,
        country = movie.country,
        titleYear = movie.titleYear,
        aspectRatio = movie.aspectRatio,
        castTotalFacebookLikes = movie.castTotalFacebookLikes,
        plotKeywords = movie.plotKeywords.split('|'),
        movieUrl = movie.movieUrl,
        casting = casting,
        rating = rating
      )
  }

}