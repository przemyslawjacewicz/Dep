package pl.epsilondeltalimit.context

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future, Promise}

// this context can be shared between many jobs
class SimpleMutableContext[T <: {val id: String}] extends AsyncJobContext[T] {
  private val m = mutable.Map.empty[String, ExecutionContext => Future[T]]
  private val ps = mutable.Map.empty[String, Promise[Unit]]

  override def register(t: T, f: ExecutionContext => Future[T]): AsyncJobContext[T] = {
    if (!ps.contains(t.id)){
      println(s"register: promise for id=${t.id} not found, adding.")
      ps.put(t.id, Promise[Unit])
    }
    ps(t.id).success(())
    println(s"register: promise for id=${t.id} completed with success")

    m += ((t.id, f))
    this
  }

  override def getForId(id: String): ExecutionContext => Future[T] = {
    if (!ps.contains(id)) {
      println(s"getForId: promise for id=$id not found, adding.")
      ps.put(id, Promise[Unit])
    }

//        if ( isCompleted(id) ) {
//          (ec: ExecutionContext) => Future( result(id) )
//        } else {
//          (ec: ExecutionContext) => ps(id).future.flatMap(_ => m(id)(ec)  )
//        }

    (ec: ExecutionContext) => ps(id).future.flatMap(_ => m(id)(ec)  )
  }

  override def exec(ec: ExecutionContext): Unit = m.values.foreach(f => f(ec) )
}
