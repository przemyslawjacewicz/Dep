package pl.epsilondeltalimit.job

import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.types._
import org.apache.spark.sql.{DataFrame, Row, SparkSession}
import pl.epsilondeltalimit.job.AsyncJob.AsyncJob

import java.util.concurrent.TimeUnit
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success, Try}

//todo: use a dedicated ExecutionContext for long lasting blocking computations
object Runner {
  def main(args: Array[String]): Unit = {
    /*

    Logger.getLogger("org.apache.spark").setLevel(Level.WARN)

    //todo: how to create an async job better ?
    val asyncJob1 = new AsyncJob {
      override def apply(spark: SparkSession): Future[DataFrame] =
        Future {
//          TimeUnit.SECONDS.sleep(15)
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

    //todo: how to create an async job better ?
    val asyncJob2 = new AsyncJob {
      override def apply(spark: SparkSession): Future[DataFrame] =
        Future {
//          TimeUnit.SECONDS.sleep(15)
          spark.createDataFrame(
            spark.sparkContext.parallelize(Seq(
              Row(2, 2L, "b")
            )),
            StructType(Seq(
              StructField("f1", IntegerType),
              StructField("f2", LongType),
              StructField("f3", StringType),
            )))
        }
    }

    //    val xx =
    //      asyncJob1(spark).flatMap { df1 =>
    //        asyncJob2(spark).map { df2 =>
    //          df1.unionByName(df2)
    //        }
    //      }

    val asyncJob3 = AsyncJob.map2(asyncJob1, asyncJob2)((df1, df2) => df1.unionByName(df2))


    AsyncJob.map2(asyncJob1, asyncJob2)((df1, df2) => df1.unionByName(df2))


    println("EXEC")

    val spark = SparkSession.builder
      .appName("Runner")
      .master("local[2]")
      .config("spark.sql.shuffle.partitions", "1")
      .config("spark.sql.parallelism", "1")
      .config("spark.ui.enabled", "false")
      .getOrCreate()

    Try(Await.ready(asyncJob1(spark), 1.minute)) match {
      case Success(f) => f.value.get match {
        case Success(df) => df.show()
        case Failure(ex) => ex.printStackTrace()
      }
      case Failure(ex) => ex.printStackTrace()
    }

    Try(Await.ready(asyncJob2(spark), 1.minute)) match {
      case Success(f) => f.value.get match {
        case Success(df) => df.show()
        case Failure(ex) => ex.printStackTrace()
      }
      case Failure(ex) => ex.printStackTrace()
    }

    Try(Await.ready(asyncJob3(spark), 1.minute)) match {
      case Success(f) => f.value.get match {
        case Success(df) => df.show()
        case Failure(ex) => ex.printStackTrace()
      }
      case Failure(ex) => ex.printStackTrace()
    }

     */
  }
}
