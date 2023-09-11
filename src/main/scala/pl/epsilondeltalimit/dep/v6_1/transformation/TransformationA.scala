package pl.epsilondeltalimit.dep.v6_1.transformation

import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.{DataFrame, Row, SparkSession}
import pl.epsilondeltalimit.dep.v6_1.{Catalog, Transformation}

object TransformationA extends Transformation[DataFrame] {
  override def apply(catalog: Catalog): Catalog = {
    val spark = catalog.get[SparkSession]("spark")

    val a = spark.map("a") { _spark =>
      _spark.createDataFrame(
        _spark.sparkContext.parallelize(Seq(Row(1, 1L, "1"), Row(1, 1L, "1"), Row(2, 2L, "2"))),
        StructType.fromDDL("f1 INT, f2 LONG, f3 STRING")
      )
    }

    catalog.put(a)
  }
}
