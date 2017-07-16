package indexer

import akka.actor.{Actor, ActorContext, ActorRef, Props}

import scala.reflect.ClassTag

trait AkkaHelper {

  def createWorkers[T <: Actor: ClassTag](context: ActorContext, numberOfWorkers: Int, worker: () => T) : Seq[ActorRef] = {
    (0 until numberOfWorkers) map (_ => context.actorOf(Props(worker())))
  }

  def startWorkers(workers: Seq[ActorRef], StartWorking: Object) = workers.foreach(_ ! StartWorking)

}

