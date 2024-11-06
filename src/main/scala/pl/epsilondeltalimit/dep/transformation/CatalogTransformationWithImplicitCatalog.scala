package pl.epsilondeltalimit.dep.transformation

import pl.epsilondeltalimit.dep.catalog.Catalog

//todo: consider removing - catalog can be made implicit when working with Catalog => Catalog type
trait CatalogTransformationWithImplicitCatalog {
  def apply(implicit c: Catalog): Catalog
}
