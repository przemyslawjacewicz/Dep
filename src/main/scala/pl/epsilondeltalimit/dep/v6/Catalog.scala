package pl.epsilondeltalimit.dep.v6

import org.apache.spark.sql.DataFrame

import scala.annotation.tailrec
import scala.collection.mutable

class Catalog {
  private val s: mutable.Set[Dep] = mutable.Set.empty

  private def byId: Map[String, Dep] =
    s.map(_dep => _dep.id -> _dep).toMap

  private def deps(id: String): Seq[Set[String]] = {
    @tailrec
    def go(_deps: Set[Dep], _stages: Seq[Set[String]]): Seq[Set[String]] = {
      val _depsId = _deps.flatMap(_.deps())
      if (_depsId.isEmpty) _stages else go(_depsId.map(byId), _depsId +: _stages)
    }

    go(Set(byId(id)), Seq(Set(id))).reverse
  }

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

  def eval(id: String): DataFrame = {
    deps(id).foreach(_depsId => _depsId.par.foreach(_depId => get(_depId)()))
    get(id)()
  }
}
