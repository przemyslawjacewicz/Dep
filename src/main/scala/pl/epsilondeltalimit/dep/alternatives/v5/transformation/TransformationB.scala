package pl.epsilondeltalimit.dep.alternatives.v5.transformation

import pl.epsilondeltalimit.dep.alternatives.v5.{Catalog, Transformation}

object TransformationB extends Transformation {
  override def apply(c: Catalog): Catalog =
    c.map("a")("b")(a => {
      println("evaluating b")
      a.distinct()
    })
}
