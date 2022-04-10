package pl.epsilondeltalimit.context

import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future, Promise}

// this context cannot be shard between many jobs
class SimpleImmutableContext[T <: {val id: String}](val m: Map[String, ExecutionContext => Future[T]] = Map.empty) extends AsyncJobContext[T] {

  private val ps: mutable.Map[String, Promise[Unit]] = mutable.Map.empty

  override def register(t: T, f: ExecutionContext => Future[T]): AsyncJobContext[T] = {
    val p = Promise[Unit]
    ps.put(t.id, p)
    p.success(())

    lazy val lazyF = f
    new SimpleImmutableContext[T](m + ((t.id, lazyF)))
  }

  override def getForId(id: String): ExecutionContext => Future[T] = ???

  override def exec(ec: ExecutionContext): Unit = ???
}
