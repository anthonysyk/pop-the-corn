package graphql

import nlp.MovieSimilarity
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

object State {

  val conf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("GraphQL")
  val ss: SparkSession = SparkSession.builder().config(conf).getOrCreate()

  def createState(): MovieSimilarityRepo = {

    val path = getClass.getResource("/similarity-matrix/").getPath
    import ss.implicits._

    val movieSimilaritiesState = ss.read.parquet(path).as[MovieSimilarity].collect().toVector

    ss.close()

    new MovieSimilarityRepo(movieSimilaritiesState)

  }


}
