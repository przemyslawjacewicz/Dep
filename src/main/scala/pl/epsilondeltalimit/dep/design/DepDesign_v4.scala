package pl.epsilondeltalimit.dep.design

import org.apache.spark.sql.{DataFrame, Row, SparkSession}
import org.apache.spark.sql.types.StructType


object DepDesign_v4 {

  trait Register {
    val

    // return new register ?
    def add(uid: String, deps: Set[String]): Unit

    def get[A](uid: String): Dep[A] =
      r => {

      }
  }

  type Dep[A] = Register => A

  def run[A](r: Register)(dep: Dep[A]): A =
    dep(r)

  def unit[A](uid: String)(a: => A): Dep[A] =
    r => {
      r.add(uid, Set.empty)
      a
    }

  def map2[A, B, C](uid: String)(aDep: => Dep[A], bDep: => Dep[B])(f: (A, B) => C): Dep[C] =
    r => {
      val a = aDep(r)
      val b = bDep(r)
      f(a, b)
    }

  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder
      .appName("Runner")
      .master("local[2]")
      .getOrCreate()

    val r: Register = ???

    val c = map2("c")(r.get[DataFrame]("a"), r.get[DataFrame]("b"))((a, b) => a.unionByName(b))

    val a = unit("a") {
      spark.createDataFrame(
        spark.sparkContext.parallelize(Seq(Row(1, 1L, "a"))),
        StructType.fromDDL("f1 INT, f2 LONG, f3 STRING")
      )
    }
    val b = unit("b") {
      spark.createDataFrame(
        spark.sparkContext.parallelize(Seq(Row(2, 2L, "b"))),
        StructType.fromDDL("f1 INT, f2 LONG, f3 STRING")
      )
    }

    run(r)(c).show()

  }
}
