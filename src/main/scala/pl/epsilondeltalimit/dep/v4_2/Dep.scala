package pl.epsilondeltalimit.dep.v4_2

import scala.language.implicitConversions

class Dep(val uid: String, val deps: Set[String])(a: Register => Register) extends (Register => Register) {
  override def apply(r: Register): Register = a(r)
}

object Dep {
  import Once.implicits._

  // todo: simplistic implementation => should be replaced with a solution based on graph
  def run[A](r: Register)(deps: Dep*): Register = {
    val uidToDep = deps.foldLeft(Map.empty[String, Dep]) { (acc, d) =>
      acc + (d.uid -> d)
    }
    val uidToDeps = deps.foldLeft(Map.empty[String, Set[String]]) { (acc, d) =>
      acc + (d.uid -> d.deps)
    }

    (uidToDeps.keys ++ uidToDeps.values.flatten).toSeq.sorted
      .foldLeft(r) { (acc, uid) =>
        val _r = uidToDep(uid)(acc)
        _r.get(uid).apply()
        _r
      }
  }

  def unit[A](uid: String)(a: => A): Dep =
    new Dep(uid, Set.empty)(r => r.put[A](uid, a))

  def map2[A, B, C](uid: String)(aUid: String, bUid: String)(f: (A, B) => C): Dep =
    new Dep(uid, Set(aUid, bUid))(r => r.put[C](uid, f(r.get[A](aUid)(), r.get[B](bUid)())))

}
