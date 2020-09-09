package contentbased

import models.{Genre, MovieDetails, TmdbMovie, UserProfile}
import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import ptc.libraries.MovieIndexDefinition

object RunContentBasedRecommendation {

  val conf: SparkConf = new SparkConf().setMaster("local[*]")
  val ss: SparkSession = SparkSession.builder().config(conf).getOrCreate()

  import org.elasticsearch.spark._

  case class MovieProfile(
                           movieId: Option[Int],
                           popularity: Double,
                           genreWeights: Map[Int, Double],
                           genreProfile: Seq[Int]
                         )

  import io.circe._
  import io.circe.generic.auto._
  import io.circe.parser._

  var movies = Vector.empty[(MovieDetails, MovieProfile)]

  // FIXME: trouver un moyen de stocker en mémoire plus classe
  def computeMoviesState() = {
    val moviesRDD = ss.sparkContext.esJsonRDD(s"${MovieIndexDefinition.IndexName}/${MovieIndexDefinition.TypeName}").values
      .flatMap(s => decode[TmdbMovie](parse(s).right.toOption.getOrElse(Json.Null).noSpaces).right.toOption)
      .persist()

    val movieProfiles: RDD[(MovieDetails, MovieProfile)] = moviesRDD.map { movie =>
      val genres = movie.genres.map(_.id).filter(genre => Genre.genreReferential.contains(genre))
      val genreWeightsSparse = genres.map(genre => genre -> 1.0 / genres.size).toMap
      val genreWeights = Genre.genreReferential.map(genre => genre._1 -> genreWeightsSparse.getOrElse(genre._1, 0.0))
      val genreProfile: Seq[Int] = movie.genres.map(_.id)

      MovieDetails.fromTmdbMovie(movie) -> MovieProfile(
        movieId = movie.id,
        popularity = movie.popularity.getOrElse(0.0),
        genreWeights = genreWeights,
        genreProfile = genreProfile
      )
    }.filter(_._1.genres.nonEmpty).persist

    movies = movieProfiles.collect().toVector
    println(s"${movies.size} films chargés")
  }

  def computeUserProfileRecommendation(userProfile: UserProfile) = {

    val genrePrefered: Map[Seq[Int], Double] = userProfile.genres.map(genreProfile => genreProfile.ids -> genreProfile.preference).toMap

    val result = movies.map { case (movieDetails, movieProfile) =>

      // FiXME: Logique recommendations sur le genre -- nombreDeGenreEnCommun * Note Utilisateur sur ce profil
      val highestGenreRating: Double = genrePrefered.map {
        case (genreIds, rating) if rating <= 2.0 => (genreIds.count(id => movieProfile.genreProfile.contains(id)) / genreIds.size) * (rating - (1 / rating))
        case (genreIds, rating) if rating >= 3.0 => (genreIds.count(id => movieProfile.genreProfile.contains(id)) / genreIds.size) * rating
      }.max

      movieDetails.copy(
        highestSimilarityProfile = Option(highestGenreRating),
        genreWeights = Genre.genreReferential.map(genre => genre._2 -> movieProfile.genreWeights.getOrElse(genre._1, 0.0))
      )
    }
      .sortBy { features =>
        -features.highestSimilarityProfile.getOrElse(0.0)
      }.take(50)
      .sortBy(res => -res.popularity.getOrElse(0.0))

    result.map(res => res.title -> res.genres -> res.highestSimilarityProfile.getOrElse(0.0)).foreach(println)

    result.toSeq

    //    case class UserProfileVector(genre: linalg.Vector, company: linalg.SparseVector)
    //
    //    val userProfileVector = UserProfileVector(
    //      genre = Vectors.dense(userProfile.genres.values.toArray),
    //      company = Vectors.dense(
    //        referential.map(company => userProfile.companies.toMap.getOrElse(company._1.toInt, 0.0)).toArray
    //      ).toSparse
    //    )
    //
    //    val recommendations = movieProfiles.map {
    //      case (movie: MovieDetails, movieProfile: MovieProfile) =>
    //        movie.copy(
    //          highestSimilarityProfile = Some(Vectors.sqdist(userProfileVector.genre, movieProfile.genreVector)),
    ////          companySqdist = Some(Vectors.sqdist(userProfileVector.company, movieProfile.companyVector)),
    //          companySqdist = None,
    //          genreWeights = Genre.genreReferential.map(ref => ref._2 -> movieProfile.genreWeights.getOrElse(ref._1, 0.0)),
    //          companiesWeights = movieProfile.companyWeights.flatMap(company => movie.companies.map(movieCompany => movieCompany.name -> company._2))
    //        )
    //    }
    //      .sortBy { field =>
    //        (field.highestSimilarityProfile.getOrElse(0.0) * 100 )
    //  }
    //      //FIXME: SqDist bof + (field.companySqdist.getOrElse(0.0) * 1)
    //      .take(50)
    //      .toSeq
    //
    //    println(s"${recommendations.size} recommendations sent")
    //    movieProfiles.unpersist()
    //
    //    recommendations
  }

