package pl.epsilondeltalimit.job

import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.types._
import org.apache.spark.sql.{Row, SparkSession}
import pl.epsilondeltalimit.context.SimpleMutableContext
import pl.epsilondeltalimit.job.AsyncSparkJob.AsyncSparkJob
import pl.epsilondeltalimit.table.Table

import java.nio.file.Files
import java.util.concurrent.TimeUnit
import scala.concurrent.{ExecutionContext, Future}

object TableRunner {
  def main(args: Array[String]): Unit = {
    Logger.getLogger("org.apache").setLevel(Level.ERROR)

    // a table without dependencies
    val t1 = Table("t1", Files.createTempDirectory(null).resolve("t1").toString)
    val job1: AsyncSparkJob[Table] = (spark, context) => {
      val f = (ec: ExecutionContext) => Future {
        val df = spark.createDataFrame(
          spark.sparkContext.parallelize(Seq(Row(1, 1L, "a"))),
          StructType(Seq(
            StructField("f1", IntegerType),
            StructField("f2", LongType),
            StructField("f3", StringType),
          )))
        df.write.format("parquet").save(t1.location)
        //        TimeUnit.SECONDS.sleep(5)

        println("Table processed: " + t1)
        t1
      }(ec)
      context.register(t1, f)
    }

    // a table with dependency
    val t2 = Table("t2", Files.createTempDirectory(null).resolve("t2").toString)
    val job2: AsyncSparkJob[Table] = (spark, context) => {
      val xx = (ec: ExecutionContext) => context.getForId("t1").andThen(f => f.map(t1 => {
        val df = t1.getDF()(spark).unionByName(
          spark.createDataFrame(
            spark.sparkContext.parallelize(Seq(Row(2, 2L, "b"))),
            StructType(Seq(
              StructField("f1", IntegerType),
              StructField("f2", LongType),
              StructField("f3", StringType),
            )))
        )
        df.write.save(t2.location)

        println("Table processed: " + t2)
        t2
      })(ec)  )(ec)

      val f = (ec: ExecutionContext) => context.getForId("t1")(ec).map(t1 => {
        val df = t1.getDF()(spark).unionByName(
          spark.createDataFrame(
            spark.sparkContext.parallelize(Seq(Row(2, 2L, "b"))),
            StructType(Seq(
              StructField("f1", IntegerType),
              StructField("f2", LongType),
              StructField("f3", StringType),
            )))
        )
        df.write.save(t2.location)

        println("Table processed: " + t2)
        t2
      })(ec)

      context.register(t2, xx)
    }

    val spark = SparkSession.builder
      .appName("Runner")
      .master("local[2]")
      .config("spark.sql.shuffle.partitions", "1")
      .config("spark.sql.parallelism", "1")
      .config("spark.ui.enabled", "false")
      .getOrCreate()

    val ec = ExecutionContext.Implicits.global

    val context = new SimpleMutableContext[Table]
    job2(spark, context)
//    job1(spark, context)

    //    val context = job2(spark, new SimpleImmutableContext[Table])
    //    val context = job2(spark, job1(spark, new SimpleImmutableContext[Table]))
    //    val context = job1(spark, job2(spark, new SimpleImmutableContext[Table]))
    context.exec(ec)

//    println("CALLBACKS")

    //    context.getForId(t1.id).foreach(f => f.onComplete {
    //      case Failure(ex) => println(s"FAILURE for table: $t1, cause: $ex")
    //      case Success(_) => println("SUCCESS for table " + t1)
    //    })

    //    context.getForId(t2.id).foreach(f => f.onComplete {
    //      case Failure(ex) => println(s"FAILURE for table: $t2, cause: $ex")
    //      case Success(_) => println("SUCCESS for table " + t2)
    //    })

    TimeUnit.SECONDS.sleep(15)
  }
}
