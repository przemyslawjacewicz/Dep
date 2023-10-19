package pl.epsilondeltalimit.dep.alternatives.v6.transformation

import pl.epsilondeltalimit.dep.alternatives.v6.{Catalog, Transformation}

object TransformationC extends Transformation {
  override def apply(catalog: Catalog): Catalog =
    catalog.put(
      catalog
        .get("a")
        .map("c") { df =>
          println("evaluating c")
          df.limit(1)
        })
}
