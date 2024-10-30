package pl.epsilondeltalimit.dep.catalog.untyped

import pl.epsilondeltalimit.dep.catalog.Catalog
import pl.epsilondeltalimit.dep.dep.Dep
import pl.epsilondeltalimit.dep.transformation._

import scala.annotation.tailrec
import scala.collection.mutable

class UntypedCatalog extends Catalog {
  private val s: mutable.Set[Dep[_]] = mutable.Set.empty

  private def byId: Map[String, Dep[_]] =
    s.map(dep => dep.id -> dep).toMap

  private def stages(id: String): Seq[Set[String]] = {
    @tailrec
    def go(deps: Set[Dep[_]], acc: Seq[Set[String]]): Seq[Set[String]] = {
      val needs = deps.flatMap(_.needs())
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

  override def withTransformations[T <: Transformation](ts: T*)(implicit wrapper: Seq[T] => Wrapper[T]): Catalog =
    wrapper(ts) match {
      case CatalogTransformations(xs)         => xs.foldLeft(this: Catalog)((c, t) => t(c))
      case CatalogTransformationsImplicit(xs) => xs.foldLeft(this: Catalog)((c, t) => t(c))
      case DepTransformations(xs)             => xs.foldLeft(this: Catalog)((c, pt) => c.put(pt(c)))
      case DepTransformationsImplicit(xs)     => xs.foldLeft(this: Catalog)((c, pt) => c.put(pt(c)))
    }

  override def put[A](id: String)(value: => A): Catalog = {
    s += Dep(id)(value)
    this
  }

  override def put[A](dep: Dep[A]): Catalog = {
    s += dep
    this
  }

  override def get[A](id: String): Dep[A] =
    byId
      .getOrElse(id, Dep(id, byId(id).needs())(byId(id).asInstanceOf[Dep[A]]()))
      .asInstanceOf[Dep[A]]

  def eval[A](id: String): A = {
    stages(id).foreach(_.par.foreach(byId(_)()))
    get[A](id)()
  }

  def explain(id: String): Seq[Set[String]] =
    stages(id)

  def show(id: String): Unit =
    println(explain(id).reverse.zipWithIndex.reverse.map { case (s, i) => s"$i: ${s.mkString(",")}" }.mkString("\n"))

}