  def main(args: Array[String]): Unit = {
    import io.circe.generic.auto._
    import io.circe.parser._

    val json = """{"genres":[{"ids":[12,28,80],"preference":1.0},{"ids":[12,18,878,10749],"preference":1.0},{"ids":[12,18,878],"preference":1.0},{"ids":[27,53],"preference":1.0},{"ids":[18,80],"preference":1.0},{"ids":[14,27,28],"preference":1.0},{"ids":[12,28,878],"preference":1.0},{"ids":[12,14,28,878],"preference":1.0},{"ids":[12,14,28,80,878],"preference":1.0},{"ids":[16,35,10751],"preference":4.0},{"ids":[18,53,878,9648],"preference":1.0},{"ids":[27],"preference":1.0},{"ids":[14,16,10402,10749,10751],"preference":4.0},{"ids":[18,35,10402,10749],"preference":1.0},{"ids":[28,53],"preference":1.0},{"ids":[12,16,35,10751],"preference":4.0},{"ids":[12,28,35],"preference":1.0},{"ids":[99],"preference":1.0}],"companies":[[6623,1.3333333333333333],[7038,1.3333333333333333],[91331,1.3333333333333333],[2,2.0],[6125,2.0],[33,2.0],[6704,2.0],[264,0.2],[420,0.2],[3036,0.2],[84424,0.2],[84425,0.2],[306,0.16666666666666666],[1645,0.16666666666666666],[2735,0.16666666666666666],[6408,0.16666666666666666],[22213,0.16666666666666666],[28788,0.16666666666666666],[2,0.5],[420,0.5],[306,0.2],[7505,0.2],[11307,0.2],[22213,0.2],[78091,0.2],[4,0.5],[10211,0.5],[5,0.3333333333333333],[10761,0.3333333333333333],[69434,0.3333333333333333],[429,0.2],[444,0.2],[507,0.2],[6194,0.2],[9993,0.2],[491,0.2],[6194,0.2],[23008,0.2],[36259,0.2],[36433,0.2],[491,0.2],[2527,0.2],[10161,0.2],[33681,0.2],[53247,0.2],[2,1.3333333333333333],[6125,1.3333333333333333],[10282,1.3333333333333333],[5,0.14285714285714285],[79,0.14285714285714285],[333,0.14285714285714285],[7193,0.14285714285714285],[19961,0.14285714285714285],[34034,0.14285714285714285],[83838,0.14285714285714285],[2740,0.3333333333333333],[17980,0.3333333333333333],[19900,0.3333333333333333],[94512,1.0],[1030,0.5],[1793,0.5],[33,0.3333333333333333],[3172,0.3333333333333333],[12236,0.3333333333333333],[126,0.3333333333333333],[3287,0.3333333333333333],[7738,0.3333333333333333],[12,0.14285714285714285],[444,0.14285714285714285],[11565,0.14285714285714285],[31375,0.14285714285714285],[47502,0.14285714285714285],[54392,0.14285714285714285],[76907,0.14285714285714285],[4,0.25],[2575,0.25],[7493,0.25],[32300,0.25]]}"""
    val userProfile = decode[UserProfile](json).right.toOption

    userProfile.map(computeUserProfileRecommendation)

  }


}
