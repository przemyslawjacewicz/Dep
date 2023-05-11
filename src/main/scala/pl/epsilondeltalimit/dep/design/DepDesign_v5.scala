package pl.epsilondeltalimit.dep.design

import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.{DataFrame, Row}
import pl.epsilondeltalimit.SparkSessionProvider

import scala.collection.mutable

object DepDesign_v5 extends SparkSessionProvider {

  trait Register[A] {
    def unit(uid: String)(a: => A): Register[A]

    def map2(uid: String)(a: String, b: String)(f: (A, A) => A): Register[A]

    def get(uid: String): A
  }

  class Once[A](a: => A) extends (() => A) {
    private lazy val value = a

    override def apply(): A = value
  }

  class SimpleRegister[A] extends Register[A] {
    private val s: mutable.Map[String, (Set[String], Once[A])] = mutable.Map.empty

    override def unit(uid: String)(a: => A): Register[A] = {
      s += (uid -> (Set.empty, new Once(a)))
      this
    }

    override def map2(uid: String)(a: String, b: String)(f: (A, A) => A): Register[A] = {
      s += (uid -> (Set(a, b), new Once(f(s(a)._2(), s(b)._2()))))
      this
    }

    //   todo: simplistic implementation => should be replaced with a solution based on graph
    override def get(uid: String): A = {
      def loop(deps: Set[String], dep: String): Set[String] =
        s(dep)._1 match {
          case ds if ds.isEmpty => deps
          case ds               => ds.flatMap(d => loop(deps ++ ds, d))
        }

      val deps = loop(Set.empty, uid)

      deps.toSeq.sorted.foreach(d => s(d)._2())

      s(uid)._2()
    }
  }

  def main(args: Array[String]): Unit = {
    val r = new SimpleRegister[DataFrame]

    val cr = r.map2("c")("a", "b") { (a: DataFrame, b: DataFrame) =>
      println("evaluating c")
      a.unionByName(b)
    }
    val bcr = cr.unit("b") {
      println("evaluating b")
      spark.createDataFrame(
        spark.sparkContext.parallelize(Seq(Row(2, 2L, "b"))),
        StructType.fromDDL("f1 INT, f2 LONG, f3 STRING")
      )
    }
    val abcr = bcr.unit("a") {
      println("evaluating a")
      spark.createDataFrame(
        spark.sparkContext.parallelize(Seq(Row(1, 1L, "a"))),
        StructType.fromDDL("f1 INT, f2 LONG, f3 STRING")
      )
    }

    abcr.get("c").show()
  }
}
