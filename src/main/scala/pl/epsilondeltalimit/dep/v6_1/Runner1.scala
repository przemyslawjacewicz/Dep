package pl.epsilondeltalimit.dep.v6_1

import org.apache.spark.sql.SparkSession
import pl.epsilondeltalimit.dep.v6_1.transformation.TransformationZ

object Runner1 {
  def main(args: Array[String]): Unit = {
    val transformations: Set[Transformation[_]] =
      Set(TransformationZ)

    val catalog: Catalog = transformations.foldLeft(new Catalog)((_c, _t) => _t(_c))

    println(catalog.eval[SparkSession]("spark").version)


  }
}
