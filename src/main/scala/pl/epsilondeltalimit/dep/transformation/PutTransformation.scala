package pl.epsilondeltalimit.dep.transformation

import pl.epsilondeltalimit.dep.catalog.Catalog
import pl.epsilondeltalimit.dep.dep.Dep

//todo: consider adding a type parameter
trait PutTransformation extends (Catalog => Dep[_])
