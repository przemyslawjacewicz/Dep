package pl.epsilondeltalimit.dep

import pl.epsilondeltalimit.dep.catalog.Catalog
import pl.epsilondeltalimit.dep.catalog.untyped.UntypedCatalog
import pl.epsilondeltalimit.dep.transformation.implicits._

object Runner {
  def main(args: Array[String]): Unit = {

    val c =
      (c: Catalog) => c.put(c.get[String]("b1").flatMap(b1 => c.get[String]("b2").map(b2 => b1 + b2 + "_c")).as("c"))

    val b2 = (c: Catalog) => c.put(c.get[String]("a").map(_ + "_b2").as("b2"))
    val b1 = (c: Catalog) => c.put(c.get[String]("a").map(_ + "_b1").as("b1"))
    val b  = (c: Catalog) => c.put(c.get[String]("a").map(_ + "_b").as("b"))

    val a = (c: Catalog) => c.put("a")("a")

    val catalog: Catalog = (new UntypedCatalog)
      .withTransformations(c, b2, b1, b, a)

    println("=== c ===")
    println(catalog.explain("c"))
    println(catalog.eval[String]("c"))

  }
}
