package pl.epsilondeltalimit.dep.alternatives.v3

// this implementation forces all dependency references to be available e.g. 'a' and 'b' need to be available to
// add/implement 'c'
class Dep[A](val id: String, val deps: Set[Dep[_]], a: => A) extends (() => A) {
  private lazy val value = a

  override def apply(): A = value
}

object Dep {

  def unit[A](id: String)(a: => A): Dep[A] =
    new Dep[A](id, Set.empty, a)

  def map2[A, B, C](id: String)(a: Dep[A], b: Dep[B])(f: (A, B) => C): Dep[C] =
    new Dep[C](id, Set(a, b), f(a(), b()))

  def map[A, B](a: Dep[A])(f: A => B): Dep[B] =
    new Dep[B](a.id, a.deps, f(a()))

  //   todo: simplistic implementation => should be replaced with a solution based on graph
  def run[A](dep: Dep[A]): A = {
    def loop(deps: Set[Dep[_]], dep: Dep[_]): Set[Dep[_]] =
      dep.deps match {
        case s if s.isEmpty => deps
        case s => s.flatMap(d => loop(deps ++ s, d))
      }

    val deps = loop(Set.empty, dep)

    deps.toSeq.sortBy(_.id).foreach(_ ())

    dep()
  }
}
