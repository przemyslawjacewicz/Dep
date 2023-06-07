package pl.epsilondeltalimit.dep.v4_2

import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.{DataFrame, Row, SparkSession}
import pl.epsilondeltalimit.SparkSessionProvider
import pl.epsilondeltalimit.dep.v4_2.Dep._

object Runner4_2a extends SparkSessionProvider {
  def main(args: Array[String]): Unit = {
    val d = map2("d")("c", "b") { (_c: Long, _b: DataFrame) =>
      println("evaluating d")
      _c + _b.count()
    }

    val c = map2("c")("a", "b") { (_a: DataFrame, _b: DataFrame) =>
      println("evaluating c")
      _a.unionByName(_b).count()
    }

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

    val r = new SimpleMutableRegister

    run(r)(a)
    r.get[DataFrame]("a")().show()
    r.get[DataFrame]("a")().show()

    run(r)(b)
    r.get[DataFrame]("b")().show()

    run(r)(d, c, b, a)
    r.get[DataFrame]("b")().show()
    println(r.get[Long]("d")())

  }
}
