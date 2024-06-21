package pl.epsilondeltalimit.dep.transformation

import pl.epsilondeltalimit.dep.catalog.Catalog
import pl.epsilondeltalimit.dep.dep.Dep

//todo: consider adding a type parameter
trait PutTransformationWithImplicitCatalog {
  def apply(implicit c: Catalog): Dep[_]
}
