package pl.epsilondeltalimit.dep.v6

import org.apache.spark.sql.DataFrame

import scala.collection.mutable

class Catalog {
  private val s: mutable.Set[Dep] = mutable.Set.empty

  private def byId: Map[String, Dep] =
    s.map(_dep => _dep.id -> _dep).toMap

  def unit(id: String, value: => DataFrame): Catalog = {
    s += new Dep(id, () => Set.empty[String])(() => value)
    this
  }

  def get(id: String): Dep =
    byId.getOrElse(id, new Dep(id, () => byId(id).deps())(() => byId(id)()))

  def put(dep: Dep): Catalog = {
    s += dep
    this
  }

  def deps(id: String): Seq[Set[String]] =
    // todo: recursively extract dependencies
    Seq(Set("a"), Set("b"))
}
