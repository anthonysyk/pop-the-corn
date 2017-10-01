package graphql

import nlp.MovieSimilarity
import org.apache.spark.mllib.linalg.distributed.MatrixEntry
import sangria.schema._

trait MovieSimilarityRepoQuery {

  import sangria.macros.derive.GraphQLField

  @GraphQLField
  def allMovies: Vector[MovieSimilarity]

  @GraphQLField
  def getMovieSimilaritiesById(id: Int): Option[MovieSimilarity] = {
    allMovies.find(_.idMovie == id)
  }
}

class MovieSimilarityRepo(movieSimilaritiesState: Vector[MovieSimilarity]) {

  object Query extends MovieSimilarityRepoQuery{

    override def allMovies: Vector[MovieSimilarity] = movieSimilaritiesState

  }

}

// Version classique de définir un schéma sur Sangria
object SchemaDefinitionClassic {

  val MatrixEntrySchema = ObjectType(
    "CosineSimilarity",
    "The cosine similarity between movies",
    fields[Unit, MatrixEntry](
      Field("i", LongType, resolve = _.value.i),
      Field("j", LongType, resolve = _.value.j),
      Field("value", FloatType, resolve = _.value.value)
    )
  )

  val MovieSimilaritySchema = ObjectType(
    "MovieSimilarity",
    "The similarity between each movie",
    fields[Unit, MovieSimilarity](
      Field("idIndex", LongType, resolve = _.value.idIndex),
      Field("idMovie", IntType, resolve = _.value.idMovie),
      Field("similarity", ListType(MatrixEntrySchema), resolve = _.value.similarity)
    )
  )

//  val Id = Argument("id", StringType)
//
//  val QueryType = ObjectType("Query", fields[ProductRepo, Unit](
//    Field("product", OptionType(ProductType),
//      description = Some("Returns a product with specific `id`."),
//      arguments = Id :: Nil,
//      resolve = c ⇒ c.ctx.product(c arg Id)),
//
//    Field("products", ListType(ProductType),
//      description = Some("Returns a list of all available products."),
//      resolve = _.ctx.products)))

}

// Version simplifiée/générique avec les macros de dérivation de Sangria
object SchemaDefinition {

  import sangria.macros.derive._

  implicit val MovieSimilaritySchema: ObjectType[Unit, MovieSimilarity] = deriveObjectType[Unit, MovieSimilarity]()
  implicit val MatrixEntrySchema: ObjectType[Unit, MatrixEntry] = deriveObjectType[Unit, MatrixEntry]()

  val Query: ObjectType[MovieSimilarityRepo, Unit] = deriveContextObjectType[MovieSimilarityRepo, MovieSimilarityRepoQuery, Unit](_.Query)

  val schema = Schema(Query)

}