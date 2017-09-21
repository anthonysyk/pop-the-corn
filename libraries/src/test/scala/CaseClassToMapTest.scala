import models._
import org.scalatest.FunSuite
import ptc.libraries.EsClient
import io.circe._
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._

import scala.annotation.implicitNotFound

class CaseClassToMapTest extends FunSuite with EsClient {

  test("Serialize Json to Map from Product") {

    println(parseJsonToMap(TmdbExample.movie1.asJson))

    println(parseProductToMap(TmdbExample.movie2))
    println(parseJsonToMap(TmdbExample.movie2.asJson))


  }

  test("Serialize Json to Case Class") {

    println(decode[TmdbMovie](parse(TmdbExample.movieJson).right.toOption.getOrElse(Json.Null).noSpaces))


  }
}

object TmdbExample {
  val movie1 = TmdbMovie(Some(false), Some("/43PLe4yV8kOaFQaYL7IBzN2vuam.jpg"), None, Some(0.0), Nil, None, Some(462942), Some("tt4653202"), Some("it"), Some("The Habit of Beauty"), None, Some(0.565588), Some("/rwEz88f44BocfmkJukHHx6rh7ri.jpg"), Vector(), Vector(ProductionCountry("IT", "Italy")), Some("2017-06-22"), Some(0), None, Vector(), Some("Released"), Some("The Habit of Beauty"), None, None, Some(0.0), Some(0))
  val movie2 = TmdbMovie(Some(false), Some("/txKYS7FU9Q1gZKcmu4pozxsMccg.jpg"), None, Some(2.7E7), Vector(Genre(28, "Action"), Genre(12, "Adventure"), Genre(878, "Science Fiction"), Genre(10749, "Romance")), None, Some(79698), Some("tt1321869"), Some("en"), Some("The Lovers"), Some("The Lovers is an epic romance time travel adventure film. Helmed by Roland Joffé from a story by Ajey Jhankar, the film is a sweeping tale of an impossible love set against the backdrop of the first Anglo-Maratha war across two time periods and continents and centred around four characters — a British officer in 18th century colonial India, the Indian woman he falls deeply in love with, an American present-day marine biologist and his wife."), Some(1.270164), Some("/2tszioaFbawMOkwOXin1ig7oJkP.jpg"), Vector(ProductionCompany("Corsan", 7299), ProductionCompany("Bliss Media", 8186), ProductionCompany("Limelight International Media Entertainment", 8187), ProductionCompany("Neelmudra Entertainment", 8188), ProductionCompany("Aristos Films", 76098), ProductionCompany("Singularity Productions", 76099), ProductionCompany("Wildkite", 76100)), Vector(ProductionCountry("AU", "Australia"), ProductionCountry("BE", "Belgium"), ProductionCountry("IN", "India")), Some("2015-02-13"), Some(0), Some(109), Vector(SpokenLanguage("en", "English")), Some("Released"), Some("The Lovers"), None, None, Some(4.8), Some(34))
  val movie3Map: Map[String, Any] = Map("popularity" -> 0.082165, "spoken_languages" -> Map("iso_639_1" -> "de", "name" -> "Deutsch"), "original_language" -> "de", "vote_count" -> 1, "vote_average" -> 8.0, "id" -> 412068, "release_date" -> "Sun Jul 10 00:00:00 CEST 2016", "status" -> "Released", "revenue" -> 0, "genres" -> Map("name" -> "Music", "id" -> 10402), "budget" -> 0.0, "adult" -> false, "title" -> "Blechnarrisch", "imdb_id" -> "tt5982896", "original_title" -> "Blechnarrisch", "poster_path" -> "/vuhPxeLaphr2ypturTKqZV4bt4O.jpg", "production_countries" -> Map("name" -> "Germany", "iso_3166_1" -> "DE"), "backdrop_path" -> "/nhcIUSvzGrJLaCJRCRhNppQ0jEC.jpg")
  val movieJson = """{"production_companies": [],"popularity":"0.525416","original_language":"it","vote_count":"0","vote_average":"0.0","id":"462942","release_date":"2017-06-22","status":"Released","revenue":"0","genres":[{"name":"Drama","id":"18"}],"budget":"0.0","adult":false,"title":"The Habit of Beauty","imdb_id":"tt4653202","original_title":"The Habit of Beauty","poster_path":"/rwEz88f44BocfmkJukHHx6rh7ri.jpg","production_countries":[{"name":"Italy","iso_3166_1":"IT"}],"backdrop_path":"/43PLe4yV8kOaFQaYL7IBzN2vuam.jpg"}"""
}
