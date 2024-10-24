package pl.epsilondeltalimit.dep.catalog

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import pl.epsilondeltalimit.dep.dep.Dep
import pl.epsilondeltalimit.dep.transformation._
import pl.epsilondeltalimit.dep.transformation.implicits._

class CatalogSpec extends AnyFlatSpec with Matchers {

  behavior of "withTransformations"

  it should "add transformations" in {
    (new Catalog)
      .withTransformations(new Transformation {
        override def apply(c: Catalog): Catalog =
          c.put("u")("u")
      })
      .eval[String]("u") should ===("u")

    (new Catalog)
      .withTransformations[Transformation]((c: Catalog) => c.put("u")("u"))
      .eval[String]("u") should ===("u")
  }

  it should "add transformations with implicit catalog" in {
    (new Catalog)
      .withTransformations(new TransformationWithImplicitCatalog {
        override def apply(implicit c: Catalog): Catalog =
          c.put("u")("u")
      })
      .eval[String]("u") should ===("u")

    (new Catalog)
      .withTransformations[TransformationWithImplicitCatalog]((c: Catalog) => c.put("u")("u"))
      .eval[String]("u") should ===("u")
  }

  it should "add put transformations" in {
    (new Catalog)
      .withTransformations(new PutTransformation {
        override def apply(c: Catalog): Dep[_] =
          Dep.dep[String]("u")("u")
      })
      .eval[String]("u") should ===("u")

    (new Catalog)
      .withTransformations[PutTransformation]((_: Catalog) => Dep.dep[String]("u")("u"))
      .eval[String]("u") should ===("u")
  }

  it should "add put transformations with implicit catalog" in {
    (new Catalog)
      .withTransformations(new PutTransformationWithImplicitCatalog {
        override def apply(implicit c: Catalog): Dep[_] =
          Dep.dep[String]("u")("u")
      })
      .eval[String]("u") should ===("u")

    (new Catalog)
      .withTransformations[PutTransformationWithImplicitCatalog]((_: Catalog) => Dep.dep[String]("u")("u"))
      .eval[String]("u") should ===("u")
  }

}
