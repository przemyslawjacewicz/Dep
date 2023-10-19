package pl.epsilondeltalimit.dep

import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.{DataFrame, Row, SparkSession}

object Runner1 {
  def main(args: Array[String]): Unit = {
    val transformations: Set[Transformation] =
      Set(
        (c: Catalog) => c.put {
          c.get[SparkSession]("spark").flatMap { spark =>
            c.get[DataFrame]("a").flatMap { a =>
              c.get[DataFrame]("b").map { b =>
                spark.createDataFrame(
                  spark.sparkContext.parallelize(Seq(Row(1, 1L, "1"), Row(2, 2L, "2"))),
                  StructType.fromDDL("f1 INT, f2 LONG, f3 STRING")
                )
                  .unionByName(a)
                  .unionByName(b)
              }
            }
          }
            .as("c")
        },
        (c: Catalog) => c.put {
          c.get[SparkSession]("spark").flatMap { spark =>
            c.get[DataFrame]("a").map { a =>
              spark.createDataFrame(
                spark.sparkContext.parallelize(Seq(Row(1, 1L, "1"), Row(2, 2L, "2"))),
                StructType.fromDDL("f1 INT, f2 LONG, f3 STRING")
              )
                .unionByName(a)
            }
          }
            .as("b")
        },
        (c: Catalog) => {
          val spark = c.get[SparkSession]("spark")

          val a = spark.map { _spark =>
            _spark.createDataFrame(
              _spark.sparkContext.parallelize(Seq(Row(1, 1L, "1"), Row(2, 2L, "2"))),
              StructType.fromDDL("f1 INT, f2 LONG, f3 STRING")
            )
          }.as("a")

          c.put(a)
        },
        (c: Catalog) => c.unit("spark") {
          SparkSession.builder
            .appName("Runner")
            .master("local[2]")
            .config("spark.driver.extraJavaOptions", "-Dlog4j.configuration=log4j2.properties")
            .getOrCreate()
        }
      )

    val catalog: Catalog = transformations.foldLeft(new Catalog)((_c, _t) => _t(_c))

    catalog.explain("spark")
    println(catalog.eval[SparkSession]("spark").version)

    catalog.explain("a")
    catalog.eval[DataFrame]("a").show()

    catalog.explain("b")
    catalog.eval[DataFrame]("b").show()

    catalog.explain("c")
    catalog.eval[DataFrame]("c").show()
  }
}
