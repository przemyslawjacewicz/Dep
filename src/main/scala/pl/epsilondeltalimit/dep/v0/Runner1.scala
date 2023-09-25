package pl.epsilondeltalimit.dep.v0

import pl.epsilondeltalimit.dep.v0.Dep.Catalog

object Runner1 {
  def main(args: Array[String]): Unit = {
    // resources must be committed in order
    Catalog.put[Int](1, "a")
    Catalog.put[String]("two", "b")

    // a and b must be available in the catalog before evaluating c
    Dep.map2[Int, String, String]("a", "b")("c")((a, b) => s"$a$b")
    Dep.map2[Int, String, Int]("a", "b")("d")((a, b) => s"$a$b".length)

    println(Catalog.get[String]("c"))
    println(Catalog.get[Int]("d"))
  }

}
