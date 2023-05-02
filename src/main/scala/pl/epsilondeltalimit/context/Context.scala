package pl.epsilondeltalimit.context

import scala.concurrent.Future

trait Context[T <: {val id: String}] {

  // registers a (unresolved) future in the context
  // returns a context with this future added, can be mutable or immutable
  def register(t: T, f: => Future[T]): Context[T]

  // returns future registered for id if exists
//  def getForId(id: String): Either[NoSuchElementException, Future[T]]
  def getWithId(id: String): Future[T]

  // starts execution of all futures
  def exec(): Unit
}



