package pl.epsilondeltalimit.dep.v5.transformation

import pl.epsilondeltalimit.dep.v5.{Catalog, Transformation}

object TransformationB extends Transformation {
  override def apply(c: Catalog): Catalog =
    c.map("a")("b")(a => {
      println("evaluating b")
      a.distinct()
    })
}
