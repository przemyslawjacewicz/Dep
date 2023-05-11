package pl.epsilondeltalimit.dep.design

import org.apache.hadoop.fs.Path
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.{Row, SparkSession}

import scala.util.Random

object DepDesign_v3 {

  class Dep[A](val id: String, val deps: Set[Dep[_]], a: => A) extends (() => A) {
    private lazy val value = a

    override def apply(): A = value
  }

//   todo: simplistic implementation => should be replaced with a solution based on graph
  def run[A](dep: Dep[A]): A = {
    def loop(deps: Set[Dep[_]], dep: Dep[_]): Set[Dep[_]] =
      dep.deps match {
        case s if s.isEmpty => deps
        case s              => s.flatMap(d => loop(deps ++ s, d))
      }
    val deps = loop(Set.empty, dep)

    deps.toSeq.sortBy(_.id).foreach(d => d())

    dep()
  }

  // todo: better name for uid
  def unit[A](uid: String)(a: => A): Dep[A] = new Dep[A](uid, Set.empty, a)

  def map2[A, B, C](uid: String)(aDep: Dep[A], bDep: Dep[B])(f: (A, B) => C): Dep[C] =
    new Dep[C](uid, Set(aDep, bDep), f(aDep(), bDep()))

  def map[A, B](aDep: Dep[A])(f: A => B): Dep[B] =
    new Dep[B](aDep.id, aDep.deps, f(aDep()))

  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder
      .appName("Runner")
      .master("local[2]")
      .getOrCreate()

    /* 1 */
//    val aPath = new Path("/", new Path("tmp", new Random().nextString(10))).toString
//    println(s"aPath=$aPath")
//    val a = unit("a") {
//      val df = spark.createDataFrame(
//        spark.sparkContext.parallelize(Seq(Row(1, 1L, "a"))),
//        StructType.fromDDL("f1 INT, f2 LONG, f3 STRING")
//      )
//      df.write.save(aPath)
//    }
//
//    val bPath = new Path("/", new Path("tmp", new Random().nextString(10))).toString
//    println(s"bPath=$bPath")
//    val b = unit("b") {
//      val df = spark.createDataFrame(
//        spark.sparkContext.parallelize(Seq(Row(2, 2L, "b"))),
//        StructType.fromDDL("f1 INT, f2 LONG, f3 STRING")
//      )
//      df.write.save(bPath)
//    }
//
//    run(map2("c")(a, b)((_, _) => spark.read.load(aPath).unionByName(spark.read.load(bPath)))).show()
//
//    run(map(a)(_ => spark.read.load(aPath))).show()

    /* 2 */
    val a = unit("a") {
      println("evaluating a")
      spark.createDataFrame(
        spark.sparkContext.parallelize(Seq(Row(1, 1L, "a"))),
        StructType.fromDDL("f1 INT, f2 LONG, f3 STRING")
      )
    }
    val b = unit("b") {
      println("evaluating b")
      spark.createDataFrame(
        spark.sparkContext.parallelize(Seq(Row(2, 2L, "b"))),
        StructType.fromDDL("f1 INT, f2 LONG, f3 STRING")
      )
    }

    run(map2("c")(a, b)((aDF, bDF) => aDF.unionByName(bDF))).show()

  }
}
