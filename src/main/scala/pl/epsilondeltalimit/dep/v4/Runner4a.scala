package pl.epsilondeltalimit.dep.v4

import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.{DataFrame, Row}
import pl.epsilondeltalimit.SparkSessionProvider
import pl.epsilondeltalimit.dep.v4.Dep._

object Runner4a extends SparkSessionProvider {
  def main(args: Array[String]): Unit = {
    val c = map2[DataFrame]("c")("a", "b")((a, b) => a.unionByName(b))

    val b = unit("b") {
      println("evaluating b")
      spark.createDataFrame(
        spark.sparkContext.parallelize(Seq(Row(2, 2L, "b"))),
        StructType.fromDDL("f1 INT, f2 LONG, f3 STRING")
      )
    }
    val a = unit("a") {
      println("evaluating a")
      spark.createDataFrame(
        spark.sparkContext.parallelize(Seq(Row(1, 1L, "a"))),
        StructType.fromDDL("f1 INT, f2 LONG, f3 STRING")
      )
    }

    //    c(b(a(new SimpleRegister[DataFrame]))).get("c").show()

    run(new SimpleRegister[DataFrame])(c, b, a).get("c")().show()

  }
}
