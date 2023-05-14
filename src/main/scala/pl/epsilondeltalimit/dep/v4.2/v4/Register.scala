package pl.epsilondeltalimit.dep.v4.2.v4

import scala.collection.mutable

trait Register {
  def put[A](uid: String, value: Once[A]): Register

  def get[A](uid: String): Once[A]
}

class SimpleRegister extends Register {
  private val s: mutable.Map[String, Once[_]] = mutable.Map.empty

  override def put[A](uid: String, value: Once[A]): Register = {
    s += (uid -> value)
    this
  }

  override def get[A](uid: String): Once[A] =
      s(uid).asInstanceOf[Once[A]]
}
