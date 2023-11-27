package pl.epsilondeltalimit.dep

import pl.epsilondeltalimit.dep.Transformations._

import scala.annotation.tailrec
import scala.collection.mutable

class Catalog {
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

  def put[A](id: String)(value: => A): Catalog = {
    s += new LeafDep[A](id, () => Set.empty, () => value)
    this
  }

  def put[A](dep: Dep[A]): Catalog = {
    s += dep
    this
  }

  def get[A](id: String): Dep[A] =
    byId
      .getOrElse(id, new LeafDep[A](id, () => byId(id).needs(), () => byId(id).asInstanceOf[Dep[A]]()))
      .asInstanceOf[Dep[A]]

  // todo: consider removing this
  def getAll: Set[Dep[_]] =
    s.toSet

  def withTransformations[T](ts: T*)(implicit wrapper: Seq[T] => Wrapped[T]): Catalog =
    wrapper(ts) match {
      case Transformations.Transformations(xs) =>
        xs.foldLeft(this)((c, t) => t(c))
      case Transformations.TransformationsWithImplicitCatalog(xs) =>
        xs.foldLeft(this)((c, t) => t(c))
      case Transformations.PutTransformations(xs) =>
        xs.foldLeft(this)((c, pt) => c.put(pt(c)))
      case Transformations.PutTransformationsWithImplicitCatalog(xs) =>
        xs.foldLeft(this)((c, pt) => c.put(pt(c)))
    }

  def eval[A](id: String): A = {
    stages(id).foreach(_.par.foreach(byId(_)()))
    get[A](id)()
  }

  def explain(id: String): Seq[Set[String]] =
    stages(id)

  def show(id: String): Unit =
    println(explain(id).reverse.zipWithIndex.reverse.map { case (s, i) => s"$i: ${s.mkString(",")}" }.mkString("\n"))

}
