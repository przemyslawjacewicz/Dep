package pl.epsilondeltalimit.context

import scala.concurrent.{ExecutionContext, Future}

trait AsyncJobContext[T <: {val id: String}] {

  // registers a (unresolved) future in the context
  // returns a context with with this future added, can be mutable or immutable
  def register(t: T, f: ExecutionContext => Future[T]): AsyncJobContext[T]

  // returns future registered for id if exists
  //  def getForId(id: String): Either[NoSuchElementException, Future[T]]
  def getForId(id: String): ExecutionContext => Future[T]

  // starts execution of all futures
  def exec(ec: ExecutionContext): Unit

}



