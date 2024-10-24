package pl.epsilondeltalimit.dep.dep

import pl.epsilondeltalimit.dep.catalog.Catalog

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
            BranchDep[C](s"${id}_M2", () => needs() + id ++ b.needs() + b.id, () => f(apply(), b()))
          case _: BranchDep[_] =>
            BranchDep[C](s"${id}_M2", () => needs() + id ++ b.needs(), () => f(apply(), b()))
        }
      case _: BranchDep[_] =>
        b match {
          case _: LeafDep[_] =>
            BranchDep[C](s"${id}_M2", () => needs() ++ b.needs() + b.id, () => f(apply(), b()))
          case _: BranchDep[_] =>
            BranchDep[C](s"${id}_M2", () => needs() ++ b.needs(), () => f(apply(), b()))
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

  // todo: consider a different name e.g. collect
  def as(id: String): Dep[A] =
    LeafDep[A](id, needs, apply)

}

//todo: consider a different name - Result, Res, Final, EndDep => Result
case class LeafDep[A] private[dep] (override val id: String,
                                    override val needs: () => Set[String],
                                    override val value: () => A)
    extends Dep[A](id, needs, value)

object LeafDep {
  private[dep] def apply[A](id: String, needs: () => Set[String], value: () => A): LeafDep[A] =
    new LeafDep(id, needs, value)
}

//todo: consider a different name - Intermediate, Inter, IDep, Transitive, Part => Part
case class BranchDep[A] private[dep] (override val id: String,
                                      override val needs: () => Set[String],
                                      override val value: () => A)
    extends Dep[A](id, needs, value)

object BranchDep {
  private[dep] def apply[A](id: String, needs: () => Set[String], value: () => A): BranchDep[A] =
    new BranchDep[A](id, needs, value)
}

object Dep {

  def dep[A](id: String)(value: => A): Dep[A] =
    LeafDep[A](id, () => Set.empty, () => value)

  def dep[A](id: String, needs: => Set[String])(value: => A): Dep[A] =
    LeafDep[A](id, () => needs, () => value)

  object implicits {

    implicit class StringImplicits(id: String) {
      def as[A](implicit c: Catalog): Dep[A] =
        c.get[A](id)
    }

  }
}
