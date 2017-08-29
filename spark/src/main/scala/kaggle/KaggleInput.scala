package kaggle

import org.apache.spark.sql.Row



case class KaggleInput(
                        color: String,
                        directorName: String,
                        numCriticForReviews: Option[Int],
                        duration: Option[Double],
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
                        contentRating: String,
                        budget: Option[Double],
                        titleYear: String,
                        actor2FacebookLikes: Option[Int],
                        imdbScore: Option[Double],
                        aspectRatio: String,
                        movieFacebookLikes: Option[Int]
                      ) {

  //"http://www.imdb.com/title/tt0848228/?ref_=fn_tt_tt_1"
  lazy val imdbId: String = movieUrl.replaceAll("^http?:\\/\\/www.imdb.com/title/", "").replaceAll(".\\?ref_=fn_tt_tt_1$", "")
}

object KaggleInput {
  def fromRow(r: Row): KaggleInput = {
    println(r)
    KaggleInput(
      color = r.getAs[String]("color"),
      directorName = r.getAs[String]("director_name"),
      numCriticForReviews = Option(r.getAs[Int]("num_critic_for_reviews")),
      duration = Option(r.getAs[String]("duration").toDouble),
      directorFacebookLikes = Option(r.getAs[Int]("director_facebook_likes")),
      actor3FacebookLikes = Option(r.getAs[Int]("actor_3_facebook_likes")),
      actor2Name = r.getAs[String]("actor_2_name"),
      actor1FacebookLikes = Option(r.getAs[Int]("actor_1_facebook_likes")),
      gross = Option(r.getAs[Double]("gross")),
      genres = r.getAs[String]("genres"),
      actor1Name = r.getAs[String]("actor_1_name"),
      movieTitle = r.getAs[String]("movie_title"),
      numVotedUser = Option(r.getAs[Int]("num_voted_users")),
      castTotalFacebookLikes = Option(r.getAs[Int]("cast_total_facebook_likes")),
      actor3Name = r.getAs[String]("actor_3_name"),
      faceNumberInPoster = Option(r.getAs[Int]("facenumber_in_poster")),
      plotKeywords = r.getAs[String]("plot_keywords"),
      movieUrl = r.getAs[String]("movie_imdb_link"),
      numUserForReviews = Option(r.getAs[Int]("num_user_for_reviews")),
      language = r.getAs[String]("language"),
      country = r.getAs[String]("country"),
      contentRating = r.getAs[String]("content_rating"),
      budget = Option(r.getAs[Double]("budget")),
      titleYear = r.getAs[String]("title_year"),
      actor2FacebookLikes = Option(r.getAs[Int]("actor_2_facebook_likes")),
      imdbScore = Option(r.getAs[Double]("imdb_score")),
      aspectRatio = r.getAs[String]("aspect_ratio"),
      movieFacebookLikes = Option(r.getAs[Int]("movie_facebook_likes"))
    )
  }
}