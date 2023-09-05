package pl.epsilondeltalimit.dep.v4_2

import pl.epsilondeltalimit.dep.once

import scala.language.implicitConversions

class Dep(val id: String, val deps: Set[String])(a: Catalog => Catalog) extends (Catalog => Catalog) {
  override def apply(c: Catalog): Catalog =
    if (c.contains(id)) c else a(c)
}

object Dep {
  import once.Once.implicits._

  def unit[A](id: String)(a: A): Dep =
    new Dep(id, Set.empty)(c => c.put[A](id, a))

  def map[A, B](id: String)(a: String)(f: A => B): Dep =
    new Dep(id, Set(a))(c => c.put[B](id, f(c.get[A](a)())))

  def map2[A, B, C](id: String)(a: String, b: String)(f: (A, B) => C): Dep =
    new Dep(id, Set(a, b))(c => c.put[C](id, f(c.get[A](a)(), c.get[B](b)())))

  // todo: simplistic implementation => should be replaced with a solution based on graph
  def run(c: Catalog)(deps: Dep*): Catalog = {
    val uidToDep = deps.foldLeft(Map.empty[String, Dep]) { (_acc, _dep) =>
      _acc + (_dep.id -> _dep)
    }
    val uidToDeps = deps.foldLeft(Map.empty[String, Set[String]]) { (_acc, _dep) =>
      _acc + (_dep.id -> _dep.deps)
    }

    (uidToDeps.keys ++ uidToDeps.values.flatten).toSeq.sorted
      .foldLeft(c) { (_acc, _uid) =>
        val _r = uidToDep(_uid).apply(_acc)
        _r.get(_uid).apply()
        _r
      }
  }

}
