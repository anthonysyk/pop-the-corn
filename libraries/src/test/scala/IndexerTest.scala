import models.{Batch, DiscoveredMovie}
import org.scalatest.FunSuite
import ptc.libraries.{AkkaHelper, CirceHelper}
import io.circe._
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._

/**
  * Created by Anthony on 19/09/2017.
  */
class IndexerTest extends FunSuite with CirceHelper{

  test("test parsing productToMap") {
    val toto = DiscoveredMovie(Some("/mCu8mJRbBD8x187A5WserM9YhOr.jpg"),Some(false),Some("During a handover to the head of counter-terrorism of MI5, Harry Pearce, a terrorist escapes custody. When Harry disappears soon after, his protégé is tasked with finding out what happened as an impending attack on London looms, and eventually uncovers a deadly conspiracy."),Some("2015-04-11"),Vector(53, 28),Some(292040),Some("Spooks: The Greater Good"),Some("en"),Some("Spooks: The Greater Good"),Some("/7qSldyO9zJl0NIFJBdLzTxRh53E.jpg"),Some(8.078558),Some(220),Some(false),Some(5.9))

    parseProductToMap(toto).foreach(println)
  }

}
