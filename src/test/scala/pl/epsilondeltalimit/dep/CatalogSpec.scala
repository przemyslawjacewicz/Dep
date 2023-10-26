package pl.epsilondeltalimit.dep

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class CatalogSpec extends AnyFlatSpec with Matchers{

  behavior of "withTransformations"

  it should "add transformations" in {
    import Transformations.implicits._

    (new Catalog)
      .withTransformations(new Transformations.Transformation {
        override def apply(c: Catalog): Catalog =
          c.unit("u")("u")
      })
      .eval[String]("u") should ===("u")

    (new Catalog)
      .withTransformations[Transformations.Transformation]((c: Catalog) => c.unit("u")("u"))
      .eval[String]("u") should ===("u")

  }

  it should "add transformations with implicit catalog" in {
    import Transformations.implicits._

    (new Catalog)
      .withTransformations(new Transformations.TransformationWithImplicitCatalog {
        override def apply(implicit c: Catalog): Catalog =
          c.unit("u")("u")
      })
      .eval[String]("u") should ===("u")

    (new Catalog)
      .withTransformations[Transformations.TransformationWithImplicitCatalog]((c: Catalog) => c.unit("u")("u"))
      .eval[String]("u") should ===("u")

  }
}
