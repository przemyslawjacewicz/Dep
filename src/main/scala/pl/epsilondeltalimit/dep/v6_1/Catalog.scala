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
      val _depsId = _deps.flatMap(_.deps())
      if (_depsId.isEmpty) _stages else go(_depsId.map(byId), _depsId +: _stages)
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
    deps(id).foreach(_depsId => _depsId.par.foreach(_depId => byId(_depId)()))
    get[A](id)()
  }
}
