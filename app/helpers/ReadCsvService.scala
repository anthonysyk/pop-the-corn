package helpers

trait ReadCsvHelper {

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