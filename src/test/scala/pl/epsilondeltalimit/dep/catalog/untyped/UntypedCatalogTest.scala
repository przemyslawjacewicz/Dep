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
              new CatalogTransformation {
                override def apply(c: Catalog): Catalog =
                  c.put("u")("u1")
              },
              new CatalogTransformation {
                override def apply(c: Catalog): Catalog =
                  c.put("u")("u2")
              }
            )
            .eval[String]("u")
        }
      }

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
        val w = new CatalogTransformation {
          override def apply(c: Catalog): Catalog =
            c.put("w")(c.get[String]("u")() + "w")
        }
        val u = new CatalogTransformation {
          override def apply(c: Catalog): Catalog =
            c.put("u")("u")
        }

        val c = (new UntypedCatalog)
          .withTransformations[CatalogTransformation](w, u)

        c.eval[String]("w") should ===("uw")
        c.eval[String]("u") should ===("u")
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
        val w = new CatalogTransformationImplicit {
          override def apply(implicit c: Catalog): Catalog =
            c.put("w")(c.get[String]("u")() + "w")
        }
        val u = new CatalogTransformationImplicit {
          override def apply(implicit c: Catalog): Catalog =
            c.put("u")("u")
        }

        val c = (new UntypedCatalog)
          .withTransformations[CatalogTransformationImplicit](w, u)

        c.eval[String]("w") should ===("uw")
        c.eval[String]("u") should ===("u")
      }

    }

    "dep transformations are added" should {
      "evaluate the result for resources without dependencies" in {
        (new UntypedCatalog)
          .withTransformations[DepTransformation[_]](new DepTransformation[String] {
            override def apply(c: Catalog): Result[String] =
              Dep("u")("u")
          })
          .eval[String]("u") should ===("u")

        (new UntypedCatalog)
          .withTransformations[DepTransformation[_]]((_: Catalog) => Dep("u")("u"))
          .eval[String]("u") should ===("u")
      }

      "evaluate the result for resources with dependencies" in {
        val w = new DepTransformation[String] {
          override def apply(c: Catalog): Result[String] =
            Dep("w")(c.get[String]("u")() + "w")
        }
        val u = new DepTransformation[String] {
          override def apply(c: Catalog): Result[String] =
            Dep("u")("u")
        }

        val c = (new UntypedCatalog)
          .withTransformations[DepTransformation[_]](w, u)

        c.eval[String]("w") should ===("uw")
        c.eval[String]("u") should ===("u")
      }

    }

    "dep transformations with implicit catalog are added" should {

      "evaluate the result for resources without dependencies" in {
        (new UntypedCatalog)
          .withTransformations[DepTransformationImplicit[_]](new DepTransformationImplicit[String] {
            override def apply(implicit c: Catalog): Result[String] =
              Dep("u")("u")
          })
          .eval[String]("u") should ===("u")

        (new UntypedCatalog)
          .withTransformations[DepTransformationImplicit[_]]((_: Catalog) => Dep("u")("u"))
          .eval[String]("u") should ===("u")
      }

      "evaluate the result for resources with dependencies" in {
        val w = new DepTransformationImplicit[String] {
          override def apply(implicit c: Catalog): Result[String] =
            Dep("w")(c.get[String]("u")() + "w")
        }
        val u = new DepTransformationImplicit[String] {
          override def apply(implicit c: Catalog): Result[String] =
            Dep("u")("u")
        }

        val c = (new UntypedCatalog)
          .withTransformations[DepTransformationImplicit[_]](w, u)

        c.eval[String]("w") should ===("uw")
        c.eval[String]("u") should ===("u")
      }

    }
  }

}
