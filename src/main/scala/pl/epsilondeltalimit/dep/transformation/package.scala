package pl.epsilondeltalimit.dep

package object transformation {

  sealed trait Wrapper[T <: Transformation]

  case class CatalogTransformations(xs: Seq[CatalogTransformation]) extends Wrapper[CatalogTransformation]

  case class CatalogTransformationsImplicit(xs: Seq[CatalogTransformationImplicit])
      extends Wrapper[CatalogTransformationImplicit]

  case class DepTransformations(xs: Seq[DepTransformation[_]]) extends Wrapper[DepTransformation[_]]

  case class DepTransformationsImplicit(xs: Seq[DepTransformationImplicit[_]])
      extends Wrapper[DepTransformationImplicit[_]]

  object implicits {

    implicit val wrapCatalogTransformations: Seq[CatalogTransformation] => Wrapper[CatalogTransformation] =
      CatalogTransformations

    implicit val wrapCatalogTransformationsImplicit: Seq[CatalogTransformationImplicit] => Wrapper[CatalogTransformationImplicit] =
      CatalogTransformationsImplicit

    implicit val wrapDepTransformations: Seq[DepTransformation[_]] => Wrapper[DepTransformation[_]] =
      DepTransformations

    implicit val wrapDepTransformationsImplicit: Seq[DepTransformationImplicit[_]] => Wrapper[DepTransformationImplicit[_]] =
      DepTransformationsImplicit
  }

}
