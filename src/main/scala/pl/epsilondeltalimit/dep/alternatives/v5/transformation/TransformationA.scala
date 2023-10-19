package pl.epsilondeltalimit.dep.alternatives.v5.transformation

import org.apache.spark.sql.Row
import org.apache.spark.sql.types.StructType
import pl.epsilondeltalimit.dep.alternatives.v5.{Catalog, Dep, Transformation}

object TransformationA extends Transformation {
  override def apply(c: Catalog): Catalog =
    c.unit(
      {
        println("evaluating a")
        new Dep("a", Set.empty[String])(
          spark.createDataFrame(
            spark.sparkContext.parallelize(Seq(Row(1, 1L, "1"), Row(1, 1L, "1"), Row(2, 2L, "2"))),
            StructType.fromDDL("f1 INT, f2 LONG, f3 STRING")
          )
        )
      }
    )
}
