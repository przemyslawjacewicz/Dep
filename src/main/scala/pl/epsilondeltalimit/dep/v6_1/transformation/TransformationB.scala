package pl.epsilondeltalimit.dep.v6_1.transformation

import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.{Row, SparkSession}
import pl.epsilondeltalimit.dep.v6_1.{Catalog, Transformation}

object TransformationB extends Transformation {
  override def apply(catalog: Catalog): Catalog =
    catalog.put(
      catalog.get[SparkSession]("spark").map{ spark =>
        spark.createDataFrame(
          spark.sparkContext.parallelize(Seq(Row(1, 1L, "1"), Row(1, 1L, "1"), Row(2, 2L, "2"))),
          StructType.fromDDL("f1 INT, f2 LONG, f3 STRING")
        )
      }
        .as("b")
    )

}
