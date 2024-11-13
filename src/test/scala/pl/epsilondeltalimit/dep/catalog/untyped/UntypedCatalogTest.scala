package pl.epsilondeltalimit.dep.catalog.untyped

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import pl.epsilondeltalimit.dep.catalog.Catalog
import pl.epsilondeltalimit.dep.dep.{Dep, Result}
import pl.epsilondeltalimit.dep.transformation.implicits._

import java.util.concurrent.TimeUnit
import scala.concurrent.duration
import scala.concurrent.duration.Duration

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

      "evaluate all results for resources with dependencies" in {
        val x = (c: Catalog) => c.put("x") { TimeUnit.SECONDS.sleep(1); c.get[String]("w")() + "x" }
        val w = (c: Catalog) => c.put("w") { TimeUnit.SECONDS.sleep(1); c.get[String]("u")() + "w" }
        val u = (c: Catalog) => c.put("u") { TimeUnit.SECONDS.sleep(1); "u" }

        val c = (new UntypedCatalog)
          .withTransformations[Catalog => Catalog](x, w, u)

        c.run()

        time(c.get[String]("u")()) should be < Duration(1, duration.SECONDS).toNanos
        time(c.get[String]("w")()) should be < Duration(1, duration.SECONDS).toNanos
        time(c.get[String]("x")()) should be < Duration(1, duration.SECONDS).toNanos
      }

    }

    "result transformations are added" should {

      "throw an exception upon evaluation when multiple results with the same id are added" in {
        a[RuntimeException] should be thrownBy {
          (new UntypedCatalog)
            .withTransformations[Catalog => Result[_]](
              (_: Catalog) => Dep("u")("u1"),
              (_: Catalog) => Dep("u")("u2")
            )
            .eval[String]("u")
        }
      }

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

      "evaluate all results for resources with dependencies" in {
        val x = (c: Catalog) => Dep("x") { TimeUnit.SECONDS.sleep(1); c.get[String]("w")() + "x" }
        val w = (c: Catalog) => Dep("w") { TimeUnit.SECONDS.sleep(1); c.get[String]("u")() + "w" }
        val u = (_: Catalog) => Dep("u") { TimeUnit.SECONDS.sleep(1); "u" }

        val c = (new UntypedCatalog)
          .withTransformations[Catalog => Result[_]](x, w, u)

        c.run()

        time(c.get[String]("u")()) should be < Duration(1, duration.SECONDS).toNanos
        time(c.get[String]("w")()) should be < Duration(1, duration.SECONDS).toNanos
        time(c.get[String]("x")()) should be < Duration(1, duration.SECONDS).toNanos
      }

    }

  }

  def time(block: => Unit): Long = {
    val start = System.nanoTime()
    block
    val end = System.nanoTime()
    end - start
  }

}
