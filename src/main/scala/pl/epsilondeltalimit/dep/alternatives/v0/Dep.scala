package pl.epsilondeltalimit.dep.alternatives.v0

import scala.collection.mutable

// example of computation we want wrap into a dependency library
// the main goal of a dependency library is the ability to resolve dependencies and execute computations so that
// all dependencies are met; this also means that computations can be coded/added to catalog in any order e.g.
// a computation 'c' depending on 'a' and 'b' can be coded/added/implemented to catalog before/regardless of addition of
// 'c'
object Dep {

  // mutable catalog of all named resources
  // catalog can be either mutable or immutable
  object Catalog {
    private val s = mutable.Map.empty[String, Any]

    // should a be unevaluated ?
    def put[A](a: A, id: String): Unit =
      s += (id -> a)

    def get[A](id: String): A =
      s(id).asInstanceOf[A]
  }

  // an example of primitive operation
  def map2[A, B, C](aId: String, bId: String)(cId: String)(f: (A, B) => C): C = {
    val c = f(Catalog.get[A](aId), Catalog.get[B](bId))
    Catalog.put(c, cId)
    c
  }
}
