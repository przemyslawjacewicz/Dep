package pl.epsilondeltalimit.dep.catalog.typed

import scala.collection.mutable

object Runner {
  def main(args: Array[String]): Unit = {
    val a = Key[Int]
    val b = Key[String]
    val c = Key[Float]

    val record: Record = a ~> 1 ++ b ~> "abc" ++ c ~> 1.0f

    val aRecord: Int    = record(a)
    val bRecord: String = record(b)

//    val h = HMap(a ~> 1, b ~> "abc")
//    val aH = h(a)

    val m = mutable.Map[String, mutable.Set[String]]("a" -> mutable.Set("a"), "b" -> mutable.Set("b"))
    println(m)

//    m.updated("a", m("a") += "b" )
    m("a") += "b"
    println(m)
  }
}
