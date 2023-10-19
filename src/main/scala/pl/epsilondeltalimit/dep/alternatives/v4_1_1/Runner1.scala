package pl.epsilondeltalimit.dep.alternatives.v4_1_1

import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.{DataFrame, Row, SparkSession}
import pl.epsilondeltalimit.dep.alternatives.v4_1_1.Dep._

object Runner1 {

  implicit lazy val spark: SparkSession = SparkSession.builder
    .appName("Runner")
    .master("local[2]")
    .config("spark.driver.extraJavaOptions", "-Dlog4j.configuration=log4j2.properties")
    .getOrCreate()

  def main(args: Array[String]): Unit = {
    val c = map2[DataFrame]("c")("a", "b")((a, b) => a.unionByName(b))

    val b = unit[DataFrame]("b") {
      println("evaluating b")
      spark.createDataFrame(
        spark.sparkContext.parallelize(Seq(Row(2, 2L, "b"))),
        StructType.fromDDL("f1 INT, f2 LONG, f3 STRING")
      )
    }
    val a = unit[DataFrame]("a") {
      println("evaluating a")
      spark.createDataFrame(
        spark.sparkContext.parallelize(Seq(Row(1, 1L, "a"))),
        StructType.fromDDL("f1 INT, f2 LONG, f3 STRING")
      )
    }

    run(new SimpleMutableCatalog[DataFrame])(c, b, a).get("c")().show()
  }
}
