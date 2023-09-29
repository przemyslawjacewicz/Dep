package pl.epsilondeltalimit.dep.v6_1

class Dep[A](val id: String, val deps: () => Set[String])(value: () => A) extends (() => A) {
  private lazy val cached = value()
  private lazy val isDerived = id.endsWith("_M") || id.endsWith("_F")

  override def apply(): A =
    cached

  def map[B](id: String)(f: A => B): Dep[B] =
    new Dep[B](id, () => if (isDerived) Set.empty else Set(this.id))(() => f(apply()))

  def map[B](f: A => B): Dep[B] =
    new Dep[B](s"${id}_M", () => if (isDerived) Set.empty else Set(this.id))(() => f(apply()))

  def flatMap[B](id: String)(f: A => Dep[B]): Dep[B] =
    new Dep[B](id, () => if (isDerived) Set.empty else Set(this.id))(() => f(apply()).apply())

  def flatMap[B](f: A => Dep[B]): Dep[B] =
    new Dep[B](s"${id}_F", () => if (isDerived) Set.empty else Set(this.id))(() => f(apply()).apply())

}

object Dep {
  def map2[A, B, C](id: String)(a: Dep[A], b: Dep[B])(f: (A, B) => C): Dep[C] =
    new Dep[C](id, () => Set(a.id, b.id))(() => f(a(), b()))
}
