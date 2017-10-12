package contentbased

import breeze.numerics.log
import io.circe.Json
import io.circe.parser.{decode, parse}
import models.{Genre, MovieDetails, TmdbMovie, UserProfile}
import nlp.TFIDF.ss
import org.apache.spark.SparkConf
import org.apache.spark.mllib.linalg
import org.apache.spark.mllib.linalg.{DenseVector, SparseVector, Vectors}
import org.apache.spark.mllib.linalg.distributed.{IndexedRow, IndexedRowMatrix}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import ptc.libraries.MovieIndexDefinition

import scala.collection.immutable.ListMap

object ContentBasedRecommendation {

  val mockedUserProfile = UserProfile(ListMap(12 -> 1.1259259259259258, 14 -> 0.9833333333333334, 16 -> 1.0333333333333332, 18 -> 1.2666666666666666, 27 -> 2.444444444444444, 28 -> 1.3047619047619048, 35 -> 1.1333333333333333, 36 -> 0.0, 37 -> 0.0, 53 -> 1.6666666666666667, 80 -> 1.3777777777777775, 99 -> 4.0, 878 -> 1.0777777777777777, 9648 -> 1.0, 10402 -> 0.9, 10749 -> 0.9333333333333332, 10751 -> 1.0333333333333332, 10752 -> 0.0, 10769 -> 0.0, 10770 -> 0.0))

  val conf = new SparkConf().setMaster("local[*]")

  val ss = SparkSession.builder().config(conf).getOrCreate()

  import org.elasticsearch.spark._

  case class MovieProfile(movieId: Option[Int], genreVector: linalg.Vector, popularity: Double, genreVectorSqdist: Double)


  def computeUserProfileRecommendation(userProfile: UserProfile)= {

    import io.circe._
    import io.circe.generic.auto._
    import io.circe.parser._
    import io.circe.syntax._

    val moviesRDD = ss.sparkContext.esJsonRDD(s"${MovieIndexDefinition.IndexName}/${MovieIndexDefinition.TypeName}").values
      .flatMap(s => decode[TmdbMovie](parse(s).right.toOption.getOrElse(Json.Null).noSpaces).right.toOption)
      .persist()

    val userProfileVector = Vectors.dense(userProfile.genres.values.toArray)


    val profiles: RDD[(MovieDetails, MovieProfile)] = moviesRDD.zipWithIndex.map {
      case (movie, index) =>
        val genres = movie.genres.map(_.id).filter(genre => Genre.genreReferential.contains(genre))
        val weights = genres.map(genre => genre -> (1.0 / genres.size)).toMap
        val vector = Genre.genreReferential.map(genre => weights.getOrElse(genre._1, 0.0))
        MovieDetails.fromTmdbMovie(movie) -> MovieProfile(
          movieId = movie.id,
          genreVector = Vectors.dense(vector.toArray),
          popularity = movie.popularity.getOrElse(0.0),
          genreVectorSqdist = 0.0
        )
    }

    val recommendations = profiles.map {
      case (movie: MovieDetails, profile: MovieProfile) =>
      movie.copy(sqdist = Some(Vectors.sqdist(userProfileVector, profile.genreVector)))
    }
      .sortBy(field => -(field.popularity.getOrElse(0.0) / 100) + field.sqdist.getOrElse(0.0))
      .take(50)
      .toSeq

    recommendations
  }

  def main(args: Array[String]): Unit = {

    computeUserProfileRecommendation(mockedUserProfile).foreach(println)

  }


}
