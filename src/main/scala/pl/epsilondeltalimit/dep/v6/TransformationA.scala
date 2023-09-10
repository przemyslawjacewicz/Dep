package pl.epsilondeltalimit.dep.v6

import org.apache.spark.sql.Row
import org.apache.spark.sql.types.StructType

object TransformationA extends Transformation {
  override def apply(c: Catalog): Catalog =
    c.unit(
      "a", {
        println("evaluating a")
        spark.createDataFrame(
          spark.sparkContext.parallelize(Seq(Row(1, 1L, "1"), Row(1, 1L, "1"), Row(2, 2L, "2"))),
          StructType.fromDDL("f1 INT, f2 LONG, f3 STRING")
        )
      }
    )
}
