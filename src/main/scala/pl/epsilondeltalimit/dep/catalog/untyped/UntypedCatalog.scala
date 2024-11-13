package pl.epsilondeltalimit.dep.catalog.untyped

import pl.epsilondeltalimit.dep.catalog.Catalog
import pl.epsilondeltalimit.dep.dep.{Dep, Result}
import pl.epsilondeltalimit.dep.transformation._

import scala.annotation.tailrec
import scala.collection.mutable

class UntypedCatalog extends Catalog {
  // todo: consider a special equality when holding results
  private val s: mutable.Set[Result[_]] = mutable.Set.empty

  private def byId: Map[String, Result[_]] = {
    val ids = s.groupBy(_.id).collect { case (id, rs) if rs.size > 1 => id }

    if (ids.nonEmpty) {
      throw new RuntimeException(s"multiple results with the same id found: ids=${ids.mkString(",")}")
    }

    s.map(r => r.id -> r).toMap
  }

  override def withTransformations[T](ts: T*)(implicit wrapper: Seq[T] => Wrapper[T]): Catalog =
    wrapper(ts) match {
      case CatalogTransformations(xs) => xs.foldLeft(this: Catalog)((c, t) => t(c))
      case ResultTransformations(xs)  => xs.foldLeft(this: Catalog)((c, pt) => c.put(pt(c)))
    }

  override def put[A](id: String)(value: => A): Catalog = {
    s += Dep(id)(value)
    this
  }

  override def put[A](r: Result[A]): Catalog = {
    s += r
    this
  }

  override def get[A](id: String): Result[A] =
    byId
      .getOrElse(id, Dep(id, byId(id).needs())(byId(id)()))
      .asInstanceOf[Result[A]]

  override def eval[A](id: String): A = {
    stages(id).foreach(_.foreach(byId(_)()))
    get[A](id)()
  }

  override def run(): Unit =
    s.map(_.id).foreach(id => stages(id).foreach(_.foreach(byId(_)())))

  override def stages(id: String): Seq[Set[String]] = {
    @tailrec
    def go(results: Set[Result[_]], acc: Seq[Set[String]]): Seq[Set[String]] = {
      val needs = results.flatMap(_.needs())
      if (needs.isEmpty) acc else go(needs.map(byId), needs +: acc)
    }

    val raw = go(Set(byId(id)), Seq(Set(id))).reverse

    def norm(s: Seq[Set[String]]): Set[String] =
      s match {
        case next :: prev :: Nil => next.diff(prev)
        case single :: Nil       => single
      }

    raw match {
      case _ :: Nil | _ :: _ :: Nil => raw
      case _                        => Seq(raw.head) ++ raw.tail.sliding(2).map(norm) ++ Set(raw.last)
    }
  }

  override def explain(id: String): String =
    stages(id).reverse.zipWithIndex.reverse.map { case (s, i) => s"$i: ${s.mkString(",")}" }.mkString("\n")

}
