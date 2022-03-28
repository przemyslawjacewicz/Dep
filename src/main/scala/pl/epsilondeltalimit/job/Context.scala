package pl.epsilondeltalimit.job

import scala.collection.mutable
import scala.concurrent.Future

//todo: T must have an id ?
//todo: blocking and not blocking context ?
trait Context[T <: {val id: String}] {

  //returns new context with registered future
  def register(t: T, f: => Future[T]): Context[T]

  def getForId(id: String): Future[T]
}

class SimpleMutableContext[T <: {val id: String}] extends Context[T] {
  private val m = mutable.Map.empty[String, Future[T]]

  override def register(t: T, f: => Future[T]): Context[T] = {
    m += ((t.id, f))
    this
  }

  override def getForId(id: String): Future[T] = m(id)
}

class SimpleImmutableContext[T <: {val id: String}](val m: Map[String, Future[T]] = Map.empty[String, Future[T]]) extends Context[T] {

  override def register(t: T, f: => Future[T]): Context[T] = new SimpleImmutableContext[T](m + ((t.id, f)))

  override def getForId(id: String): Future[T] = m(id)
}

