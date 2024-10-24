package pl.epsilondeltalimit.dep.dep

import org.scalatest.Assertion
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import pl.epsilondeltalimit.dep.catalog.Catalog
import pl.epsilondeltalimit.dep.transformation.Transformation

import java.util.concurrent.TimeUnit
import scala.concurrent.duration
import scala.concurrent.duration.Duration

//todo: remove me
class DepSpec extends AnyFlatSpec with Matchers {

  behavior of "apply"

  it should "cache the result" in {

    def time(block: => Unit): Long = {
      val start = System.nanoTime()
      block
      val end = System.nanoTime()
      end - start
    }

    val dep = LeafDep[Unit]("dep", () => Set.empty, () => TimeUnit.SECONDS.sleep(1))

    time(dep()) should be > Duration(1, duration.SECONDS).toNanos
    time(dep()) should be < Duration(1, duration.SECONDS).toNanos
  }

  behavior of "map"

  val t: Transformation = _.put("u")(1)

  it should "create a Dep instance with proper id and needs" in {
    info("map")
    assertDep(
      Set[Transformation](
        (c: Catalog) => c.put(c.get[Int]("u").map(_ + 1)),
        t
      )
        .foldLeft(new Catalog)((c, t) => t(c))
        .get[Int]("u_M")
    )(id = "u_M", needs = Set("u"), value = 2)

    info("map + as")
    assertDep(
      Set[Transformation](
        (c: Catalog) => c.put(c.get[Int]("u").map(_ + 1).as("t")),
        t
      )
        .foldLeft(new Catalog)((c, t) => t(c))
        .get[Int]("t")
    )(id = "t", needs = Set("u"), value = 2)

    info("map + map")
    assertDep(
      Set[Transformation](
        (c: Catalog) => c.put(c.get[Int]("u").map(_ + 1).map(_ + 1)),
        t
      )
        .foldLeft(new Catalog)((c, t) => t(c))
        .get[Int]("u_M_M")
    )(id = "u_M_M", needs = Set("u"), value = 3)

    info("map + map + as")
    assertDep(
      Set[Transformation](
        (c: Catalog) => c.put(c.get[Int]("u").map(_ + 1).map(_ + 1).as("t")),
        t
      )
        .foldLeft(new Catalog)((c, t) => t(c))
        .get[Int]("t")
    )(id = "t", needs = Set("u"), value = 3)
  }

  behavior of "flatMap"

  it should "create a Dep instance with proper id and needs" in {
    info("flatMap")
    assertDep(
      Set[Transformation](
        (c: Catalog) => c.put(c.get[Int]("u").flatMap(_ => c.get[Int]("u2"))),
        _.put("u2")(2),
        t
      )
        .foldLeft(new Catalog)((c, t) => t(c))
        .get[Int]("u_FM")
    )(id = "u_FM", needs = Set("u", "u2"), value = 2)

    info("flatMap + as")
    assertDep(
      Set[Transformation](
        (c: Catalog) => c.put(c.get[Int]("u").flatMap(_ => c.get[Int]("u2")).as("t")),
        _.put("u2")(2),
        t
      )
        .foldLeft(new Catalog)((c, t) => t(c))
        .get[Int]("t")
    )(id = "t", needs = Set("u", "u2"), value = 2)

    info("flatMap + flatMap")
    assertDep(
      Set[Transformation](
        (c: Catalog) => c.put(c.get[Int]("u").flatMap(_ => c.get[Int]("u2")).flatMap(_ => c.get[Int]("u3"))),
        _.put("u3")(3),
        _.put("u2")(2),
        t
      )
        .foldLeft(new Catalog)((c, t) => t(c))
        .get[Int]("u_FM_FM")
    )(id = "u_FM_FM", needs = Set("u", "u2", "u3"), value = 3)

    info("flatMap + flatMap + as")
    assertDep(
      Set[Transformation](
        (c: Catalog) => c.put(c.get[Int]("u").flatMap(_ => c.get[Int]("u2")).flatMap(_ => c.get[Int]("u3")).as("t")),
        _.put("u3")(3),
        _.put("u2")(2),
        t
      )
        .foldLeft(new Catalog)((c, t) => t(c))
        .get[Int]("t")
    )(id = "t", needs = Set("u", "u2", "u3"), value = 3)

    info("flatMap + map")
    assertDep(
      Set[Transformation](
        (c: Catalog) => c.put(c.get[Int]("u").flatMap(u => c.get[Int]("u2").map(u2 => u + u2))),
        _.put("u2")(2),
        t
      )
        .foldLeft(new Catalog)((c, t) => t(c))
        .get[Int]("u_FM")
    )(id = "u_FM", needs = Set("u", "u2"), value = 3)

    info("flatMap + map + as")
    assertDep(
      Set[Transformation](
        (c: Catalog) => c.put(c.get[Int]("u").flatMap(u => c.get[Int]("u2").map(u2 => u + u2)).as("t")),
        _.put("u2")(2),
        t
      )
        .foldLeft(new Catalog)((c, t) => t(c))
        .get[Int]("t")
    )(id = "t", needs = Set("u", "u2"), value = 3)
  }

  behavior of "map2"

  it should "create a Dep instance with proper id and needs" in {
    info("map2")
    assertDep(
      Set[Transformation](
        (c: Catalog) => c.put(c.get[Int]("a").map2(c.get[Int]("b"))(_ + _)),
        _.put("b")(2),
        _.put("a")(1)
      )
        .foldLeft(new Catalog)((c, t) => t(c))
        .get[Int]("a_M2")
    )(id = "a_M2", needs = Set("a", "b"), value = 3)

    info("map2 + as")
    assertDep(
      Set[Transformation](
        (c: Catalog) => c.put(c.get[Int]("a").map2(c.get[Int]("b"))(_ + _).as("t")),
        _.put("b")(2),
        _.put("a")(1)
      )
        .foldLeft(new Catalog)((c, t) => t(c))
        .get[Int]("t")
    )(id = "t", needs = Set("a", "b"), value = 3)
  }

  def assertDep[A](dep: Dep[A])(id: String, needs: Set[String], value: A): Assertion = {
    dep.id should ===(id)
    dep.needs() should ===(needs)
    dep() should ===(value)
  }

}
