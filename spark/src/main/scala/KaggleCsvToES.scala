import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

object KaggleCsvToES extends ReadCsvHelper {

  val conf = Map(
    "app.name" -> "KaggleToEs",
    "es.index.auto.create" -> "true",
    "es.port" -> "9200",
    "es.nodes" -> "127.0.0.1"
  )

  val sparkConf = new SparkConf().setMaster("local[*]")

  val ss: SparkSession = SparkSession.builder().config(sparkConf).getOrCreate()


  def main(args: Array[String]): Unit = {

    import org.elasticsearch.spark._
    ss.sparkContext.parallelize(serializeMoviesFromCsv).coalesce(20).saveToEs("movies_index/movie")


  }

}