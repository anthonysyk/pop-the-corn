import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import ptc.libraries.{CirceHelper, EsClient, MovieIndexDefinition, SuggestIndexDefinition}
import io.circe._
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import models.{Suggestion, SuggestionES, TmdbMovie}
import org.apache.spark.rdd.RDD

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.duration._

object SuggestionMovieIndexer extends CirceHelper with EsClient {

  val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("SuggestionIndexer")
  val ss: SparkSession = SparkSession.builder().config(sparkConf).getOrCreate()

  import org.apache.log4j.{Level, Logger}

  Logger.getLogger("org").setLevel(Level.OFF)
  Logger.getLogger("akka").setLevel(Level.OFF)

  import org.elasticsearch.spark._

  def main(args: Array[String]): Unit = {

    val IndexAndType = s"${SuggestIndexDefinition.IndexName}/${SuggestIndexDefinition.TypeName}"

    val isIndexCreated = upsertIndex(SuggestIndexDefinition.esIndexConfiguration).await(2.seconds)

    if (isIndexCreated) {
      val moviesRDD: RDD[SuggestionES] = ss.sparkContext.esJsonRDD(s"${MovieIndexDefinition.IndexName}/${MovieIndexDefinition.TypeName}").values
        .flatMap(s => decode[TmdbMovie](parse(s).right.toOption.getOrElse(Json.Null).noSpaces).right.toOption)
        .map(_.suggestionES)
        .persist()

      moviesRDD.coalesce(20).saveToEs(IndexAndType)
    }

  }

}