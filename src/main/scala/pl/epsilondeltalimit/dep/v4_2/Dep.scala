package pl.epsilondeltalimit.dep.v4_2

import pl.epsilondeltalimit.once._

import scala.language.implicitConversions

class Dep(val uid: String, val deps: Set[String])(a: Register => Register) extends (Register => Register) {
  override def apply(r: Register): Register =
    if (r.contains(uid)) r else a(r)
}

object Dep {
  import Once.implicits._

  def unit[A](uid: String)(a: => A): Dep =
    new Dep(uid, Set.empty)(_r => _r.put[A](uid, a))

  def unit[B1, A](uid: String)(b1: B1)(a: B1 => A): Dep =
    new Dep(uid, Set.empty)(_r => _r.put[A](uid, a(b1)))

  def unit[B1, B2, A](uid: String)(b1: B1, b2: B2)(a: (B1, B2) => A): Dep =
    new Dep(uid, Set.empty)(_r => _r.put[A](uid, a(b1, b2)))

  def unit[B1, B2, B3, A](uid: String)(b1: B1, b2: B2, b3: B3)(a: (B1, B2, B3) => A): Dep =
    new Dep(uid, Set.empty)(_r => _r.put[A](uid, a(b1, b2, b3)))

  def map2[A, B, C](uid: String)(aUid: String, bUid: String)(f: (A, B) => C): Dep =
    new Dep(uid, Set(aUid, bUid))(_r => _r.put[C](uid, f(_r.get[A](aUid)(), _r.get[B](bUid)())))

  // todo: simplistic implementation => should be replaced with a solution based on graph
  def run(r: Register)(deps: Dep*): Register = {
    val uidToDep = deps.foldLeft(Map.empty[String, Dep]) { (_acc, _dep) =>
      _acc + (_dep.uid -> _dep)
    }
    val uidToDeps = deps.foldLeft(Map.empty[String, Set[String]]) { (_acc, _dep) =>
      _acc + (_dep.uid -> _dep.deps)
    }

    (uidToDeps.keys ++ uidToDeps.values.flatten).toSeq.sorted
      .foldLeft(r) { (_acc, _uid) =>
        val _r = uidToDep(_uid).apply(_acc)
        _r.get(_uid).apply()
        _r
      }
  }

}
