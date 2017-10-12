import controllers.GraphQLController
import models.Genre
import org.scalatest.FunSuite

class GeneralTest extends FunSuite {

  import io.circe._
  import io.circe.generic.auto._
  import io.circe.parser._

  test("Genre To String test") {
    Genre.values.map(_.toString).foreach(println)

    assert(Genre.ScienceFiction.toString == "Science Fiction")
  }

}
