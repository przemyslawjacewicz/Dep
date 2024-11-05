package pl.epsilondeltalimit.dep.catalog

import pl.epsilondeltalimit.dep.dep.Result
import pl.epsilondeltalimit.dep.transformation.Wrapper

trait Catalog {

  def withTransformations[T](ts: T*)(implicit wrapper: Seq[T] => Wrapper[T]): Catalog

  def put[A](id: String)(value: => A): Catalog

  def put[A](r: Result[A]): Catalog

  def get[A](id: String): Result[A]

  def eval[A](id: String): A

  // todo: consider a type for result
  def stages(id: String): Seq[Set[String]]

  def explain(id: String): String
}
