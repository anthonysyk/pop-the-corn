package services

import javax.inject.{Inject, Singleton}

import helpers.ReadCsvHelper
import models.KaggleInput
import play.api.libs.json.{JsValue, Json}

import scala.concurrent.Future
import scala.io.Source

@Singleton
class ElasticsearchService @Inject()() extends ReadCsvHelper {

  def indexMovies: Future[JsValue] = {

    val bufferedSource = Source.fromFile("/sideproject/pop-the-corn/app/resources/movie_metadata.csv")
    val csv: Seq[Array[String]] = for {
      lines <- bufferedSource.getLines().drop(1).toVector
      values = lines.split(",").map(_.trim)
    } yield {
      values
    }

    val kaggleInput = csv.map { line =>
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

    Json.toJson(kaggleInput)
  }

}
