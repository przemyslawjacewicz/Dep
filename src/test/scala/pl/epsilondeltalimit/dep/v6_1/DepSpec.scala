package pl.epsilondeltalimit.dep.v6_1

import org.scalatest.Assertion
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class DepSpec extends AnyFlatSpec with Matchers {

  behavior of "map"

  val u: Transformation = (c: Catalog) => c.unit("u")(1)

  it should "create a Dep instance with proper id and needs" in {
    assertDep(
      Set[Transformation](
        u,
        (c: Catalog) => c.put(c.get[Int]("u").map(_ + 1))
      )
        .foldLeft(new Catalog)((c, t) => t(c))
        .get[Int]("u_M")
    )("u_M", Set("u"), 2)

    assertDep(
      Set[Transformation](
        u,
        (c: Catalog) => c.put(c.get[Int]("u").map(_ + 1).as("t"))
      )
        .foldLeft(new Catalog)((c, t) => t(c))
        .get[Int]("t")
    )("t", Set("u"), 2)

    assertDep(
      Set[Transformation](
        u,
        (c: Catalog) => c.put(c.get[Int]("u").map(_ + 1).map(_ + 1))
      )
        .foldLeft(new Catalog)((c, t) => t(c))
        .get[Int]("u_M_M")
    )("u_M_M", Set("u"), 3)

    assertDep(
      Set[Transformation](
        u,
        (c: Catalog) => c.put(c.get[Int]("u").map(_ + 1).map(_ + 1).as("t"))
      )
        .foldLeft(new Catalog)((c, t) => t(c))
        .get[Int]("t")
    )("t", Set("u"), 3)
  }

  behavior of "flatMap"

  it should "create a Dep instance with proper id and needs" in {
    assertDep(
      Set[Transformation](
        u, _.unit("u2")(2),
        (c: Catalog) => c.put(c.get[Int]("u").flatMap(_ => c.get[Int]("u2")))
      )
        .foldLeft(new Catalog)((c, t) => t(c))
        .get[Int]("u_F")
    )("u_F", Set("u", "u2"), 2)

    assertDep(
      Set[Transformation](
        u, _.unit("u2")(2),
        (c: Catalog) => c.put(c.get[Int]("u").flatMap(_ => c.get[Int]("u2")).as("t"))
      )
        .foldLeft(new Catalog)((c, t) => t(c))
        .get[Int]("t")
    )("t", Set("u", "u2"), 2)

    assertDep(
      Set[Transformation](
        u, _.unit("u2")(2), _.unit("u3")(3),
        (c: Catalog) => c.put(c.get[Int]("u").flatMap(_ => c.get[Int]("u2")).flatMap(_ => c.get[Int]("u3")))
      )
        .foldLeft(new Catalog)((c, t) => t(c))
        .get[Int]("u_F_F")
    )("u_F_F", Set("u", "u2", "u3"), 3)

    assertDep(
      Set[Transformation](
        u, _.unit("u2")(2), _.unit("u3")(3),
        (c: Catalog) => c.put(c.get[Int]("u").flatMap(_ => c.get[Int]("u2")).flatMap(_ => c.get[Int]("u3")).as("t"))
      )
        .foldLeft(new Catalog)((c, t) => t(c))
        .get[Int]("t")
    )("t", Set("u", "u2", "u3"), 3)

    assertDep(
      Set[Transformation](
        u, _.unit("u2")(2),
        (c: Catalog) => c.put(c.get[Int]("u").flatMap(u => c.get[Int]("u2").map(u2 => u + u2)))
      )
        .foldLeft(new Catalog)((c, t) => t(c))
        .get[Int]("u_F")
    )("u_F", Set("u", "u2"), 3)

    assertDep(
      Set[Transformation](
        u, _.unit("u2")(2),
        (c: Catalog) => c.put(c.get[Int]("u").flatMap(u => c.get[Int]("u2").map(u2 => u + u2)).as("t"))
      )
        .foldLeft(new Catalog)((c, t) => t(c))
        .get[Int]("t")
    )("t", Set("u", "u2"), 3)
  }

  def assertDep[A](dep: Dep[A])(id: String, needs: Set[String], value: A): Assertion = {
    dep.id should ===(id)
    dep.needs() should ===(needs)
    dep() should ===(value)
  }

}
