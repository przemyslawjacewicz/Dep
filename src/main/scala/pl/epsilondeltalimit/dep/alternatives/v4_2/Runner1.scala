package pl.epsilondeltalimit.dep.alternatives.v4_2

import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.{DataFrame, Row, SparkSession}
import pl.epsilondeltalimit.dep.alternatives.v4_2.Dep._

object Runner1 {

  implicit lazy val spark: SparkSession = SparkSession.builder
    .appName("Runner")
    .master("local[2]")
    .config("spark.driver.extraJavaOptions", "-Dlog4j.configuration=log4j2.properties")
    .getOrCreate()

  def main(args: Array[String]): Unit = {
    val d = map2("d")("c", "b") { (_c: Long, _b: DataFrame) =>
      println("evaluating d")
      _c + _b.count()
    }

    val c = map2("c")("a", "b") { (_a: DataFrame, _b: DataFrame) =>
      println("evaluating c")
      _a.unionByName(_b).count()
    }

    val s = unit[SparkSession]("spark")(spark)

    val b = map[SparkSession, DataFrame]("b")("spark") { _spark =>
      println("evaluating b")
      _spark.createDataFrame(
        _spark.sparkContext.parallelize(Seq(Row(2, 2L, "b"))),
        StructType.fromDDL("f1 INT, f2 LONG, f3 STRING")
      )
    }
    val a = map[SparkSession, DataFrame]("a")("spark") { _spark =>
      println("evaluating a")
      _spark.createDataFrame(
        _spark.sparkContext.parallelize(Seq(Row(1, 1L, "a"))),
        StructType.fromDDL("f1 INT, f2 LONG, f3 STRING")
      )
    }

    val catalog = new SimpleMutableCatalog

    run(catalog)(s)
    println(catalog.get[SparkSession]("spark")().version)

    run(catalog)(a, s)

    catalog.get[DataFrame]("a")().show()
    catalog.get[DataFrame]("a")().show()

    run(catalog)(b, s)
    catalog.get[DataFrame]("b")().show()

    run(catalog)(d, c, b, a, s)
    catalog.get[DataFrame]("b")().show()
    println(catalog.get[Long]("d")())

  }
}
