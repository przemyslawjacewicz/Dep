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

    go(Set(byId(id)), Seq(Set(id))).reverse
  }

  def unit[A](id: String)(value: => A): Catalog = {
    s += new Dep[A](id, () => Set.empty[String])(() => value)
    this
  }

  def get[A](id: String): Dep[A] =
    byId
      .getOrElse(id, new Dep[A](id, () => byId(id).needs())(() => byId(id).asInstanceOf[Dep[A]]()))
      .asInstanceOf[Dep[A]]

  // todo: consider removing this
  def getAll: Set[Dep[_]] =
    s.toSet

  def put[A](dep: Dep[A]): Catalog = {
    //    require(!dep.id.endsWith("_M") && !dep.id.endsWith("_F"), "cannot put intermediate Dep")
    s += dep
    this
  }

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
      case Transformations.MultiPutTransformations(xs) =>
        xs.foldLeft(this)((c, mpt) => mpt(c).foldLeft(c)(_.put(_)))
      case Transformations.MultiPutTransformationsWithImplicitCatalog(xs) =>
        xs.foldLeft(this)((c, mpt) => mpt(c).foldLeft(c)(_.put(_)))
    }

  def eval[A](id: String): A = {
    stages(id).foreach(_.par.foreach(byId(_)()))
    get[A](id)()
  }

  // todo: evalAll ?

  // todo: consider a lib create a graph
  def explain(id: String): Seq[Set[String]] =
    stages(id)

  def show(id: String): Unit =
    println(explain(id).reverse.zipWithIndex.reverse.map { case (s, i) => s"$i: ${s.mkString(",")}" }.mkString("\n"))

}
