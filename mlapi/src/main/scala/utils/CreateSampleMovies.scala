package utils

import models.TmdbMovie
import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, SaveMode, SparkSession}
import ptc.libraries.MovieIndexDefinition

object CreateSampleMovies {

  case class FlattenMovie(
                           adult: Option[Boolean],
                           budget: Option[Double],
                           genres: Seq[String],
                           nbGenres: Int,
                           original_language: Option[String],
                           popularity: Option[Double],
                           vote_average: Option[Double],
//                           production_companies: Seq[String],
//                           production_countries: Seq[String],
                           release_date: Option[String],
                           revenue: Option[Int],
                           runtime: Option[Int],
                           tagline: Option[String],
                           title: Option[String],
                           vote_count: Option[Int]
                         )

  object FlattenMovie {

    def fromTmdbMovie(tmdbMovie: TmdbMovie) = {
      FlattenMovie(
        tmdbMovie.adult,
        tmdbMovie.budget,
        tmdbMovie.genres.map(_.name),
        tmdbMovie.genres.length,
        tmdbMovie.original_language,
        tmdbMovie.popularity,
        tmdbMovie.vote_average,
//        tmdbMovie.production_companies.map(_.name),
//        tmdbMovie.production_countries.map(_.name),
        tmdbMovie.release_date,
        tmdbMovie.revenue,
        tmdbMovie.runtime,
        tmdbMovie.tagline,
        tmdbMovie.title,
        tmdbMovie.vote_count
      )
    }

  }


  def main(args: Array[String]): Unit = {

    val conf: SparkConf = new SparkConf().setMaster("local[*]")
    val ss: SparkSession = SparkSession.builder().config(conf).getOrCreate()

    import io.circe._
    import io.circe.generic.auto._
    import io.circe.parser._
    import org.elasticsearch.spark._

    import ss.implicits._


    val moviesRDD: RDD[FlattenMovie] = ss.sparkContext.esJsonRDD(s"${MovieIndexDefinition.IndexName}/${MovieIndexDefinition.TypeName}").values
      .flatMap(s => decode[TmdbMovie](parse(s).right.toOption.getOrElse(Json.Null).noSpaces).right.toOption.map(FlattenMovie.fromTmdbMovie))
      .persist()

    val genresRef = ss.sparkContext.broadcast(moviesRDD.flatMap(_.genres).distinct().collect())

    import org.apache.spark.sql.functions.udf

    def genreRepartition(genre: String): (Double, Seq[String]) => Double = { (nbGenre: Double, genres: Seq[String]) =>
      if (genres.contains(genre)) 1 / nbGenre else 0
    }

    def genreRepartitionUDF(genre: String) = udf(genreRepartition(genre))

    val matriceDF: DataFrame = genresRef.value.foldLeft(moviesRDD.toDF) { (acc, next) =>
      acc.withColumn(next, genreRepartitionUDF(next)('nbGenres, 'genres))
    }

    matriceDF.show(20)

    matriceDF.drop('genres).coalesce(1).write.mode(SaveMode.Overwrite)
      .option("header", "true")
      .option("delimiter", "|")
      .csv("/home/anthony/projects/popthecorn/data")

  }

}