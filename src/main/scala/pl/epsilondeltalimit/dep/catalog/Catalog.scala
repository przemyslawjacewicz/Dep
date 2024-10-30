package pl.epsilondeltalimit.dep.catalog

import pl.epsilondeltalimit.dep.dep.Dep
import pl.epsilondeltalimit.dep.transformation.{Transformation, Wrapper}

trait Catalog {

  def withTransformations[T <: Transformation](ts: T*)(implicit wrapper: Seq[T] => Wrapper[T]): Catalog

  def put[A](id: String)(value: => A): Catalog

  // todo: consider making this accept LeafDep only
  def put[A](dep: Dep[A]): Catalog

  // todo: consider making this return LeafDep only
  def get[A](id: String): Dep[A]

  def eval[A](id: String): A

  // todo: consider a type for result
  def explain(id: String): Seq[Set[String]]

  def show(id: String): Unit

}
