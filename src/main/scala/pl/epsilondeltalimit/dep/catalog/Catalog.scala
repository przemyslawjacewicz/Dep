package pl.epsilondeltalimit.dep.catalog

import pl.epsilondeltalimit.dep.Result
import pl.epsilondeltalimit.dep.transformation.Wrapper

trait Catalog {

  def withTransformations[T](ts: T*)(implicit wrapper: Seq[T] => Wrapper[T]): Catalog

  def put[A](id: String)(value: => A): Catalog

  def ref[R, A](id: String)(r: => R)(a: R => A): Catalog =
    put[A](id)(a(r))

  def put[A](r: Result[A]): Catalog

  def get[A](id: String): Result[A]

  def ids: Set[String]

  def eval[A](id: String): A

  // todo: validate this method
  // eval all
  // this method can be strange to apply, it will probably only work when catalog holds elements with the same type
  def eval[A](): Unit =
    ids.foreach(eval[A])

  def run[A](id: String)(s: A => Unit): Unit =
    s(eval[A](id))

  // todo: validate this method
  // run all
  // this method can be strange to apply, possibly exploding with cases for each id
  def run(s: String => AnyRef => Unit): Unit =
    ids.foreach(id => run(id)(s(id)))

  // todo: consider a type for result
  def stages(id: String): Seq[Set[String]]

  def explain(id: String): String

}
