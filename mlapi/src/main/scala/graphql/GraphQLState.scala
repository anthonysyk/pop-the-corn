package graphql

import nlp.MovieSimilarity
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

object GraphQLState {

  def createState(): MovieSimilarityRepo = {

    val conf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("GraphQL")
    val ss: SparkSession = SparkSession.builder().config(conf).getOrCreate()

//    val path = getClass.getResource("/similarity-matrix/").getPath
    val path = "/sideproject/pop-the-corn/mlapi/src/main/resources/similarity-matrix"

    import ss.implicits._

    val movieSimilaritiesState = ss.read.parquet(path).as[MovieSimilarity].collect().toVector

    ss.close()

    new MovieSimilarityRepo(movieSimilaritiesState)

  }

}
