package pl.epsilondeltalimit.context

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, Promise}

// this context cannot be shard between many jobs
class SimpleImmutableContext[T <: {val id: String}](val m: Map[String, Future[T]] = Map.empty[String, Future[T]]) extends Context[T] {

  private val ps: mutable.Map[String, Promise[Unit]] = mutable.Map.empty

  override def register(t: T, f: => Future[T]): Context[T] = {
    val p = Promise[Unit]
    ps.put(t.id, p)
    p.success(())

    lazy val lazyF = f
    new SimpleImmutableContext[T](m + ((t.id, lazyF)))
  }

  override def getWithId(id: String): Future[T] = ???

  override def exec(): Unit = ???
}
