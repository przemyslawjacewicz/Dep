package pl.epsilondeltalimit.job

import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.types._
import org.apache.spark.sql.{Row, SparkSession}
import pl.epsilondeltalimit.job.AsyncJob.AsyncJob
import pl.epsilondeltalimit.table.Table

import java.nio.file.Files
import java.util.concurrent.TimeUnit
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object TableRunner {
  def main(args: Array[String]): Unit = {
    Logger.getLogger("org.apache.spark").setLevel(Level.ERROR)

    // a table without dependencies
    val t1: AsyncJob[Table] = (spark, context) => {
      val table = Table("t1", Files.createTempDirectory(null).resolve("t1").toString)
      context.register(table, Future {
        val df = spark.createDataFrame(
          spark.sparkContext.parallelize(Seq(Row(1, 1L, "a"))),
          StructType(Seq(
            StructField("f1", IntegerType),
            StructField("f2", LongType),
            StructField("f3", StringType),
          )))
        println("Saving table: " + table)
        df.write.format("parquet").save(table.location)
        println("Table saved: " + table)
        println("Going to sleep: " + table)
        TimeUnit.SECONDS.sleep(5)
        println("Waking up: " + table)
        table
      })
    }

    // a table with dependency
    val t2: AsyncJob[Table] = (spark, context) => {
      val table = Table("t2", Files.createTempDirectory(null).resolve("t2").toString)
      context.register(table, for {
        t1 <- context.getForId("t1")
      } yield {
        val df = t1.getDF()(spark).unionByName(
          spark.createDataFrame(
            spark.sparkContext.parallelize(Seq(Row(2, 2L, "b"))),
            StructType(Seq(
              StructField("f1", IntegerType),
              StructField("f2", LongType),
              StructField("f3", StringType),
            )))
        )
        println("Saving table: " + table)
        df.write.save(table.location)
        println("Table saved: " + table)
        table
      }
      )
    }

    val spark = SparkSession.builder
      .appName("Runner")
      .master("local[2]")
      .config("spark.sql.shuffle.partitions", "1")
      .config("spark.sql.parallelism", "1")
      .config("spark.ui.enabled", "false")
      .getOrCreate()

//    val mContext = new SimpleMutableContext[Table]
//    t1(spark, mContext)
//    t2(spark, mContext)

    t2(spark, t1(spark, new SimpleImmutableContext[Table]))


    TimeUnit.SECONDS.sleep(15)
  }
}
