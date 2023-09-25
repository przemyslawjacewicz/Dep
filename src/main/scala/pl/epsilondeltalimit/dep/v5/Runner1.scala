package pl.epsilondeltalimit.dep.v5

import pl.epsilondeltalimit.dep.v5.transformation.{TransformationA, TransformationB, TransformationC}

object Runner1 {
  def main(args: Array[String]): Unit = {
    val transformations: Set[Transformation] = Set(TransformationC, TransformationB, TransformationA)

    val catalog: Catalog = transformations.foldLeft(new SimpleImmutableCatalog: Catalog)((c, t) => t(c))

    val evaluator: Evaluator = new AlphabeticalEvaluator

    val c = evaluator("c")(catalog) //todo: evaluator(catalog)("c") ???

    c.show()
  }
}
