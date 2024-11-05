package pl.epsilondeltalimit.dep.transformation

import pl.epsilondeltalimit.dep.catalog.Catalog
import pl.epsilondeltalimit.dep.dep.Result

trait ResultTransformationWithImplicitCatalog[A] {

  // todo: move me ?
  implicit class StringImplicits(id: String) {
    def as[T](implicit c: Catalog): Result[T] =
      c.get[T](id)
  }

  def apply(implicit c: Catalog): Result[A]
}
