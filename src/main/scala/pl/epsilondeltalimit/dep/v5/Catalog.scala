package pl.epsilondeltalimit.dep.v5

import org.apache.spark.sql.DataFrame

import scala.collection.mutable

trait Catalog {
  def unit(dep: Dep): Catalog

  def map(a: String)(b: String)(f: DataFrame => DataFrame): Catalog

  def deps: Set[Dep]
}

class SimpleImmutableCatalog extends Catalog {
  private val s: mutable.Set[Dep] = mutable.Set.empty

  override def unit(dep: Dep): Catalog = {
    s += dep
    this
  }

  override def map(a: String)(b: String)(f: DataFrame => DataFrame): Catalog = {
    s += new Dep(b, Set(a))(f(s.map(dep => dep.id -> dep).toMap.apply(a).apply()))
    this
  }

  override def deps: Set[Dep] =
    s.toSet
}
