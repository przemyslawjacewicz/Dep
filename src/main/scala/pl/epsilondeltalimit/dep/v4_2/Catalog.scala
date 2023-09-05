package pl.epsilondeltalimit.dep.v4_2

import pl.epsilondeltalimit.dep.once.Once
import scala.collection.mutable

trait Catalog {
  def put[A](id: String, value: Once[A]): Catalog

  def get[A](id: String): Once[A]

  def contains(id: String): Boolean
}

class SimpleMutableCatalog extends Catalog {
  private val s = mutable.Map.empty[String, Once[_]]

  override def put[A](id: String, value: Once[A]): Catalog = {
    s += (id -> value)
    this
  }

  override def get[A](id: String): Once[A] =
    s(id).asInstanceOf[Once[A]]

  override def contains(id: String): Boolean = s.contains(id)
}
