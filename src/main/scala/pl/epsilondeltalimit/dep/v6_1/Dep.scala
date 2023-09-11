package pl.epsilondeltalimit.dep.v6_1

class Dep[A](val id: String, val deps: () => Set[String])(value: () => A) extends (() => A) {
  private lazy val cached = value()

  override def apply(): A =
    cached

  def map[B](id: String)(f: A => B): Dep[B] =
    new Dep[B](id, () => Set(this.id))(() => f(apply()))
}

object Dep {
  def map2[A, B, C](id: String)(a: Dep[A], b: Dep[B])(f: (A, B) => C): Dep[C] =
    new Dep[C](id, () => Set(a.id, b.id))(() => f(a(), b()))
}
