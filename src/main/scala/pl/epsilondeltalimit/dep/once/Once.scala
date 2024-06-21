package pl.epsilondeltalimit.dep.once

//import scala.language.implicitConversions

//todo: consider removing
class Once[A](a: => A) extends (() => A) {
  private lazy val value = a

  override def apply(): A = value
}

object Once {
  object implicits {
//    implicit def value2Once[A](a: A): Once[A] =
//      new Once[A](a)

//    implicit class OnceOps[A](a: A) ex


  }
}
