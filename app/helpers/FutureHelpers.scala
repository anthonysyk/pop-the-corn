package helpers

import scala.concurrent.{ExecutionContext, Future}

trait FutureHelpers {

  implicit class RichFuture[A](future: Future[A]) {

    def thenSideEffect(block: A => Unit)(implicit ec: ExecutionContext): Future[A] = {
      future.map { result =>
        block(result)
        result
      }
    }
  }

}
