package pl.epsilondeltalimit.dep.v6_1

import org.scalatest.flatspec.AnyFlatSpec

class DepSpec extends AnyFlatSpec {

  behavior of "map"

  it should "create a Dep instance" in {
    val transformations = Set(
      new Transformation {
        override def apply(c: Catalog): Catalog =
          c.unit("t1")(1)
      },
      new Transformation {
        override def apply(c: Catalog): Catalog =
          c.put {
            c.get[Int]("t1").map(i => i + 1)
          }
      }
    )

    val catalog: Catalog = transformations.foldLeft(new Catalog)((c, t) => t(c))
//    println(catalog.eval[Int]("t?"))
    catalog.s.foreach(dep => println(s"${dep.id}: needs=${dep.needs().mkString(",")}"))

  }

}
