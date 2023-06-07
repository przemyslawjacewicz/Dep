package pl.epsilondeltalimit.dep.v4_1_1

import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.{DataFrame, Row, SparkSession}
import pl.epsilondeltalimit.SparkSessionProvider
import pl.epsilondeltalimit.dep.v4_1_1.Dep._

object Runner4_1_1a extends SparkSessionProvider {
  def main(args: Array[String]): Unit = {

//    val d = mapN[DataFrame]("d")("c", "b") { case c :: d :: Nil => c.unionByName(d) }

    val c = map2[DataFrame]("c")("a", "b")((a, b) => a.unionByName(b))

    val b = unit[SparkSession, DataFrame]("b")(spark) { _spark =>
      println("evaluating b")
      _spark.createDataFrame(
        _spark.sparkContext.parallelize(Seq(Row(2, 2L, "b"))),
        StructType.fromDDL("f1 INT, f2 LONG, f3 STRING")
      )
    }
    val a = unit[SparkSession, DataFrame]("a")(spark) { _spark =>
      println("evaluating a")
      _spark.createDataFrame(
        _spark.sparkContext.parallelize(Seq(Row(1, 1L, "a"))),
        StructType.fromDDL("f1 INT, f2 LONG, f3 STRING")
      )
    }

    run(new SimpleRegister[DataFrame])(c, b, a).get("c")().show()

  }
}
