package pl.epsilondeltalimit.dep.v6

object Runner1 {
  def main(args: Array[String]): Unit = {
    val transformations: Set[Transformation] = Set(TransformationB, TransformationA)

    val catalog: Catalog = transformations.foldLeft(new Catalog)((c, t) => t(c))

    catalog.deps("b").foreach(ids => ids.par.foreach(id =>  catalog.get(id)() ) )

    catalog.get("b")().show()
  }
}
