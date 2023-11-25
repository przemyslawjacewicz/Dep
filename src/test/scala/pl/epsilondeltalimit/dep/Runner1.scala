package pl.epsilondeltalimit.dep

import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.{DataFrame, Row, SparkSession}
import pl.epsilondeltalimit.dep.Transformations.Transformation

object Runner1 {
  def main(args: Array[String]): Unit = {

    val c: Transformation = (c: Catalog) =>
      c.put {
        c.get[SparkSession]("spark")
          .flatMap { spark =>
            c.get[DataFrame]("a").flatMap { a =>
              c.get[DataFrame]("b").map { b =>
                spark
                  .createDataFrame(
                    spark.sparkContext.parallelize(Seq(Row(1, 1L, "1"), Row(2, 2L, "2"))),
                    StructType.fromDDL("f1 INT, f2 LONG, f3 STRING")
                  )
                  .unionByName(a)
                  .unionByName(b)
              }
            }
          }
          .as("c")
      }

    val b: Transformation = (c: Catalog) =>
      c.put {
        c.get[SparkSession]("spark")
          .flatMap { spark =>
            c.get[DataFrame]("a").map { a =>
              spark
                .createDataFrame(
                  spark.sparkContext.parallelize(Seq(Row(1, 1L, "1"), Row(2, 2L, "2"))),
                  StructType.fromDDL("f1 INT, f2 LONG, f3 STRING")
                )
                .unionByName(a)
            }
          }
          .as("b")
      }

    val a: Transformation = (c: Catalog) => {
      val spark = c.get[SparkSession]("spark")

      val a = spark
        .map { _spark =>
          _spark.createDataFrame(
            _spark.sparkContext.parallelize(Seq(Row(1, 1L, "1"), Row(2, 2L, "2"))),
            StructType.fromDDL("f1 INT, f2 LONG, f3 STRING")
          )
        }
        .as("a")

      c.put(a)
    }

    val spark: Transformation =
      (c: Catalog) =>
        c.put("spark") {
          SparkSession.builder
            .appName("Runner")
            .master("local[2]")
            .config("spark.driver.extraJavaOptions", "-Dlog4j.configuration=log4j2.properties")
            .getOrCreate()
        }

    import Transformations.implicits._

    val catalog: Catalog = (new Catalog)
      .withTransformations(c, b, a, spark)

    println("=== c ===")
    catalog.show("c")
    catalog.eval[DataFrame]("c").show()

    println("=== b ===")
    catalog.show("b")
    catalog.eval[DataFrame]("b").show()

    println("=== a ===")
    catalog.show("a")
    catalog.eval[DataFrame]("a").show()

    println("=== spark ===")
    catalog.show("spark")
    println(catalog.eval[SparkSession]("spark").version)
  }
}
