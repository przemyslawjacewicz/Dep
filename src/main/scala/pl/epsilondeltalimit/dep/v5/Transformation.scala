package pl.epsilondeltalimit.dep.v5

import pl.epsilondeltalimit.dep.SparkSessionProvider

trait Transformation extends (Catalog => Catalog) with SparkSessionProvider
