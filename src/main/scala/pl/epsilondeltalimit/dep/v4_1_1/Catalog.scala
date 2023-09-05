package pl.epsilondeltalimit.dep.v4_1_1

import pl.epsilondeltalimit.dep.once.Once

import scala.collection.mutable

trait Catalog[A] {
  def put(id: String, value: Once[A]): Catalog[A]

  def get(id: String): Once[A]
}

class SimpleMutableCatalog[A] extends Catalog[A] {
  private val s: mutable.Map[String, Once[A]] = mutable.Map.empty

  override def put(id: String, value: Once[A]): Catalog[A] = {
    s += (id -> value)
    this
  }

  override def get(id: String): Once[A] =
    s(id)
}
