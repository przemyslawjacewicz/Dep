package pl.epsilondeltalimit.dep.v4

import scala.collection.mutable
import scala.language.implicitConversions

object Dep {

  class Once[A](a: => A) extends (() => A) {
    private lazy val value = a

    override def apply(): A = value
  }

  object Once {
    implicit def value2Once[A](a: => A): Once[A] =
      new Once[A](a)
  }

  trait Register[A] {

    def put(uid: String, value: Once[A]): Register[A]

    def get(uid: String): Once[A]
  }

  class SimpleRegister[A] extends Register[A] {
    private val s: mutable.Map[String, Once[A]] = mutable.Map.empty

    override def put(uid: String, value: Once[A]): Register[A] = {
      s += (uid -> value)
      this
    }

    override def get(uid: String): Once[A] =
      s(uid)
  }

  class Dep[A](val uid: String, val deps: Set[String])(r: Register[A] => Register[A])
      extends (Register[A] => Register[A]) {
    override def apply(v1: Register[A]): Register[A] = r(v1)
  }

  def unit[A](uid: String)(a: => A): Dep[A] =
    new Dep[A](uid, Set.empty)(r => r.put(uid, a))

  def map2[A](uid: String)(aUid: String, bUid: String)(f: (A, A) => A): Dep[A] =
    new Dep[A](uid, Set(aUid, bUid))(r => {
      val a = r.get(aUid)()
      val b = r.get(bUid)()
      val c = f(a, b)
      r.put(uid, c)
    })

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
        val _r = uidToDep(uid).apply(acc)
        _r.get(uid)()
        _r
      }
  }

  def main(args: Array[String]): Unit = {}
}
