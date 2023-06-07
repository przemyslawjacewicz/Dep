package pl.epsilondeltalimit.dep.v3

class Dep[A](val id: String, val deps: Set[Dep[_]], a: => A) extends (() => A) {
  private lazy val value = a

  override def apply(): A = value
}

object Dep {

  def unit[A](uid: String)(a: => A): Dep[A] = new Dep[A](uid, Set.empty, a)

  def map2[A, B, C](uid: String)(aDep: Dep[A], bDep: Dep[B])(f: (A, B) => C): Dep[C] =
    new Dep[C](uid, Set(aDep, bDep), f(aDep(), bDep()))

  def map[A, B](aDep: Dep[A])(f: A => B): Dep[B] =
    new Dep[B](aDep.id, aDep.deps, f(aDep()))

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
