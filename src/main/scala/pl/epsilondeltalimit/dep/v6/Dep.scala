package pl.epsilondeltalimit.dep.v6

import org.apache.spark.sql.DataFrame

class Dep(val id: String, val deps: () => Set[String])(value: () => DataFrame) extends (() => DataFrame) {
  private lazy val cached = value()

  override def apply(): DataFrame =
    cached

  def map(id: String)(f: DataFrame => DataFrame): Dep =
    new Dep(id, () => Set(this.id))(() => f(apply()))
}

//todo: add smart constructors
object Dep {
//  def dep(id: String, deps: Set[String])(value: => DataFrame): Dep =
//    new Dep(id, () => deps)(() => value)

//  def dep(id: String, deps: () => Set[String])(value: () => DataFrame): Dep =
//    new Dep(id, deps)(value)

  def map2(id: String)(a: Dep, b: Dep)(f: (DataFrame, DataFrame) => DataFrame): Dep =
    new Dep(id, () => Set(a.id, b.id))(() => f(a(), b()))

  def mapN(id: String)(deps: Dep*)(f: Seq[DataFrame] => DataFrame): Dep =
    new Dep(id, () => deps.flatMap(_.deps()).toSet)(() => f(deps.map(_())))

}
