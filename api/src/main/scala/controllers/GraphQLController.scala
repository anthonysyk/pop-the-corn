package controllers

import services.{ApiService, GraphQLService}
import io.circe._
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import models.{Genre, MovieDetails, ProductionCompany, UserProfile}

import scala.collection.immutable.ListMap
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
      .map(_.data.getMovieSimilaritiesById.similarity.map(sim => sim.j -> sim.value)).getOrElse(Nil)
      .take(15)

    val result = for {
      movies <- ApiService.getMovies(similarities.map(_._1.toString) :+ id.toString)
    } yield {
      movies.map(movie => movie.copy(tfidfSimilarity = movie.id.flatMap(movieId => similarities.toMap.get(movieId))))
    }.sortBy(_.tfidfSimilarity).reverse
      .filter(_.poster.nonEmpty)

    result.map(seq => (seq.last +: seq.init).asJson.noSpaces)
  }

  def convertQuickResultsIntoUserProfile(quickRatingResults: Seq[MovieDetails]): UserProfile = {

    val genresPreference = quickRatingResults.flatMap { result =>
      val mapping = Genre.genreReferential.filter(g => result.genres contains g._2)
      mapping.mapValues(_ => result.rating.getOrElse(0.0) / mapping.size).toSeq
    }.groupBy(_._1).mapValues(values => values.map(_._2).sum / values.size)

    val productionCompaniesPreference: Map[ProductionCompany, Double] = quickRatingResults.flatMap{ result =>
      val companies = result.companies
      result.companies.map(_ -> result.rating.getOrElse(0.0) / companies.size)
    }.toMap

    val genreListMap: ListMap[Int, Double] = Genre.genreReferential.map(genre => genre._1 -> genresPreference.getOrElse(genre._1, 0.0))

    UserProfile(genreListMap, productionCompaniesPreference.map(value => value._1.id -> value._2))

    // Send it to graphql and retrieve movies which match the most
  }

  def getMoviesBasedOnTaste(response: String): Future[Seq[MovieDetails]] = {
    val quickRatingResults = parse(response).right.toOption.getOrElse(Json.Null).as[Seq[MovieDetails]].right.toOption.getOrElse(Nil)
    val userProfile = convertQuickResultsIntoUserProfile(quickRatingResults)
    Future(GraphQLService.getMoviesBasedOnTaste(userProfile).filterNot(movie => quickRatingResults.exists(ratedMovie => movie.id == ratedMovie.id)))
  }

}
