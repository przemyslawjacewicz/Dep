package pl.epsilondeltalimit.dep.v4_1_1

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
