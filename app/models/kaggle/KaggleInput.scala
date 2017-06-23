package models.kaggle

case class KaggleInput(
                        color: String,
                        directorName: String,
                        numCriticForReviews: Option[Int],
                        duration: Option[Int],
                        directorFacebookLikes: Option[Int],
                        actor3FacebookLikes: Option[Int],
                        actor2Name: String,
                        actor1FacebookLikes: Option[Int],
                        gross: Option[Double],
                        genres: String,
                        actor1Name: String,
                        movieTitle: String,
                        numVotedUser: Option[Int],
                        castTotalFacebookLikes: Option[Int],
                        actor3Name: String,
                        faceNumberInPoster: Option[Int],
                        plotKeywords: String,
                        movieUrl: String,
                        numUserForReviews: Option[Int],
                        language: String,
                        country: String,
                        contentRating: Option[Int],
                        budget: Option[Double],
                        titleYear: String,
                        actor2FacebookLikes: Option[Int],
                        imdbScore: Option[Double],
                        aspectRatio: String,
                        movieFacebookLikes: Option[Int]
                      ) {

  //"http://www.imdb.com/title/tt0848228/?ref_=fn_tt_tt_1"
  lazy val imdbId = movieUrl.replaceAll("^http?:\\/\\/www.imdb.com/title/", "").replaceAll(".\\?ref_=fn_tt_tt_1$", "")
}


