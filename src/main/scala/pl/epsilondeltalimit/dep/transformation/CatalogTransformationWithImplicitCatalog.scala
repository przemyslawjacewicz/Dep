package pl.epsilondeltalimit.dep.transformation

import pl.epsilondeltalimit.dep.catalog.Catalog

trait CatalogTransformationWithImplicitCatalog {
  def apply(implicit c: Catalog): Catalog
}
