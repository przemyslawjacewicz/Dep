package pl.epsilondeltalimit.dep.v4_2

import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.{DataFrame, Row}
import pl.epsilondeltalimit.SparkSessionProvider
import pl.epsilondeltalimit.dep.v4_2.Dep._

object Runner4_2a extends SparkSessionProvider {
  def main(args: Array[String]): Unit = {
    val d = map2("d")("c", "b")((c: Long, b: DataFrame) => c + b.count())

    val c = map2("c")("a", "b")((a: DataFrame, b: DataFrame) => a.unionByName(b).count())

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

    run(new SimpleRegister)(a).get[DataFrame]("a")().show()


    println(
        run(new SimpleRegister)(c, b, a).get[Long]("c")()
    )

    println(
      run(new SimpleRegister)(d, c, b, a).get[Long]("d")()
    )

  }
}
