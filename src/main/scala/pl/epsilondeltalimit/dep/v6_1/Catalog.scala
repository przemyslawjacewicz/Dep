package pl.epsilondeltalimit.dep.v6_1

import scala.annotation.tailrec
import scala.collection.mutable

class Catalog {
  private val s: mutable.Set[Dep[_]] = mutable.Set.empty

  private def byId: Map[String, Dep[_]] =
    s.map(_dep => _dep.id -> _dep).toMap

  private def deps(id: String): Seq[Set[String]] = {
    @tailrec
    def go(_deps: Set[Dep[_]], _stages: Seq[Set[String]]): Seq[Set[String]] = {
      val _depsIds = _deps.flatMap(_.deps())
      if (_depsIds.isEmpty) _stages else go(_depsIds.map(byId), _depsIds +: _stages)
    }

    go(Set(byId(id)), Seq(Set(id))).reverse
  }

  def unit[A](id: String, value: => A): Catalog = {
    s += new Dep[A](id, () => Set.empty[String])(() => value)
    this
  }

  def get[A](id: String): Dep[A] =
    byId
      .getOrElse(id, new Dep[A](id, () => byId(id).deps())(() => byId(id).asInstanceOf[Dep[A]]()))
      .asInstanceOf[Dep[A]]

  def put[A](dep: Dep[A]): Catalog = {
    s += dep
    this
  }

  def eval[A](id: String): A = {
    deps(id).foreach(_depsIds => _depsIds.par.foreach(_depId => byId(_depId)()))
    get[A](id)()
  }
}
