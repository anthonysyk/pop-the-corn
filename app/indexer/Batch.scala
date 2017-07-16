package indexer

case class Batch[T](values: Vector[T]) {

  lazy val size: Int = values.size

  def isDone: Boolean = values.isEmpty

  def next: Option[(T, Batch[T])] =
    if (isDone) None
    else Option(values.head, Batch(values.tail))
}

object Batch {
  def empty[T]: Batch[T] = Batch[T](Vector.empty[T])
}
