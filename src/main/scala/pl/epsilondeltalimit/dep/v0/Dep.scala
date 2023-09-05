package pl.epsilondeltalimit.dep.v0

import pl.epsilondeltalimit.dep.SparkSessionProvider

import scala.collection.mutable

// example of computation we want wrap into a dependency library
// the main goal of a dependency library is the ability to resolve dependencies and execute computations so that
// all dependencies are met; this also means that computations can be coded/added to catalog in any order e.g.
// a computation 'c' depending on 'a' and 'b' can be coded/added to catalog before 'c'
object Dep extends SparkSessionProvider {

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

  def main(args: Array[String]): Unit = {
    // resources must be committed in order
    Catalog.put[Int](1, "a")
    Catalog.put[String]("two", "b")

    // a and b must be available in the catalog before evaluating c
    map2[Int, String, String]("a", "b")("c")((a, b) => s"$a$b")
    map2[Int, String, Int]("a", "b")("d")((a, b) => s"$a$b".length)

    println(Catalog.get[String]("c"))
    println(Catalog.get[Int]("d"))
  }
}
