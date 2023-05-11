package pl.epsilondeltalimit.dep.design

import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.{DataFrame, Row}
import pl.epsilondeltalimit.SparkSessionProvider

import scala.collection.mutable

object DepDesign_v4 extends SparkSessionProvider {

  class Once[A](a: => A) extends (() => A) {
    private lazy val value = a

    override def apply(): A = value
  }

  object Once{
    implicit def value2Once[A](a: => A): Once[A] =
      new Once[A](a)
  }

  trait Register[A] {

    def add(uid: String, deps: Set[String], value: Once[A]): Register[A]

    def get(uid: String): A
  }

  class SimpleRegister[A] extends Register[A] {
    private val s: mutable.Map[String, (Set[String], Once[A])] = mutable.Map.empty

    override def add(uid: String, deps: Set[String], value: Once[A]): Register[A] = {
      s += (uid -> (deps, value))
      this
    }

    //   todo: simplistic implementation => should be replaced with a solution based on graph
    override def get(uid: String): A = {
      def loop(deps: Set[String], dep: String): Set[String] =
        s(dep)._1 match {
          case ds if ds.isEmpty => deps
          case ds => ds.flatMap(d => loop(deps ++ ds, d))
        }

      val deps = loop(Set.empty, uid)

      deps.toSeq.sorted.foreach(d => s(d)._2())

      s(uid)._2()
    }
  }

  type Dep[A] = Register[A] => Register[A]

  def unit[A](uid: String)(a: => A): Dep[A] =
    r => r.add(uid, Set.empty, a)

  def map2[A](uid: String)(aUid: String, bUid: String)(f: (A, A) => A): Dep[A] =
    r => {
      val a = r.get(aUid)
      val b = r.get(bUid)
      val c = f(a, b)
      r.add(uid, Set(aUid, bUid), c)
    }

  def main(args: Array[String]): Unit = {

    val c = map2[DataFrame]("c")("a", "b")((a, b) => a.unionByName(b))

    val b = unit("b") {
      spark.createDataFrame(
        spark.sparkContext.parallelize(Seq(Row(2, 2L, "b"))),
        StructType.fromDDL("f1 INT, f2 LONG, f3 STRING")
      )
    }
    val a = unit("a") {
      spark.createDataFrame(
        spark.sparkContext.parallelize(Seq(Row(1, 1L, "a"))),
        StructType.fromDDL("f1 INT, f2 LONG, f3 STRING")
      )
    }

    //    val r: Register[DataFrame] =

    Set(a,b,c).foldLeft(new SimpleRegister[DataFrame])( (r, e) =>   )

    c(b(a(r))).get("c").show()
  }
}
