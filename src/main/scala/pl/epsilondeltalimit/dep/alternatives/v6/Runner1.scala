package pl.epsilondeltalimit.dep.alternatives.v6

import pl.epsilondeltalimit.dep.alternatives.v6.transformation._

object Runner1 {
  def main(args: Array[String]): Unit = {
    val transformations: Set[Transformation] =
      Set(TransformationE, TransformationD, TransformationC, TransformationB, TransformationA)

    val catalog: Catalog = transformations.foldLeft(new Catalog)((_c, _t) => _t(_c))

    catalog.eval("e").show()
  }
}
