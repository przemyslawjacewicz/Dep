package pl.epsilondeltalimit.dep.transformation

import pl.epsilondeltalimit.dep.catalog.Catalog

trait Transformation extends (Catalog => Catalog)
