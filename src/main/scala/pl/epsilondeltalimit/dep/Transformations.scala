package pl.epsilondeltalimit.dep

object Transformations {

  trait Transformation extends (Catalog => Catalog)

  trait TransformationWithImplicitCatalog {
    def apply(implicit c: Catalog): Catalog
  }

  trait PutTransformation extends (Catalog => Dep[_])

  trait PutTransformationWithImplicitCatalog {
    def apply(implicit c: Catalog): Dep[_]
  }

  sealed trait Wrapped[T]

  case class Transformations(xs: Seq[Transformation]) extends Wrapped[Transformation]

  case class TransformationsWithImplicitCatalog(xs: Seq[TransformationWithImplicitCatalog])
      extends Wrapped[TransformationWithImplicitCatalog]

  case class PutTransformations(xs: Seq[PutTransformation]) extends Wrapped[PutTransformation]

  case class PutTransformationsWithImplicitCatalog(xs: Seq[PutTransformationWithImplicitCatalog])
      extends Wrapped[PutTransformationWithImplicitCatalog]

  object implicits {

    implicit val wrapTransformations: Seq[Transformation] => Wrapped[Transformation] =
      Transformations

    implicit val wrapTransformationsWithImplicitCatalog: Seq[TransformationWithImplicitCatalog] => Wrapped[TransformationWithImplicitCatalog] =
      TransformationsWithImplicitCatalog

    implicit val wrapPutTransformations: Seq[PutTransformation] => Wrapped[PutTransformation] =
      PutTransformations

    implicit val wrapPutTransformationsWithImplicitCatalog: Seq[PutTransformationWithImplicitCatalog] => Wrapped[PutTransformationWithImplicitCatalog] =
      PutTransformationsWithImplicitCatalog
  }
}
