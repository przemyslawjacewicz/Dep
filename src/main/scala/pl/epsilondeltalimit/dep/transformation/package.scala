package pl.epsilondeltalimit.dep

import pl.epsilondeltalimit.dep.catalog.Catalog
import pl.epsilondeltalimit.dep.dep.Result

package object transformation {

  sealed trait Wrapper[T]

  case class CatalogTransformations(xs: Seq[Catalog => Catalog]) extends Wrapper[Catalog => Catalog]

  case class CatalogTransformationsImplicit(xs: Seq[CatalogTransformationImplicit])
      extends Wrapper[CatalogTransformationImplicit]

  case class DepTransformations(xs: Seq[Catalog => Result[_]]) extends Wrapper[Catalog => Result[_]]

  case class DepTransformationsImplicit(xs: Seq[DepTransformationImplicit[_]])
      extends Wrapper[DepTransformationImplicit[_]]

  object implicits {

    implicit val wrapCatalogTransformations: Seq[Catalog => Catalog] => Wrapper[Catalog => Catalog] =
      CatalogTransformations

    implicit val wrapCatalogTransformationsImplicit: Seq[CatalogTransformationImplicit] => Wrapper[CatalogTransformationImplicit] =
      CatalogTransformationsImplicit

    implicit val wrapDepTransformations: Seq[Catalog => Result[_]] => Wrapper[Catalog => Result[_]] =
      DepTransformations

    implicit val wrapDepTransformationsImplicit: Seq[DepTransformationImplicit[_]] => Wrapper[DepTransformationImplicit[_]] =
      DepTransformationsImplicit
  }

}
