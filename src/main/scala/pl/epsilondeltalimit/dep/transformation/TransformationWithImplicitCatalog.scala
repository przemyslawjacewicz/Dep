package pl.epsilondeltalimit.dep.transformation

import pl.epsilondeltalimit.dep.catalog.Catalog

trait TransformationWithImplicitCatalog {
  def apply(implicit c: Catalog): Catalog
}
