package pl.epsilondeltalimit.dep.design


object Dep {

//  trait DependencyContext

  trait Dep[A] extends (() => A) {
    val id: String

    val deps:
  }

  def unit[A](a: => A)(id: String): Dep[A] =
    new Dep[A] {
      override val id: String = id

      override def apply(v1: DependencyContext): A = a
    }

  def map2[A, B, C](dep1: Dep[A], dep2: Dep[B])(f: (A, B) => C)(id: String): Dep[C] =
    new Dep[C] {
      override val id: String = id

      override def apply(c: DependencyContext): C = f(dep1(c), dep2(c))
    }

//  def run[A](d: Dep[A]): A =
//    d(r)

}
