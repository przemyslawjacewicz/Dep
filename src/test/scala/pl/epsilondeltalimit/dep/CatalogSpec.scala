package pl.epsilondeltalimit.dep

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import pl.epsilondeltalimit.dep.Transformations.implicits._

class CatalogSpec extends AnyFlatSpec with Matchers {

  behavior of "withTransformations"

  it should "add transformations" in {
    (new Catalog)
      .withTransformations(new Transformations.Transformation {
        override def apply(c: Catalog): Catalog =
          c.put("u")("u")
      })
      .eval[String]("u") should ===("u")

    (new Catalog)
      .withTransformations[Transformations.Transformation]((c: Catalog) => c.put("u")("u"))
      .eval[String]("u") should ===("u")
  }

  it should "add transformations with implicit catalog" in {
    (new Catalog)
      .withTransformations(new Transformations.TransformationWithImplicitCatalog {
        override def apply(implicit c: Catalog): Catalog =
          c.put("u")("u")
      })
      .eval[String]("u") should ===("u")

    (new Catalog)
      .withTransformations[Transformations.TransformationWithImplicitCatalog]((c: Catalog) => c.put("u")("u"))
      .eval[String]("u") should ===("u")
  }

  it should "add put transformations" in {
    (new Catalog)
      .withTransformations(new Transformations.PutTransformation {
        override def apply(c: Catalog): Dep[_] =
          Dep.dep[String]("u", Set.empty)("u")
      })
      .eval[String]("u") should ===("u")

    (new Catalog)
      .withTransformations[Transformations.PutTransformation]((c: Catalog) => Dep.dep[String]("u", Set.empty)("u"))
      .eval[String]("u") should ===("u")
  }

  it should "add put transformations with implicit catalog" in {
    (new Catalog)
      .withTransformations(new Transformations.PutTransformationWithImplicitCatalog {
        override def apply(implicit c: Catalog): Dep[_] =
          Dep.dep[String]("u", Set.empty)("u")
      })
      .eval[String]("u") should ===("u")

    (new Catalog)
      .withTransformations[Transformations.PutTransformationWithImplicitCatalog]((c: Catalog) => Dep.dep[String]("u", Set.empty)("u"))
      .eval[String]("u") should ===("u")
  }

  it should "add multi put transformations" in {
    (new Catalog)
      .withTransformations(new Transformations.MultiPutTransformation {
        override def apply(c: Catalog): Seq[Dep[_]] =
          Seq(Dep.dep[String]("u", Set.empty)("u"))
      })
      .eval[String]("u") should ===("u")

    (new Catalog)
      .withTransformations[Transformations.MultiPutTransformation]((c: Catalog) => Seq(Dep.dep[String]("u", Set.empty)("u")))
      .eval[String]("u") should ===("u")
  }

  it should "add multi put transformations with implicit catalog" in {
    (new Catalog)
      .withTransformations(new Transformations.MultiPutTransformationWithImplicitCatalog {
        override def apply(implicit c: Catalog): Seq[Dep[_]] =
          Seq(Dep.dep[String]("u", Set.empty)("u"))
      })
      .eval[String]("u") should ===("u")

    (new Catalog)
      .withTransformations[Transformations.MultiPutTransformationWithImplicitCatalog]((c: Catalog) => Seq(Dep.dep[String]("u", Set.empty)("u")))
      .eval[String]("u") should ===("u")
  }

  behavior of "explain"

  it should "???" in {

    println((new Catalog).explain("id"))


  }


}
