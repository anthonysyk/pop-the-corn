import models.Batch
import org.scalatest.FunSuite
import ptc.libraries.AkkaHelper

/**
  * Created by Anthony on 19/09/2017.
  */
class IndexerTest extends FunSuite with AkkaHelper{
  var batch = Batch.empty[Int]

  test("Test when TheMovieDB don't find the movie") {
    batch = Batch(Vector(473814))
  }

  def receive: Receive = {
    case FetchMovieById(id) => "lauch webclient doget"
  }

  sealed trait TestMessage

  case class FetchMovieById(id: Int) extends TestMessage

}
