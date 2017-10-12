package nlp

import breeze.numerics.log
import edu.stanford.nlp.process.Morphology
import io.circe.Json
import io.circe.parser.{decode, parse}
import models.TmdbMovie
import org.apache.spark.SparkConf
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{SaveMode, SparkSession}
import ptc.libraries.MovieIndexDefinition
import io.circe._
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._

object TFIDF {

  case class Article(id: Int, words: Seq[String], score: Seq[(String, Double)] = Nil)

  import org.apache.log4j.{Level, Logger}

  Logger.getLogger("org").setLevel(Level.OFF)
  Logger.getLogger("akka").setLevel(Level.OFF)

  val conf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("TFIDF")
  implicit val ss: SparkSession = SparkSession.builder().config(conf).getOrCreate()

  import org.elasticsearch.spark._

  def main(args: Array[String]): Unit = {

    val moviesRDD: RDD[TmdbMovie] = ss.sparkContext.esJsonRDD(s"${MovieIndexDefinition.IndexName}/${MovieIndexDefinition.TypeName}").values
      .flatMap(s => decode[TmdbMovie](parse(s).right.toOption.getOrElse(Json.Null).noSpaces).right.toOption)
      .persist()

    // Découpage des documents en sacs de mots
    val bagsOfWords: RDD[Article] = moviesRDD.collect {
      case movie if movie.overview.nonEmpty && movie.id.nonEmpty && movie.title.nonEmpty =>
        val overview: Seq[String] = movie.overview.get.toLowerCase.replaceAll("\\p{P}(?=\\s|$)", "").split("\\s")
          .filter(_.length > 3)
          .filter(_.forall(_.isLetter))
          .toSeq
        Article(movie.id.getOrElse(-scala.math.random.toInt), overview, Nil)
    }.persist()


    //TODO: Enlever les prénoms
    // Création d'un vocabulaire trié par nombre d'occurence (popularité) dans l'ensemble de nos documents
    val dictionnaryBroadcast: Broadcast[Array[String]] = ss.sparkContext.broadcast(bagsOfWords
      .flatMap(_.words.map { word =>
        val NlpHelper = new Morphology()
        NlpHelper.stem(word) -> 1
      })
      .reduceByKey(_ + _)
      .keys.collect()
      .filterNot(NLPHelper.getStopwords.contains)
      .filterNot(NLPHelper.getFirstNames.contains)
    )

    val numberOfArticles = bagsOfWords.count


    // Frequence du term dans l'ensemble des documents
    val inverseDocumentFrequency: RDD[(String, Double)] = bagsOfWords
      .flatMap(article => article.words.filter(dictionnaryBroadcast.value.contains).distinct.map(_ -> 1))
      .reduceByKey(_ + _)
      .map {
        case (termLabel, numberOfArticlesContainingTerm) =>
          termLabel -> log(numberOfArticles / numberOfArticlesContainingTerm.toDouble)
      }.sortBy(_._2, false)
      .persist

    // Fréquence du term dans un article
    val termFrequencyByArticle: RDD[Article] = bagsOfWords
      .map { article =>
        val numberOfWords = article.words.length
        val score: Seq[(String, Double)] = article.words.filter(dictionnaryBroadcast.value.contains)
          .map(word => word -> 1)
          .groupBy(_._1)
          .mapValues(value => value.map(_._2).sum.toDouble / numberOfWords)
          .toSeq
        article.copy(score = score)
      }.persist

    val broadcastIDF = ss.sparkContext.broadcast(inverseDocumentFrequency.collect().toSeq).value

    val articlesWithTFIDF: RDD[Article] = termFrequencyByArticle.map { article =>
      val idfs: Seq[(String, Double)] = broadcastIDF.filter(idf => article.score.map(_._1).contains(idf._1))
      val tfidf: Seq[(String, Double)] = (article.score ++ idfs).groupBy(_._1).mapValues(_.map(_._2).product).toSeq.sortBy(_._2).reverse
      article.copy(score = tfidf)
    }

    import ss.implicits._
    articlesWithTFIDF.toDF.coalesce(1).write.mode(SaveMode.Overwrite).parquet("mlapi/src/main/resources/tfidf-movies")
    ss.close()
  }

}