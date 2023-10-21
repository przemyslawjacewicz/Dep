package pl.epsilondeltalimit.dep

object Transformations {
  trait Transformation         extends (Catalog => Catalog)
  trait PutTransformation      extends (Catalog => Dep[_])
  trait MultiPutTransformation extends (Catalog => Seq[Dep[_]])
}
