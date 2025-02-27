package pl.epsilondeltalimit.dep.catalog.typed

import pl.epsilondeltalimit.dep.Dep

sealed trait Record {
  def apply[T](key: Key[T]): T

  def get[T](key: Key[T]): Option[T]

  def ++(that: Record): Record
}

//todo: consider a better name
class RecordImpl(private val inner:Map[Key[_], Any]) extends Record {

  def apply[T](key:Key[T]) : T = inner.apply(key).asInstanceOf[T]

  def get[T](key:Key[T]) : Option[T] = inner.get(key).asInstanceOf[Option[T]]

  def ++ (that:Record) = that match {
    case that:RecordImpl => new RecordImpl(this.inner ++ that.inner)
  }
}