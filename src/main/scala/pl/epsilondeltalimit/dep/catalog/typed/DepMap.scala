package pl.epsilondeltalimit.dep.catalog.typed

import pl.epsilondeltalimit.dep.Dep
import scala.collection.mutable

//todo: consider a better name
sealed trait DepMap {
  def apply[T](key: Key[T]): mutable.Set[Dep[T]]

//  def get[T](key: Key[T]): Option[T]

  // todo: consider a different name
  def ++(that: DepMap): DepMap
}

//todo: consider a better name
class DefaultDepMap(private val inner: mutable.Map[Key[_], mutable.Set[Dep[_]]]) extends DepMap {

  override def apply[T](key: Key[T]): mutable.Set[Dep[T]] =
    inner.getOrElseUpdate(key, mutable.Set.empty).map(_.asInstanceOf[Dep[T]])

//  def get[T](key:Key[T]) : Option[T] = inner.get(key).asInstanceOf[Option[T]]

  override def ++(that: DepMap): DepMap =
    that match {
      case that: DefaultDepMap => new DefaultDepMap(this.inner ++ that.inner)
    }
}
