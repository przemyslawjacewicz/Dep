package pl.epsilondeltalimit.dep

import pl.epsilondeltalimit.dep.catalog.Catalog
import pl.epsilondeltalimit.dep.catalog.untyped.UntypedCatalog
import pl.epsilondeltalimit.dep.transformation.CatalogTransformation
import pl.epsilondeltalimit.dep.transformation.implicits._

object RunnerMultiple {
  def main(args: Array[String]): Unit = {
    val c: CatalogTransformation  = c => c.put(c.get[String]("b").map(_ + "c").as("c"))
    val b2: CatalogTransformation = c => c.put(c.get[String]("a").map(_ + "b2").as("b"))
    val b1: CatalogTransformation = c => c.put(c.get[String]("a").map(_ + "b1").as("b"))
    val a: CatalogTransformation  = c => c.put("a")("a")

    val catalog: Catalog = (new UntypedCatalog)
      .withTransformations(c, b1, b2, a)

    println("=== c ===")
    println(catalog.explain("c"))
    println(catalog.eval[String]("c"))
  }
}
