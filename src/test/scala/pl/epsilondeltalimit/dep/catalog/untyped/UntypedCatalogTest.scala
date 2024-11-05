package pl.epsilondeltalimit.dep.catalog.untyped

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import pl.epsilondeltalimit.dep.catalog.Catalog
import pl.epsilondeltalimit.dep.dep.{Dep, Result}
import pl.epsilondeltalimit.dep.transformation._
import pl.epsilondeltalimit.dep.transformation.implicits._

class UntypedCatalogTest extends AnyWordSpec with Matchers {

  "catalog" when {

    "catalog transformations are added" should {

      "throw an exception upon evaluation when multiple results with the same id are added" in {
        a[RuntimeException] should be thrownBy {
          (new UntypedCatalog)
            .withTransformations(
              (c: Catalog) => c.put("u")("u1"),
              (c: Catalog) => c.put("u")("u2")
            )
            .eval[String]("u")
        }
      }

      "evaluate the result for resources without dependencies" in {
        (new UntypedCatalog)
          .withTransformations((c: Catalog) => c.put("u")("u"))
          .eval[String]("u") should ===("u")
      }

      "evaluate the result for resources with dependencies" in {
        val w = (c: Catalog) => c.put("w")(c.get[String]("u")() + "w")
        val u = (c: Catalog) => c.put("u")("u")

        val c = (new UntypedCatalog)
          .withTransformations(w, u)

        c.eval[String]("w") should ===("uw")
        c.eval[String]("u") should ===("u")
      }

    }

    "catalog transformations with implicit catalog are added" should {
      "evaluate the result for resources without dependencies" in {
        (new UntypedCatalog)
          .withTransformations(new CatalogTransformationWithImplicitCatalog {
            override def apply(implicit c: Catalog): Catalog =
              c.put("u")("u")
          })
          .eval[String]("u") should ===("u")

        (new UntypedCatalog)
          .withTransformations[CatalogTransformationWithImplicitCatalog]((c: Catalog) => c.put("u")("u"))
          .eval[String]("u") should ===("u")

      }

      "evaluate the result for resources with dependencies" in {
        val w = new CatalogTransformationWithImplicitCatalog {
          override def apply(implicit c: Catalog): Catalog =
            c.put("w")(c.get[String]("u")() + "w")
        }
        val u = new CatalogTransformationWithImplicitCatalog {
          override def apply(implicit c: Catalog): Catalog =
            c.put("u")("u")
        }

        val c = (new UntypedCatalog)
          .withTransformations[CatalogTransformationWithImplicitCatalog](w, u)

        c.eval[String]("w") should ===("uw")
        c.eval[String]("u") should ===("u")
      }

    }

    "dep transformations are added" should {
      "evaluate the result for resources without dependencies" in {
        (new UntypedCatalog)
          .withTransformations[Catalog => Result[_]]((_: Catalog) => Dep("u")("u"))
          .eval[String]("u") should ===("u")
      }

      "evaluate the result for resources with dependencies" in {
        val w = (c: Catalog) => Dep("w")(c.get[String]("u")() + "w")
        val u = (_: Catalog) => Dep("u")("u")

        val c = (new UntypedCatalog)
          .withTransformations[Catalog => Result[_]](w, u)

        c.eval[String]("w") should ===("uw")
        c.eval[String]("u") should ===("u")
      }
    }

    "dep transformations with implicit catalog are added" should {

      "evaluate the result for resources without dependencies" in {
        (new UntypedCatalog)
          .withTransformations[ResultTransformationWithImplicitCatalog[_]](new ResultTransformationWithImplicitCatalog[String] {
            override def apply(implicit c: Catalog): Result[String] =
              Dep("u")("u")
          })
          .eval[String]("u") should ===("u")

        (new UntypedCatalog)
          .withTransformations[ResultTransformationWithImplicitCatalog[_]]((_: Catalog) => Dep("u")("u"))
          .eval[String]("u") should ===("u")
      }

      "evaluate the result for resources with dependencies" in {
        val w = new ResultTransformationWithImplicitCatalog[String] {
          override def apply(implicit c: Catalog): Result[String] =
            Dep("w")(c.get[String]("u")() + "w")
        }
        val u = new ResultTransformationWithImplicitCatalog[String] {
          override def apply(implicit c: Catalog): Result[String] =
            Dep("u")("u")
        }

        val c = (new UntypedCatalog)
          .withTransformations[ResultTransformationWithImplicitCatalog[_]](w, u)

        c.eval[String]("w") should ===("uw")
        c.eval[String]("u") should ===("u")
      }

    }
  }

}
