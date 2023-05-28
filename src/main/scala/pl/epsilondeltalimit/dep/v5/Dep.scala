package pl.epsilondeltalimit.dep.v5

import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.{DataFrame, Row}
import pl.epsilondeltalimit.SparkSessionProvider

import scala.collection.mutable

object Dep extends SparkSessionProvider {

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
}
