package pl.epsilondeltalimit.dep.v5

import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.{DataFrame, Row}
import pl.epsilondeltalimit.SparkSessionProvider
import pl.epsilondeltalimit.dep.v5.Dep._

object Runner5a extends SparkSessionProvider {
  def main(args: Array[String]): Unit = {
    val r = new SimpleRegister[DataFrame]

    val cr = r.map2("c")("a", "b") { (a: DataFrame, b: DataFrame) =>
      println("evaluating c")
      a.unionByName(b)
    }
    val bcr = cr.unit("b") {
      println("evaluating b")
      spark.createDataFrame(
        spark.sparkContext.parallelize(Seq(Row(2, 2L, "b"))),
        StructType.fromDDL("f1 INT, f2 LONG, f3 STRING")
      )
    }
    val abcr = bcr.unit("a") {
      println("evaluating a")
      spark.createDataFrame(
        spark.sparkContext.parallelize(Seq(Row(1, 1L, "a"))),
        StructType.fromDDL("f1 INT, f2 LONG, f3 STRING")
      )
    }

    abcr.get("c").show()
  }
}
