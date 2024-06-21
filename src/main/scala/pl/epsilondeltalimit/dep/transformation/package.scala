package pl.epsilondeltalimit.dep

package object transformation {

  sealed trait Wrapper[T]

  case class Transformations(xs: Seq[Transformation]) extends Wrapper[Transformation]

  case class TransformationsWithImplicitCatalog(xs: Seq[TransformationWithImplicitCatalog])
      extends Wrapper[TransformationWithImplicitCatalog]

  case class PutTransformations(xs: Seq[PutTransformation]) extends Wrapper[PutTransformation]

  case class PutTransformationsWithImplicitCatalog(xs: Seq[PutTransformationWithImplicitCatalog])
      extends Wrapper[PutTransformationWithImplicitCatalog]

  object implicits {

    implicit val wrapTransformations: Seq[Transformation] => Wrapper[Transformation] =
      Transformations

    implicit val wrapTransformationsWithImplicitCatalog: Seq[TransformationWithImplicitCatalog] => Wrapper[TransformationWithImplicitCatalog] =
      TransformationsWithImplicitCatalog

    implicit val wrapPutTransformations: Seq[PutTransformation] => Wrapper[PutTransformation] =
      PutTransformations

    implicit val wrapPutTransformationsWithImplicitCatalog: Seq[PutTransformationWithImplicitCatalog] => Wrapper[PutTransformationWithImplicitCatalog] =
      PutTransformationsWithImplicitCatalog
  }

}
