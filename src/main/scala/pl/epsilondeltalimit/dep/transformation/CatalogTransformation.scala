package pl.epsilondeltalimit.dep.transformation

import pl.epsilondeltalimit.dep.catalog.Catalog

trait CatalogTransformation extends (Catalog => Catalog) with Transformation
