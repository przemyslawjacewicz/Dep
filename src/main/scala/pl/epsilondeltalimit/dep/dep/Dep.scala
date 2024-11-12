package pl.epsilondeltalimit.dep.dep

sealed abstract class Dep[A](val id: String, val needs: () => Set[String], val value: () => A) extends (() => A) {
  private lazy val cached = value()

  override def apply(): A =
    cached

  def map[B](f: A => B): Dep[B] =
    this match {
      case _: Result[_] =>
        Part[B](s"${id}_M", () => needs() + id, () => f(apply()))
      case _: Part[_] =>
        Part[B](s"${id}_M", () => needs(), () => f(apply()))
    }

  def map2[B, C](b: Dep[B])(f: (A, B) => C): Dep[C] =
    this match {
      case _: Result[_] =>
        b match {
          case _: Result[_] =>
            Part[C](s"${id}_M2", () => needs() + id ++ b.needs() + b.id, () => f(apply(), b()))
          case _: Part[_] =>
            Part[C](s"${id}_M2", () => needs() + id ++ b.needs(), () => f(apply(), b()))
        }
      case _: Part[_] =>
        b match {
          case _: Result[_] =>
            Part[C](s"${id}_M2", () => needs() ++ b.needs() + b.id, () => f(apply(), b()))
          case _: Part[_] =>
            Part[C](s"${id}_M2", () => needs() ++ b.needs(), () => f(apply(), b()))
        }
    }

  def flatMap[B](f: A => Dep[B]): Dep[B] =
    this match {
      case _: Result[_] =>
        lazy val getNeeds =
          f(apply()) match {
            case dep: Result[_] => needs() + id ++ dep.needs() + dep.id
            case dep: Part[_]   => needs() + id ++ dep.needs()
          }

        Part[B](s"${id}_FM", () => getNeeds, () => f(apply()).apply())
      case _: Part[_] =>
        lazy val getNeeds =
          f(apply()) match {
            case dep: Result[_] => needs() ++ dep.needs() + dep.id
            case dep: Part[_]   => needs() ++ dep.needs()
          }

        Part[B](s"${id}_FM", () => getNeeds, () => f(apply()).apply())
    }

  def as(id: String): Result[A] =
    Result[A](id, needs, apply)

  override def toString(): String =
    s"${getClass.getSimpleName}: id=$id, needs=${needs()}, value=${value()}"

}

case class Result[A] private[dep] (override val id: String,
                                   override val needs: () => Set[String],
                                   override val value: () => A)
    extends Dep[A](id, needs, value)

object Result {
  private[dep] def apply[A](id: String, needs: () => Set[String], value: () => A): Result[A] =
    new Result(id, needs, value)
}

case class Part[A] private[dep] (override val id: String,
                                 override val needs: () => Set[String],
                                 override val value: () => A)
    extends Dep[A](id, needs, value)

object Part {
  private[dep] def apply[A](id: String, needs: () => Set[String], value: () => A): Part[A] =
    new Part[A](id, needs, value)
}

object Dep {

  def apply[A](id: String)(value: => A): Result[A] =
    Result[A](id, () => Set.empty, () => value)

  def apply[A](id: String, needs: => Set[String])(value: => A): Result[A] =
    Result[A](id, () => needs, () => value)

}
