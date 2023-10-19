package pl.epsilondeltalimit.dep.alternatives.v4_1

import pl.epsilondeltalimit.dep._

import scala.language.implicitConversions

class Dep[A](val id: String, val deps: Set[String])(a: Catalog[A] => Catalog[A]) extends (Catalog[A] => Catalog[A]) {
  override def apply(c: Catalog[A]): Catalog[A] = a(c)
}

object Dep {
  import once.Once.implicits._

  def unit[A](id: String)(a: A): Dep[A] =
    new Dep[A](id, Set.empty)(c => c.put(id, a))

  def map2[A](id: String)(a: String, b: String)(f: (A, A) => A): Dep[A] =
    new Dep[A](id, Set(a, b))(c => c.put(id, f(c.get(a)(), c.get(b)())))

//  def mapN[A](id: String)(deps: String*)(f: Seq[A] => A): Dep[A] =
//    new Dep[A](id, deps.toSet)(c => c.put(id, f(deps.map(dId => c.get(dId)()))))

  // todo: simplistic implementation => should be replaced with a solution based on graph
  def run[A](c: Catalog[A])(deps: Dep[A]*): Catalog[A] = {
    val uidToDep = deps.foldLeft(Map.empty[String, Dep[A]]) { (acc, d) =>
      acc + (d.id -> d)
    }
    val uidToDeps = deps.foldLeft(Map.empty[String, Set[String]]) { (acc, d) =>
      acc + (d.id -> d.deps)
    }

    (uidToDeps.keys ++ uidToDeps.values.flatten).toSeq.sorted
      .foldLeft(c) { (acc, uid) =>
        val _r = uidToDep(uid)(acc)
        _r.get(uid)()
        _r
      }
  }

}
