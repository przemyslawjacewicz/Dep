package pl.epsilondeltalimit.dep.v4_1_1

import scala.language.implicitConversions

class Dep[A](val uid: String, val deps: Set[String])(a: Register[A] => Register[A])
    extends (Register[A] => Register[A]) {
  override def apply(r: Register[A]): Register[A] = a(r)
}

object Dep {
  import Once.implicits._

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

  def unit0[A](uid: String)(a: => A): Dep[A] =
    new Dep[A](uid, Set.empty)(r => r.put(uid, new Once[A](a)))

  def unit1[B1, A](uid: String)(b1: B1)(a: B1 => A): Dep[A] =
    new Dep[A](uid, Set.empty)(r => r.put(uid, new Once[A](a(b1))))

  def map2[A](uid: String)(aUid: String, bUid: String)(f: (A, A) => A): Dep[A] =
    new Dep[A](uid, Set(aUid, bUid))(r => r.put(uid, f(r.get(aUid)(), r.get(bUid)())))

  def mapN[A](uid: String)(deps: String*)(f: Seq[A] => A): Dep[A] =
    new Dep[A](uid, deps.toSet)(r => r.put(uid, f(deps.map(dUid => r.get(dUid)()))))

}
