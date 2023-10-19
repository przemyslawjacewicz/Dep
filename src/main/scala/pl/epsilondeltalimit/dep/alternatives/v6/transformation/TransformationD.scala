package pl.epsilondeltalimit.dep.alternatives.v6.transformation

import pl.epsilondeltalimit.dep.alternatives.v6.{Catalog, Dep, Transformation}

object TransformationD extends Transformation {
  override def apply(catalog: Catalog): Catalog = {
    val a = catalog.get("a")
    val b = catalog.get("b")

    catalog.put(Dep.map2("d")(a, b) { (_a, _b) =>
      println("evaluating d")
      _a.unionByName(_b)
    })
  }
}
