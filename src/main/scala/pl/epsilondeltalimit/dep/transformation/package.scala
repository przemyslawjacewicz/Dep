package pl.epsilondeltalimit.dep

import pl.epsilondeltalimit.dep.catalog.Catalog
import pl.epsilondeltalimit.dep.dep.Result

package object transformation {

  sealed trait Wrapper[T]

  case class CatalogTransformations(xs: Seq[Catalog => Catalog]) extends Wrapper[Catalog => Catalog]

  case class ResultTransformations(xs: Seq[Catalog => Result[_]]) extends Wrapper[Catalog => Result[_]]

  object implicits {

    implicit val wrapCatalogTransformations: Seq[Catalog => Catalog] => Wrapper[Catalog => Catalog] =
      CatalogTransformations

    implicit val wrapResultTransformations: Seq[Catalog => Result[_]] => Wrapper[Catalog => Result[_]] =
      ResultTransformations

    implicit class StringImplicits(id: String) {
      def as[T](implicit c: Catalog): Result[T] =
        c.get[T](id)
    }

  }

}
