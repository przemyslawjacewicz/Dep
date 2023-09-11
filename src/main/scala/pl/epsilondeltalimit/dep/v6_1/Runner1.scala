package pl.epsilondeltalimit.dep.v6_1

import org.apache.spark.sql.{DataFrame, SparkSession}
import pl.epsilondeltalimit.dep.v6_1.transformation.{TransformationA, TransformationZ}

object Runner1 {
  def main(args: Array[String]): Unit = {
    val transformations: Set[Transformation[_]] =
      Set(TransformationA, TransformationZ)

    val catalog: Catalog = transformations.foldLeft(new Catalog)((_c, _t) => _t(_c))

    println(catalog.eval[SparkSession]("spark").version)

    catalog.eval[DataFrame]("a").show()
  }
}
