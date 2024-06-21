package pl.epsilondeltalimit.dep

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import pl.epsilondeltalimit.dep.catalog.Catalog
import pl.epsilondeltalimit.dep.dep.{Dep, LeafDep}
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
          LeafDep[String]("u", () => Set.empty, () => "u")
      })
      .eval[String]("u") should ===("u")

    (new Catalog)
      .withTransformations[PutTransformation]((_: Catalog) => LeafDep[String]("u", () => Set.empty, () => "u"))
      .eval[String]("u") should ===("u")
  }

  it should "add put transformations with implicit catalog" in {
    (new Catalog)
      .withTransformations(new PutTransformationWithImplicitCatalog {
        override def apply(implicit c: Catalog): Dep[_] =
          LeafDep[String]("u", () => Set.empty, () => "u")
      })
      .eval[String]("u") should ===("u")

    (new Catalog)
      .withTransformations[PutTransformationWithImplicitCatalog]((_: Catalog) =>
        LeafDep[String]("u", () => Set.empty, () => "u"))
      .eval[String]("u") should ===("u")
  }

}
