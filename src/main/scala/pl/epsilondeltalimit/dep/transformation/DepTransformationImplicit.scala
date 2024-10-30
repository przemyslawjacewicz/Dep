package pl.epsilondeltalimit.dep.transformation

import pl.epsilondeltalimit.dep.catalog.Catalog
import pl.epsilondeltalimit.dep.dep.Dep

trait DepTransformationImplicit[A] extends Transformation {
  def apply(implicit c: Catalog): Dep[A]
}
