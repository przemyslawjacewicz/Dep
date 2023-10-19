package pl.epsilondeltalimit.dep.alternatives.v5

import org.apache.spark.sql.DataFrame

class Dep(val id: String, val deps: Set[String])(v: => DataFrame) extends (() => DataFrame) {
  private lazy val value = v

  override def apply(): DataFrame =
    value
}
