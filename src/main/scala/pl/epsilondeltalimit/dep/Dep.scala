package pl.epsilondeltalimit.dep

//todo: make constructor private ?
class Dep[A](val id: String, val needs: () => Set[String])(value: () => A) extends (() => A) {
  private lazy val cached = value()

  override def apply(): A =
    cached

  def map[B](f: A => B): Dep[B] =
    new Dep[B](s"${id}_M", () => (Set(this.id) ++ this.needs()).filterNot(isDerived))(() => f(apply()))

  def map2[B, C](b: Dep[B])(f: (A, B) => C): Dep[C] =
    new Dep[C](s"${id}_M2", () => Set(id, b.id))(() => f(apply(), b()))

  def flatMap[B](f: A => Dep[B]): Dep[B] = {
    new Dep[B](s"${id}_F", () => (Set(this.id, f(apply()).id) ++ this.needs() ++ f(apply()).needs()).filterNot(isDerived))(() => f(apply()).apply())
  }

  //todo: consider a better way of marking a Dep as derived/intermediate
  private def isDerived(id: String): Boolean =
    id.endsWith("_M") || id.endsWith("_F") || id.endsWith("_M2")

  def as(id: String): Dep[A] =
    new Dep[A](id, () => needs())(() => apply())

}

object Dep {

  object implicits {

    implicit class StringImplicits(id: String) {
      def as[A](implicit c: Catalog): Dep[A] =
        c.get[A](id)

    }

  }
}
