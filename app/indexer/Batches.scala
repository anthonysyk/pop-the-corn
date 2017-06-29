package indexer

import play.api.Logger

import scala.annotation.tailrec

case class IndexBatch(indexName: String, from: Long, size: Long)

case class Batches[T](values: Vector[T]) {

  lazy val size: Int = values.size

  def isDone: Boolean = values.isEmpty

  def next: Option[(T, Batches[T])] =
    if (isDone) None
    else Option(values.head, copy(values = values.drop(1)))

}

object Batches {
  def empty[T]: Batches[T] = Batches[T](Vector.empty[T])

  def from(startIndexBatch: IndexBatch, to: Long): Batches[IndexBatch] = {
    Logger.info(s"Creating batch = From = ${startIndexBatch.from}, to=$to, step=${startIndexBatch.size}")

    @tailrec
    def go(acc: List[IndexBatch], from: Long): List[IndexBatch] = {
      val newFrom = from + startIndexBatch.size
      val newAcc = startIndexBatch.copy(from = from) :: acc
      if (newFrom >= to) newAcc
      else go(newAcc, newFrom)
    }

    Batches(go(Nil, startIndexBatch.from).toVector)
  }
}
