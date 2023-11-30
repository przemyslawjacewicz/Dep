package pl.epsilondeltalimit.dep

import org.scalatest.Assertion
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import pl.epsilondeltalimit.dep.Transformations.Transformation

import java.util.concurrent.TimeUnit
import scala.concurrent.duration
import scala.concurrent.duration.Duration

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

  val u: Transformation = (c: Catalog) => c.put("u")(1)

  it should "create a Dep instance with proper id and needs" in {
    info("map")
    assertDep(
      Set[Transformation](
        (c: Catalog) => c.put(c.get[Int]("u").map(_ + 1)),
        u
      )
        .foldLeft(new Catalog)((c, t) => t(c))
        .get[Int]("u_M")
    )("u_M", Set("u"), 2)

    info("map + as")
    assertDep(
      Set[Transformation](
        (c: Catalog) => c.put(c.get[Int]("u").map(_ + 1).as("t")),
        u
      )
        .foldLeft(new Catalog)((c, t) => t(c))
        .get[Int]("t")
    )("t", Set("u"), 2)

    info("map + map")
    assertDep(
      Set[Transformation](
        (c: Catalog) => c.put(c.get[Int]("u").map(_ + 1).map(_ + 1)),
        u
      )
        .foldLeft(new Catalog)((c, t) => t(c))
        .get[Int]("u_M_M")
    )("u_M_M", Set("u"), 3)

    info("map + map + as")
    assertDep(
      Set[Transformation](
        (c: Catalog) => c.put(c.get[Int]("u").map(_ + 1).map(_ + 1).as("t")),
        u
      )
        .foldLeft(new Catalog)((c, t) => t(c))
        .get[Int]("t")
    )("t", Set("u"), 3)
  }

  behavior of "flatMap"

  it should "create a Dep instance with proper id and needs" in {
    info("flatMap")
    assertDep(
      Set[Transformation](
        (c: Catalog) => c.put(c.get[Int]("u").flatMap(_ => c.get[Int]("u2"))),
        _.put("u2")(2),
        u
      )
        .foldLeft(new Catalog)((c, t) => t(c))
        .get[Int]("u_FM")
    )("u_FM", Set("u", "u2"), 2)

    info("flatMap + as")
    assertDep(
      Set[Transformation](
        (c: Catalog) => c.put(c.get[Int]("u").flatMap(_ => c.get[Int]("u2")).as("t")),
        _.put("u2")(2),
        u
      )
        .foldLeft(new Catalog)((c, t) => t(c))
        .get[Int]("t")
    )("t", Set("u", "u2"), 2)

    info("flatMap + flatMap")
    assertDep(
      Set[Transformation](
        (c: Catalog) => c.put(c.get[Int]("u").flatMap(_ => c.get[Int]("u2")).flatMap(_ => c.get[Int]("u3"))),
        _.put("u3")(3),
        _.put("u2")(2),
        u
      )
        .foldLeft(new Catalog)((c, t) => t(c))
        .get[Int]("u_FM_FM")
    )("u_FM_FM", Set("u", "u2", "u3"), 3)

    info("flatMap + flatMap + as")
    assertDep(
      Set[Transformation](
        (c: Catalog) => c.put(c.get[Int]("u").flatMap(_ => c.get[Int]("u2")).flatMap(_ => c.get[Int]("u3")).as("t")),
        _.put("u3")(3),
        _.put("u2")(2),
        u
      )
        .foldLeft(new Catalog)((c, t) => t(c))
        .get[Int]("t")
    )("t", Set("u", "u2", "u3"), 3)

    info("flatMap + map")
    assertDep(
      Set[Transformation](
        (c: Catalog) => c.put(c.get[Int]("u").flatMap(u => c.get[Int]("u2").map(u2 => u + u2))),
        _.put("u2")(2),
        u
      )
        .foldLeft(new Catalog)((c, t) => t(c))
        .get[Int]("u_FM")
    )("u_FM", Set("u", "u2"), 3)

    info("flatMap + map + as")
    assertDep(
      Set[Transformation](
        (c: Catalog) => c.put(c.get[Int]("u").flatMap(u => c.get[Int]("u2").map(u2 => u + u2)).as("t")),
        _.put("u2")(2),
        u
      )
        .foldLeft(new Catalog)((c, t) => t(c))
        .get[Int]("t")
    )("t", Set("u", "u2"), 3)
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
    )("a_M2", Set("a", "b"), 3)

    info("map2 + as")
    assertDep(
      Set[Transformation](
        (c: Catalog) => c.put(c.get[Int]("a").map2(c.get[Int]("b"))(_ + _).as("t")),
        _.put("b")(2),
        _.put("a")(1)
      )
        .foldLeft(new Catalog)((c, t) => t(c))
        .get[Int]("t")
    )("t", Set("a", "b"), 3)
  }

  def assertDep[A](dep: Dep[A])(id: String, needs: Set[String], value: A): Assertion = {
    dep.id should ===(id)
    dep.needs() should ===(needs)
    dep() should ===(value)
  }

}
