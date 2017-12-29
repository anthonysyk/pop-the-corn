package controllers

import java.util.UUID

import services.{ApiService, GraphQLService}
import io.circe._
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import models._

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

  def convertQuickResultsIntoUserProfile(quickRatingResults: Seq[MovieDetails]): (UserProfile, DisplayProfile) = {

//    val genresPreference: Map[Int, Double] = {
//      val ratingRepartitionned: Seq[(Int, Double)] = quickRatingResults.flatMap { result =>
//        val movieGenres: Map[Int, String] = Genre.genreReferential.filter(ref => result.genres.contains(ref._2))
//        movieGenres.toSeq.map(movieGenre => movieGenre._1 -> result.rating.getOrElse(0.0) / movieGenres.size)
//      }
//
//      val average: Map[Int, Double] = ratingRepartitionned.groupBy(_._1).mapValues(values => values.map(_._2).sum / values.size)
//
//      val total = average.values.sum
//
//      average.mapValues {_ / Math.abs(total)}
//
//    }



    val productionCompaniesPreference: Seq[(ProductionCompany, Double)] = quickRatingResults.flatMap { result =>
      val companies = result.companies
      result.companies.map(_ -> result.rating.getOrElse(0.0) / companies.size)
    }

    val genreCombinaisonPrefered: Map[Seq[Int], Double] = {
            quickRatingResults.map { result =>
              val movieGenres: Map[Int, String] = Genre.genreReferential.filter(ref => result.genres.contains(ref._2))
              movieGenres.toSeq.map(movieGenre => movieGenre._1) -> result.rating.getOrElse(0.0)
            }.groupBy(_._1).mapValues(combinaison => combinaison.map(_._2).sum / combinaison.size)
    }

//    val genreListMap: ListMap[Int, Double] = Genre.genreReferential.map(genre => genre._1 -> genresPreference.getOrElse(genre._1, 0.0))

    UserProfile(genreCombinaisonPrefered.map(genrePreference => GenreProfile(genrePreference._1, genrePreference._2)).toSeq, productionCompaniesPreference.map(value => value._1.id -> value._2)) ->
      DisplayProfile(
        movieId = UUID.randomUUID().toString,
        genres = Genre.genreReferential.map(genre => genre._2 -> genreCombinaisonPrefered.filter(_._1.contains(genre._1)).values.sum / genreCombinaisonPrefered.values.sum),
        companies = productionCompaniesPreference.map(value => value._1.name -> value._2).toMap
      )

    // Send it to graphql and retrieve movies which match the most
  }

  def getMoviesBasedOnTaste(quickRatingResults: Seq[MovieDetails], uuid: String): Future[Recommendation] = {
    val userProfileConverted = convertQuickResultsIntoUserProfile(quickRatingResults)
    val userProfile = userProfileConverted._1

    Future(
      Recommendation(
        userProfile = userProfileConverted._2.copy(movieId = uuid),
        recommendations = GraphQLService.getMoviesBasedOnTaste(userProfile).filterNot(movie => quickRatingResults.exists(ratedMovie => movie.id == ratedMovie.id) || movie.id.contains(378462) || movie.adult.getOrElse(false))
      )
    )
  }

}
