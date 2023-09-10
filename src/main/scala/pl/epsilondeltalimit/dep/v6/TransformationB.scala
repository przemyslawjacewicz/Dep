package pl.epsilondeltalimit.dep.v6

object TransformationB extends Transformation {
  override def apply(c: Catalog): Catalog = {
    val a = c.get("a")

    val b = a.map("b")(df => {
      println("evaluating b")
      df.distinct()
    })

    c.put(b)
  }
}
