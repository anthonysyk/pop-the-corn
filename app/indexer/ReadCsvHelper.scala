package indexer

import java.io.File

import models.kaggle.{Casting, KaggleInput, MovieData, Rating}

import scala.io.Source
import com.github.tototoshi.csv._

trait ReadCsvHelper {

  val bufferedSource = Source.fromFile("/sideproject/pop-the-corn/app/resources/movie_metadata.csv")
  val file = new File("/sideproject/pop-the-corn/app/resources/movie_metadata.csv")

  @deprecated("Use reader because parsing is better", "1.0")
  val csv: Seq[Array[String]] = for {
    lines <- bufferedSource.getLines().drop(1).toVector
    values = lines.split(",")
  } yield {
    values
  }

  val reader = CSVReader
    .open(file)
    .all()
    .drop(1)


  val kaggleInput: Seq[KaggleInput] = reader.map { line =>
    KaggleInput(
      line(0),
      line(1),
      castToInt(line(2)),
      castToInt(line(3)),
      castToInt(line(4)),
      castToInt(line(5)),
      line(6),
      castToInt(line(7)),
      castToDouble(line(8)),
      line(9),
      line(10),
      line(11),
      castToInt(line(12)),
      castToInt(line(13)),
      line(14),
      castToInt(line(15)),
      line(16),
      line(17),
      castToInt(line(18)),
      line(19),
      line(20),
      castToInt(line(21)),
      castToDouble(line(22)),
      line(23),
      castToInt(line(24)),
      castToDouble(line(25)),
      line(26),
      castToInt(line(27))
    )
  }

  val movies: Seq[MovieData] = kaggleInput.map { movie =>

    val casting = Seq(
      Casting(Casting.Role.Director, movie.directorName, movie.directorFacebookLikes),
      Casting(Casting.Role.ActorPrincipal, movie.actor1Name, movie.actor1FacebookLikes),
      Casting(Casting.Role.ActorSecondary, movie.actor2Name, movie.actor2FacebookLikes),
      Casting(Casting.Role.ActorOther, movie.actor3Name, movie.actor3FacebookLikes)
    )

    val rating = Rating(movie.imdbScore, movie.numUserForReviews, movie. numVotedUser, movie. numCriticForReviews)

    MovieData(
      id = movie.imdbId,
      title =  movie.movieTitle.dropRight(1),
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
      casting,
      rating
    )
  }



  //  color,director_name,num_critic_for_reviews,duration,director_facebook_likes,actor_3_facebook_likes,actor_2_name,actor_1_facebook_likes,gross,genres,actor_1_name,movie_title,num_voted_users,cast_total_facebook_likes,actor_3_name,facenumber_in_poster,plot_keywords,movie_imdb_link,num_user_for_reviews,language,country,content_rating,budget,title_year,actor_2_facebook_likes,imdb_score,aspect_ratio,movie_facebook_likes

  def castToInt(value: String): Option[Int] = value match {
    case ToInt(int) => Some(int)
    case _ => None
  }

  def castToDouble(value: String): Option[Double] = value match {
    case ToDouble(double) => Some(double)
    case _ => None
  }

  object ToInt {
    def unapply(value: String): Option[Int] = try {
      Some(value.toInt)
    } catch {
      case _: java.lang.NumberFormatException => None
    }
  }

  object ToDouble {
    def unapply(value: String): Option[Double] = try {
      Some(value.toDouble)
    } catch {
      case _: java.lang.NumberFormatException => None
    }
  }
}