package pl.epsilondeltalimit.context

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, Promise}

// this context can be shared between many jobs
class SimpleMutableContext[T <: {val id: String}] extends Context[T] {
  private val m = mutable.Map.empty[String, Future[T]]
  private val ps = mutable.Map.empty[String, Promise[Unit]]

  override def register(t: T, f: => Future[T]): Context[T] = {
    if (!ps.contains(t.id)){
      println(s"register: promise for id=${t.id} not found, adding.")
      ps.put(t.id, Promise[Unit])
    }
    ps(t.id).success(())
    println(s"register: promise for id=${t.id} completed with success")

    lazy val lazyF = f
    m += ((t.id, lazyF))
    this
  }

  override def getWithId(id: String): Future[T] = {
    if (!ps.contains(id)) {
      println(s"getForId: promise for id=$id not found, adding.")
      ps.put(id, Promise[Unit])
    }

    ps(id).future.flatMap(_ => m(id))
  }

  override def exec(): Unit = ???
}
