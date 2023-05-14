package pl.epsilondeltalimit.dep.v4.2.v4

import scala.language.implicitConversions

class Once[A](a: => A) extends (() => A) {
  private lazy val value = a

  override def apply(): A = value
}

object Once {
  object implicits {
    implicit def value2Once[A](a: => A): Once[A] =
      new Once[A](a)
  }
}
