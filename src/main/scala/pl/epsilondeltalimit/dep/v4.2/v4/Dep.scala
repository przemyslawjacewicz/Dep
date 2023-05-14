package pl.epsilondeltalimit.dep.v4.2.v4

import scala.language.implicitConversions

class Dep(val uid: String, val deps: Set[String])(a: Register => Register)
    extends (Register => Register) {
  override def apply(r: Register): Register = a(r)
}

object Dep {
  import Once.implicits._

  def unit[A](uid: String)(a: => A): Dep[A] =
    new Dep[A](uid, Set.empty)(r => r.put(uid, a))

  def map2[A](uid: String)(aUid: String, bUid: String)(f: (A, A) => A): Dep[A] =
    new Dep[A](uid, Set(aUid, bUid))(r => r.put(uid, f(r.get(aUid)(), r.get(bUid)())))

  def mapN[A](uid: String)(deps: String*)(f: Seq[A] => A): Dep[A] =
    new Dep[A](uid, deps.toSet)(r => r.put(uid, f(deps.map(dUid => r.get(dUid)()))))

  // todo: simplistic implementation => should be replaced with a solution based on graph
  def run[A](r: Register[A])(deps: Dep[A]*): Register[A] = {
    val uidToDep = deps.foldLeft(Map.empty[String, Dep[A]]) { (acc, d) =>
      acc + (d.uid -> d)
    }
    val uidToDeps = deps.foldLeft(Map.empty[String, Set[String]]) { (acc, d) =>
      acc + (d.uid -> d.deps)
    }

    (uidToDeps.keys ++ uidToDeps.values.flatten).toSeq.sorted
      .foldLeft(r) { (acc, uid) =>
        val _r = uidToDep(uid)(acc)
        _r.get(uid)()
        _r
      }
  }
}
