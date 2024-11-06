package pl.epsilondeltalimit.dep.transformation

import pl.epsilondeltalimit.dep.catalog.Catalog
import pl.epsilondeltalimit.dep.dep.Result

//todo: consider removing - catalog can be made implicit when working with Catalog => Result[A] type
trait ResultTransformationWithImplicitCatalog[A] {
  def apply(implicit c: Catalog): Result[A]
}
