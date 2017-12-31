import models._
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

    println(parseProductToMap(toto))
  }

  test("test parsing productToMap TmdbMovie1") {
    val toto = TmdbMovie(Some(false),Some("/qJ0MU3Ln21q7KqcM5UUeL9KjjMv.jpg"),None,Some(0.0),Vector(),None,Some(391899),Some("tt5612850"),Some("en"),Some("Hannibal Buress: Hannibal Takes Edinburgh"),Some("Hannibal Buress braves Scotland's epic Fringe festival in Edinburgh, performing dozens of wry stand-up sets and testing new material on the locals."),Some(2.858017),Some("/uwKq3xnYcw18y79FOpoMKDPq0Pa.jpg"),Vector(),Vector(),Some("2016-04-08"),Some(0),Some(80),Vector(SpokenLanguage("en","English")),Some("Released"),Some(""),Some("Hannibal Buress: Hannibal Takes Edinburgh"),Some(false),Some(6.0),Some(5),None)

    println(parseProductToMap(toto))
  }

  test("test parsing productToMap TmdbMovie2") {
    val toto = Some(TmdbMovie(Some(false),Some("/gKHJBL0PoS7HiKhun15JK4aBAx9.jpg"),None,Some(0.0),Vector(Genre(27,"Horror")),Some(""),Some(408149),Some("tt5108168"),Some("en"),Some("Isle of the Dead"),Some("Strangers trapped on a secluded island struggle to survive against hordes of the dead."),Some(2.540829),Some("/9l0EGPvZEp1vK8ACBsE5Pg0jsON.jpg"),Vector(ProductionCompany("The Asylum",1311), ProductionCompany("Slightly Distorted Productions",77827)),Vector(ProductionCountry("US","United States of America")),Some("2016-12-02"),Some(0),Some(0),Vector(SpokenLanguage("en","English")),Some("Released"),None,Some("Isle of the Dead"),Some(false),Some(4.5),Some(9),None))

    println(parseProductToMap(toto))
  }

}
