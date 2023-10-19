package pl.epsilondeltalimit.dep.alternatives.v5.transformation

import pl.epsilondeltalimit.dep.alternatives.v5.{Catalog, Transformation}

object TransformationC extends Transformation {
  override def apply(c: Catalog): Catalog =
    c.map("a")("c")(a => {
      println("evaluating c")
      a.limit(5)
    })
}
