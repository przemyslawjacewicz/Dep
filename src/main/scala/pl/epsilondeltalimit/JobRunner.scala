package pl.epsilondeltalimit

import org.apache.spark.sql.types._
import org.apache.spark.sql.{DataFrame, Row, SparkSession}
import pl.epsilondeltalimit.JobDesign.Job

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

object JobRunner {
  def main(args: Array[String]): Unit = {

    val job = new Job[DataFrame] {
      override def apply(spark: SparkSession): DataFrame = {
        spark.createDataFrame(
          spark.sparkContext.parallelize(Seq(
            Row(1, 1L, "a")
          )),
          StructType(Seq(
            StructField("f1", IntegerType),
            StructField("f2", LongType),
            StructField("f3", StringType),
          )))
      }
    }

    val spark = SparkSession.builder
      .appName("Runner")
      .master("local[2]")
      .config("spark.sql.shuffle.partitions", "1")
      .config("spark.sql.parallelism", "1")
      .config("spark.ui.enabled", "false")
      .getOrCreate()

    println("Immediate execution")
    job(spark).show()

    println("Asynchronous execution")
    JobDesign.async(spark)(job).onComplete {
      case Success(df) => df.show()
      case Failure(ex) => ex.printStackTrace()
    }


  }
}
