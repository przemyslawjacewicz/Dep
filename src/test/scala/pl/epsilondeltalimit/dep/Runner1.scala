package pl.epsilondeltalimit.dep

import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.{DataFrame, Row, SparkSession}
import pl.epsilondeltalimit.dep.Transformations.{MultiPutTransformation, PutTransformation, Transformation}

object Runner1 {
  def main(args: Array[String]): Unit = {

    val putTransformations: Set[PutTransformation] = Set((c: Catalog) =>
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
        .as("c"))

    val multiPutTransformations: Set[MultiPutTransformation] =
      Set { (c: Catalog) =>
        val spark = c.get[SparkSession]("spark")

        val a = spark
          .map { _spark =>
            _spark.createDataFrame(
              _spark.sparkContext.parallelize(Seq(Row(1, 1L, "1"), Row(2, 2L, "2"))),
              StructType.fromDDL("f1 INT, f2 LONG, f3 STRING")
            )
          }
          .as("a")

        val b = spark
          .flatMap { _spark =>
            a.map { _a =>
              _spark
                .createDataFrame(
                  _spark.sparkContext.parallelize(Seq(Row(1, 1L, "1"), Row(2, 2L, "2"))),
                  StructType.fromDDL("f1 INT, f2 LONG, f3 STRING")
                )
                .unionByName(_a)
            }
          }
          .as("b")

        Seq(a, b)
      }

    val transformations: Set[Transformation] =
      Set((c: Catalog) =>
        c.unit("spark") {
          SparkSession.builder
            .appName("Runner")
            .master("local[2]")
            .config("spark.driver.extraJavaOptions", "-Dlog4j.configuration=log4j2.properties")
            .getOrCreate()
        })

    import Transformations.implicits._
    val catalog: Catalog = (new Catalog)
      .withTransformations(transformations.toSeq: _*)
      .withTransformations(putTransformations.toSeq: _*)
      .withTransformations(multiPutTransformations.toSeq: _*)

    catalog.show("spark")
    println(catalog.eval[SparkSession]("spark").version)

    catalog.show("a")
    catalog.eval[DataFrame]("a").show()

    catalog.show("b")
    catalog.eval[DataFrame]("b").show()

    catalog.show("c")
    catalog.eval[DataFrame]("c").show()
  }
}
