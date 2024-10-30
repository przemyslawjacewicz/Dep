package pl.epsilondeltalimit.dep.transformation

import pl.epsilondeltalimit.dep.catalog.Catalog

trait CatalogTransformationImplicit extends Transformation {
  def apply(implicit c: Catalog): Catalog
}
