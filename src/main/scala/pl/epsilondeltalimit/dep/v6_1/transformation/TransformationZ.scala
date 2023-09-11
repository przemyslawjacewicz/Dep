package pl.epsilondeltalimit.dep.v6_1.transformation

import org.apache.spark.sql.SparkSession
import pl.epsilondeltalimit.dep.SparkSessionProvider
import pl.epsilondeltalimit.dep.v6_1.{Catalog, Transformation}

object TransformationZ extends Transformation[SparkSession] with SparkSessionProvider {
  override def apply(catalog: Catalog): Catalog =
    catalog.unit("spark", spark)
}
