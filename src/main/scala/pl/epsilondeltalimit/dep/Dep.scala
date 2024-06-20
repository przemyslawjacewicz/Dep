package pl.epsilondeltalimit.dep

//todo: consider removing needs forwarding
//todo: extend () => A is unexpected -> suggests that there are no dependencies for this Dep, which may be not true
sealed abstract class Dep[A](val id: String, val needs: () => Set[String], val value: () => A) extends (() => A) {
  private lazy val cached = value()

  override def apply(): A =
    cached

  // todo: consider random id
  def map[B](f: A => B): Dep[B] =
    this match {
      case _: LeafDep[_] =>
        BranchDep[B](s"${id}_M", () => needs() + id, () => f(apply()))
      case _: BranchDep[_] =>
        BranchDep[B](s"${id}_M", () => needs(), () => f(apply()))
    }

  def map2[B, C](b: Dep[B])(f: (A, B) => C): Dep[C] =
    this match {
      case _: LeafDep[_] =>
        b match {
          case _: LeafDep[_] =>
            BranchDep[C](s"${id}_M2", () => needs() + id + b.id, () => f(apply(), b()))
          case _: BranchDep[_] =>
            BranchDep[C](s"${id}_M2", () => needs() + id, () => f(apply(), b()))
        }
      case _: BranchDep[_] =>
        b match {
          case _: LeafDep[_] =>
            BranchDep[C](s"${id}_M2", () => needs() + b.id, () => f(apply(), b()))
          case _: BranchDep[_] =>
            BranchDep[C](s"${id}_M2", needs, () => f(apply(), b()))
        }
    }

  def flatMap[B](f: A => Dep[B]): Dep[B] =
    this match {
      case _: LeafDep[_] =>
        lazy val getNeeds =
          f(apply()) match {
            case dep: LeafDep[_]   => needs() + id ++ dep.needs() + dep.id
            case dep: BranchDep[_] => needs() + id ++ dep.needs()
          }

        BranchDep[B](s"${id}_FM", () => getNeeds, () => f(apply()).apply())
      case _: BranchDep[_] =>
        lazy val getNeeds =
          f(apply()) match {
            case dep: LeafDep[_]   => needs() ++ dep.needs() + dep.id
            case dep: BranchDep[_] => needs() ++ dep.needs()
          }

        BranchDep[B](s"${id}_FM", () => getNeeds, () => f(apply()).apply())
    }

  //todo: consider a different name e.g. collect
  def as(id: String): Dep[A] =
    LeafDep[A](id, needs, apply)

}

case class LeafDep[A](override val id: String, override val needs: () => Set[String], override val value: () => A)
    extends Dep[A](id, needs, value)

case class BranchDep[A](override val id: String, override val needs: () => Set[String], override val value: () => A)
    extends Dep[A](id, needs, value)

object Dep {

  def dep[A](id: String)(value: => A): Dep[A] =
    LeafDep[A](id, () => Set.empty, () => value)

  def dep[A](id: String, needs: => Set[String])(value: => A): Dep[A] =
    LeafDep[A](id, () => needs, () => value)

  def leafDep[A](id: String)(value: => A): Dep[A] =
    LeafDep[A](id, () => Set.empty, () => value)

  def leafDep[A](id: String, needs: => Set[String])(value: => A): Dep[A] =
    LeafDep[A](id, () => needs, () => value)

  def branchDep[A](id: String)(value: => A): Dep[A] =
    LeafDep[A](id, () => Set.empty, () => value)

  def branchDep[A](id: String, needs: => Set[String])(value: => A): Dep[A] =
    LeafDep[A](id, () => needs, () => value)

  object implicits {

    implicit class StringImplicits(id: String) {
      def as[A](implicit c: Catalog): Dep[A] =
        c.get[A](id)
    }

  }
}
