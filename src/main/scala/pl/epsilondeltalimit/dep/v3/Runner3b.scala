package pl.epsilondeltalimit.dep.v3

import org.apache.spark.sql.Row
import org.apache.spark.sql.types.StructType
import pl.epsilondeltalimit.SparkSessionProvider
import pl.epsilondeltalimit.dep.v3.Dep._

object Runner3b extends SparkSessionProvider {
  def main(args: Array[String]): Unit = {
    val a = unit("a") {
      println("evaluating a")
      spark.createDataFrame(
        spark.sparkContext.parallelize(Seq(Row(1, 1L, "a"))),
        StructType.fromDDL("f1 INT, f2 LONG, f3 STRING")
      )
    }
    val b = unit("b") {
      println("evaluating b")
      spark.createDataFrame(
        spark.sparkContext.parallelize(Seq(Row(2, 2L, "b"))),
        StructType.fromDDL("f1 INT, f2 LONG, f3 STRING")
      )
    }

    run(map2("c")(a, b)((aDF, bDF) => aDF.unionByName(bDF))).show()

    run(map2("d")(a, b)((aDF, bDF) => aDF.crossJoin(bDF))).show()

  }
}
