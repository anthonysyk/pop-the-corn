package controllers

import services.{ApiService, GraphQLService}
import io.circe._
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import models.MovieDetails

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object GraphQLController {

  // TODO: Find a cleaner way to parse graphql response
  case class MatrixEntry(
                          i: Long,
                          j: Long,
                          value: Double
                        )

  case class MovieSimilarity(
                              idIndex: Long,
                              idMovie: Int,
                              similarity: Seq[MatrixEntry]
                            )

  case class Data(
                   getMovieSimilaritiesById: MovieSimilarity
                 )

  case class SimilarityResponse(
                                 data: Data
                               )

  def getMovieSimilaritiesById(id: Int): Future[String] = {
    val similarities = GraphQLService.getMovieSimilarity(id).flatMap(json => decode[SimilarityResponse](json).right.toOption)
      .map(_.data.getMovieSimilaritiesById.similarity.map(sim => sim.j -> sim.value)).getOrElse(Nil).take(10)

    val result = for {
      movies <- ApiService.getMovies(similarities.map(_._1.toString) :+ id.toString)
    } yield {
      movies.map(movie => movie.copy(tfidfSimilarity = movie.id.flatMap(movieId => similarities.toMap.get(movieId))))
    }.sortBy(_.tfidfSimilarity).reverse

    result.map(seq => (seq.last+:seq.init).asJson.noSpaces)

  }

}
