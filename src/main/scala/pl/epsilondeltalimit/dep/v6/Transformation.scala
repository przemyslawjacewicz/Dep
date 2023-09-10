package pl.epsilondeltalimit.dep.v6

import pl.epsilondeltalimit.dep.SparkSessionProvider

trait Transformation extends (Catalog => Catalog) with SparkSessionProvider
