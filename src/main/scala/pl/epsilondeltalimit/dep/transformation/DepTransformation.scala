package pl.epsilondeltalimit.dep.transformation

import pl.epsilondeltalimit.dep.catalog.Catalog
import pl.epsilondeltalimit.dep.dep.Dep

trait DepTransformation[A] extends (Catalog => Dep[A]) with Transformation
