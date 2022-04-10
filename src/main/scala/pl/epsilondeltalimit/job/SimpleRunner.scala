package pl.epsilondeltalimit.job

import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.types._
import org.apache.spark.sql.{DataFrame, Row, SparkSession}
import pl.epsilondeltalimit.job.AsyncSparkJob.AsyncSparkJob

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future, duration}
import scala.util.{Failure, Success}

object SimpleRunner {
  def main(args: Array[String]): Unit = {
    /*
    Logger.getLogger("org.apache.spark").setLevel(Level.WARN)

    //todo: how to create a job better ?
    val job = new Job[DataFrame] {
      override def apply(spark: SparkSession): DataFrame =
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

    //todo: how to create an async job better ?
    val asyncJob = new AsyncJob {
      override def apply(spark: SparkSession): Future[DataFrame] =
        Future(
          spark.createDataFrame(
            spark.sparkContext.parallelize(Seq(
              Row(1, 1L, "a")
            )),
            StructType(Seq(
              StructField("f1", IntegerType),
              StructField("f2", LongType),
              StructField("f3", StringType),
            )))
        )
    }

    /* === EXEC === */

    val spark = SparkSession.builder
      .appName("Runner")
      .master("local[2]")
      .config("spark.sql.shuffle.partitions", "1")
      .config("spark.sql.parallelism", "1")
      .config("spark.ui.enabled", "false")
      .getOrCreate()

    println("Job execution")
    job(spark).show()

    println("AsyncJob execution")
    val f = asyncJob(spark)
    f.onComplete {
      case Success(df) => df.printSchema()
      case Failure(ex) => ex.printStackTrace()
    }

    Await.ready(f, Duration(1, duration.MINUTES))

     */
  }
}
