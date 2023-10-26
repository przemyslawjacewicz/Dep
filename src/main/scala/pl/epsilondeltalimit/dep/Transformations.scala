package pl.epsilondeltalimit.dep

//todo: consider adding more transformations with implicit catalog
object Transformations {
  trait Transformation extends (Catalog => Catalog)

  trait TransformationWithImplicitCatalog {
    def apply(implicit c: Catalog): Catalog
  }

  trait PutTransformation extends (Catalog => Dep[_])

  trait PutTransformationWithImplicitCatalog {
    def apply(implicit c: Catalog): Dep[_]
  }

  trait MultiPutTransformation extends (Catalog => Seq[Dep[_]])

  trait MultiPutTransformationWithImplicitCatalog {
    def apply(implicit c: Catalog): Seq[Dep[_]]
  }

  sealed trait Wrapped[T]

  case class Transformations(xs: Seq[Transformation]) extends Wrapped[Transformation]

  case class TransformationsWithImplicitCatalog(xs: Seq[TransformationWithImplicitCatalog]) extends Wrapped[TransformationWithImplicitCatalog]

  case class PutTransformations(xs: Seq[PutTransformation]) extends Wrapped[PutTransformation]

  case class PutTransformationsWithImplicitCatalog(xs: Seq[PutTransformationWithImplicitCatalog]) extends Wrapped[PutTransformationWithImplicitCatalog]

  case class MultiPutTransformations(xs: Seq[MultiPutTransformation]) extends Wrapped[MultiPutTransformation]

  case class MultiPutTransformationsWithImplicitCatalog(xs: Seq[MultiPutTransformationWithImplicitCatalog]) extends Wrapped[MultiPutTransformationWithImplicitCatalog]

  object implicits {

    implicit val wrapTransformations: Seq[Transformation] => Wrapped[Transformation] =
      Transformations

    implicit val wrapTransformationsWithImplicitCatalog: Seq[TransformationWithImplicitCatalog] => Wrapped[TransformationWithImplicitCatalog] =
      TransformationsWithImplicitCatalog

    implicit val wrapPutTransformations: Seq[PutTransformation] => Wrapped[PutTransformation] =
      PutTransformations

    implicit val wrapPutTransformationsWithImplicitCatalog: Seq[PutTransformationWithImplicitCatalog] => Wrapped[PutTransformationWithImplicitCatalog] =
      PutTransformationsWithImplicitCatalog

    implicit val wrapMultiPutTransformations: Seq[MultiPutTransformation] => Wrapped[MultiPutTransformation] =
      MultiPutTransformations

    implicit val wrapMultiPutTransformationsWithImplicitCatalog: Seq[MultiPutTransformationWithImplicitCatalog] => Wrapped[MultiPutTransformationWithImplicitCatalog] =
      MultiPutTransformationsWithImplicitCatalog
  }
}
