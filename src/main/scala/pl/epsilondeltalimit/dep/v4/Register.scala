package pl.epsilondeltalimit.dep.v4

import scala.collection.mutable

trait Register[A] {
  def put(uid: String, value: Once[A]): Register[A]

  def get(uid: String): Once[A]
}

class SimpleRegister[A] extends Register[A] {
  private val s: mutable.Map[String, Once[A]] = mutable.Map.empty

  override def put(uid: String, value: Once[A]): Register[A] = {
    s += (uid -> value)
    this
  }

  override def get(uid: String): Once[A] =
    s(uid)
}
