package nlp

import nlp.TFIDF.Article
import org.apache.spark.SparkConf
import org.apache.spark.mllib.linalg
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.linalg.distributed._
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{SaveMode, SparkSession}

case class MovieSimilarity(
                            idIndex: Long,
                            idMovie: Int,
                            similarity: Seq[MatrixEntry]
                          )

object RunTfidf {

  val conf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("RunTfidf")

  val ss: SparkSession = SparkSession.builder().config(conf).getOrCreate()

  case class MovieMatrix(indexId: Long, movieId: Int, scores: Map[String, Double], inputVector: Seq[Double], vector: linalg.SparseVector, similarities: Option[MatrixEntry] = None)

  def main(args: Array[String]): Unit = {

    val inputLocal = getClass.getResource("/tfidf-movies").getPath

    import ss.implicits._
    val articleRDD = ss.read.parquet(inputLocal).as[Article].rdd.sortBy(_.id).persist()

    println(articleRDD.count)

    val dictionnary: Seq[String] = articleRDD.flatMap { article =>
      article.words
    }.distinct().collect().toSeq

    val scoresByMovie: RDD[MovieMatrix] = articleRDD.zipWithIndex.map {
      case (movie, index) => MovieMatrix(index, movie.id, movie.score.toMap, Nil, Vectors.sparse(0, Array.emptyIntArray, Array.emptyDoubleArray).toSparse)
    }.persist

    val preCosineSimilarityMovies: RDD[MovieMatrix] = scoresByMovie.map { movieMatrix =>
      val inputVector = dictionnary.map { word =>
        movieMatrix.scores.getOrElse(word, 0.0)
      }
      val vector = Vectors.dense(inputVector.toArray).toSparse
      movieMatrix.copy(inputVector = inputVector, vector = vector)
    }

    val indexedRows: RDD[IndexedRow] = preCosineSimilarityMovies.map { movieMatrix =>
      IndexedRow(movieMatrix.indexId, movieMatrix.vector)
    }.persist

    val irm: IndexedRowMatrix = new IndexedRowMatrix(indexedRows)

    val resultMatrix: RDD[MatrixEntry] = irm.toCoordinateMatrix().transpose().toRowMatrix().columnSimilarities().entries

    val broadcastedMappingMovieIndex = ss.sparkContext.broadcast(scoresByMovie.groupBy(_.indexId).flatMapValues(_.map(_.movieId)).collectAsMap())

    val movieSimilarity = resultMatrix.groupBy(_.i).map{ row =>
      MovieSimilarity(
        row._1,
        broadcastedMappingMovieIndex.value(row._1),
        row._2.map(similarMovie => similarMovie.copy(j = broadcastedMappingMovieIndex.value(similarMovie.j))).toSeq.sortBy(_.value).reverse.take(50))
    }

    val outputLocal = "mlapi/src/main/resources/similarity-matrix"

    movieSimilarity.toDF.coalesce(1).write.mode(SaveMode.Overwrite).parquet(outputLocal)

    ss.close()
  }

}
