package pl.epsilondeltalimit.job

import org.apache.spark.sql.types._
import org.apache.spark.sql.{DataFrame, Row, SparkSession}

object RunnerDesign {

  trait Context{
    def register(id: String)(t: SparkSession => DataFrame)

    def getWithId(id: String): DataFrame
  }

  def main(args: Array[String]): Unit = {
    val context: Context = ???

    context.register("1") { spark  =>
      spark.createDataFrame(
        spark.sparkContext.parallelize(Seq(Row(1, 1L, "a"))),
        StructType(
          Seq(
            StructField("f1", IntegerType),
            StructField("f2", LongType),
            StructField("f3", StringType)
          ))
      )
    }

    context.register("2"){spark =>
      val df1 = context.getWithId("1")

      ???
    }





  }
}
