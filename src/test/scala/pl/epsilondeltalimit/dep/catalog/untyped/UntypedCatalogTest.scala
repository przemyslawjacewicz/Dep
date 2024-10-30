package pl.epsilondeltalimit.dep.catalog.untyped

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import pl.epsilondeltalimit.dep.catalog.Catalog
import pl.epsilondeltalimit.dep.dep.Dep
import pl.epsilondeltalimit.dep.transformation._
import pl.epsilondeltalimit.dep.transformation.implicits._

class UntypedCatalogTest extends AnyWordSpec with Matchers {

  "catalog" when {

    "catalog transformations are added" should {

      "evaluate the result for resources without dependencies" in {
        (new UntypedCatalog)
          .withTransformations(new CatalogTransformation {
            override def apply(c: Catalog): Catalog =
              c.put("u")("u")
          })
          .eval[String]("u") should ===("u")

        (new UntypedCatalog)
          .withTransformations[CatalogTransformation]((c: Catalog) => c.put("u")("u"))
          .eval[String]("u") should ===("u")
      }

      "evaluate the result for resources with dependencies" in {
        pending
      }

    }

    "catalog transformations with implicit catalog are added" should {
      "evaluate the result for resources without dependencies" in {
        (new UntypedCatalog)
          .withTransformations(new CatalogTransformationImplicit {
            override def apply(implicit c: Catalog): Catalog =
              c.put("u")("u")
          })
          .eval[String]("u") should ===("u")

        (new UntypedCatalog)
          .withTransformations[CatalogTransformationImplicit]((c: Catalog) => c.put("u")("u"))
          .eval[String]("u") should ===("u")

      }

      "evaluate the result for resources with dependencies" in {
        pending
      }

    }

    "dep transformations are added" should {
      "evaluate the result for resources without dependencies" in {
        (new UntypedCatalog)
          .withTransformations[DepTransformation[_]](new DepTransformation[String] {
            override def apply(c: Catalog): Dep[String] =
              Dep("u")("u")
          })
          .eval[String]("u") should ===("u")

        (new UntypedCatalog)
          .withTransformations[DepTransformation[_]]((_: Catalog) => Dep("u")("u"))
          .eval[String]("u") should ===("u")
      }

      "evaluate the result for resources with dependencies" in {
        pending
      }

    }

    "dep transformations with implicit catalog are added" should {

      "evaluate the result for resources without dependencies" in {
        (new UntypedCatalog)
          .withTransformations[DepTransformationImplicit[_]](new DepTransformationImplicit[String] {
            override def apply(implicit c: Catalog): Dep[String] =
              Dep("u")("u")
          })
          .eval[String]("u") should ===("u")

        (new UntypedCatalog)
          .withTransformations[DepTransformationImplicit[_]]((_: Catalog) => Dep("u")("u"))
          .eval[String]("u") should ===("u")
      }

      "evaluate the result for resources with dependencies" in {
        pending
      }

    }
  }

}
