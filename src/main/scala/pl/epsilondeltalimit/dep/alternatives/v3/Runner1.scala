package pl.epsilondeltalimit.dep.alternatives.v3

import org.apache.hadoop.fs.Path
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.{Row, SparkSession}
import pl.epsilondeltalimit.dep.alternatives.v3.Dep._

import scala.util.Random

object Runner1 {

  implicit lazy val spark: SparkSession = SparkSession.builder
    .appName("Runner")
    .master("local[2]")
    .config("spark.driver.extraJavaOptions", "-Dlog4j.configuration=log4j2.properties")
    .getOrCreate()

  def main(args: Array[String]): Unit = {
    val aPath = new Path("/", new Path("tmp", new Random().nextString(10))).toString
    println(s"aPath=$aPath")
    val a = unit("a") {
      val df = spark.createDataFrame(
        spark.sparkContext.parallelize(Seq(Row(1, 1L, "a"))),
        StructType.fromDDL("f1 INT, f2 LONG, f3 STRING")
      )
      df.write.save(aPath)
    }

    val bPath = new Path("/", new Path("tmp", new Random().nextString(10))).toString
    println(s"bPath=$bPath")
    val b = unit("b") {
      val df = spark.createDataFrame(
        spark.sparkContext.parallelize(Seq(Row(2, 2L, "b"))),
        StructType.fromDDL("f1 INT, f2 LONG, f3 STRING")
      )
      df.write.save(bPath)
    }

    run(map2("c")(a, b)((_, _) => spark.read.load(aPath).unionByName(spark.read.load(bPath)))).show()

    run(map(a)(_ => spark.read.load(aPath))).show()

  }
}
