package ptc.libraries

import akka.actor.{Actor, ActorContext, ActorLogging, ActorRef, Props}
import akka.event.Logging

trait AkkaHelper extends Actor with ActorLogging {

  val logger = Logging(context.system, this)


  def createWorkers(numberOfWorkers: Int, props: Props)(implicit context: ActorContext, self: ActorRef) : Seq[ActorRef] = {
    (0 until numberOfWorkers) map (_ => context.actorOf(props))
  }

  def startWorkers(StartWorking: Object)(implicit workers: Seq[ActorRef]): Unit = workers.foreach(_ ! StartWorking)

}


