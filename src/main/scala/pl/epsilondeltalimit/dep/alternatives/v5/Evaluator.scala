package pl.epsilondeltalimit.dep.alternatives.v5

import org.apache.spark.sql.DataFrame

trait Evaluator extends (String => Catalog => DataFrame)

class AlphabeticalEvaluator extends Evaluator {
  override def apply(id: String): Catalog => DataFrame =
    catalog => {
      // todo: works only for a,b,c
      val deps     = catalog.deps
      val depsById = deps.map(dep => dep.id -> dep).toMap
      val c        = depsById("c")
      val b        = depsById("b")
      val a        = depsById("a")

      a()
      b()
      c()
    }
}
