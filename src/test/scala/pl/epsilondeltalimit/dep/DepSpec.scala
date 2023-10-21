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

    val dep = new Dep[Unit]("dep", () => Set.empty[String])(() => {
      TimeUnit.SECONDS.sleep(1)
      ()
    })

    time(dep()) should be > Duration(1, duration.SECONDS).toNanos
    time(dep()) should be < Duration(1, duration.SECONDS).toNanos
  }

  behavior of "map"

  val u: Transformation = (c: Catalog) => c.unit("u")(1)

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
        _.unit("u2")(2),
        u
      )
        .foldLeft(new Catalog)((c, t) => t(c))
        .get[Int]("u_F")
    )("u_F", Set("u", "u2"), 2)

    info("flatMap + as")
    assertDep(
      Set[Transformation](
        (c: Catalog) => c.put(c.get[Int]("u").flatMap(_ => c.get[Int]("u2")).as("t")),
        _.unit("u2")(2),
        u
      )
        .foldLeft(new Catalog)((c, t) => t(c))
        .get[Int]("t")
    )("t", Set("u", "u2"), 2)

    info("flatMap + flatMap")
    assertDep(
      Set[Transformation](
        (c: Catalog) => c.put(c.get[Int]("u").flatMap(_ => c.get[Int]("u2")).flatMap(_ => c.get[Int]("u3"))),
        _.unit("u3")(3),
        _.unit("u2")(2),
        u
      )
        .foldLeft(new Catalog)((c, t) => t(c))
        .get[Int]("u_F_F")
    )("u_F_F", Set("u", "u2", "u3"), 3)

    info("flatMap + flatMap + as")
    assertDep(
      Set[Transformation](
        (c: Catalog) => c.put(c.get[Int]("u").flatMap(_ => c.get[Int]("u2")).flatMap(_ => c.get[Int]("u3")).as("t")),
        _.unit("u3")(3),
        _.unit("u2")(2),
        u
      )
        .foldLeft(new Catalog)((c, t) => t(c))
        .get[Int]("t")
    )("t", Set("u", "u2", "u3"), 3)

    info("flatMap + map")
    assertDep(
      Set[Transformation](
        (c: Catalog) => c.put(c.get[Int]("u").flatMap(u => c.get[Int]("u2").map(u2 => u + u2))),
        _.unit("u2")(2),
        u
      )
        .foldLeft(new Catalog)((c, t) => t(c))
        .get[Int]("u_F")
    )("u_F", Set("u", "u2"), 3)

    info("flatMap + map + as")
    assertDep(
      Set[Transformation](
        (c: Catalog) => c.put(c.get[Int]("u").flatMap(u => c.get[Int]("u2").map(u2 => u + u2)).as("t")),
        _.unit("u2")(2),
        u
      )
        .foldLeft(new Catalog)((c, t) => t(c))
        .get[Int]("t")
    )("t", Set("u", "u2"), 3)
  }

  behavior of "map2"

  it should "create a Dep instance with proper id and needs" in {
    assertDep(
      Set[Transformation](
        (c: Catalog) => c.put(Dep.map2("c")(c.get[Int]("a"), c.get[Int]("b"))(_ + _)),
        _.unit("b")(2),
        _.unit("a")(1)
      )
        .foldLeft(new Catalog)((c, t) => t(c))
        .get[Int]("c")
    )("c", Set("a", "b"), 3)
  }

  def assertDep[A](dep: Dep[A])(id: String, needs: Set[String], value: A): Assertion = {
    dep.id should ===(id)
    dep.needs() should ===(needs)
    dep() should ===(value)
  }

}
