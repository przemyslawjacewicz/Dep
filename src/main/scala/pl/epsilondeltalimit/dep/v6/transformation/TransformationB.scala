package pl.epsilondeltalimit.dep.v6.transformation

import pl.epsilondeltalimit.dep.v6.{Catalog, Transformation}

object TransformationB extends Transformation {
  override def apply(catalog: Catalog): Catalog = {
    val a = catalog.get("a")

    val b = a.map("b")(df => {
      println("evaluating b")
      df.distinct()
    })

    catalog.put(b)
  }
}
