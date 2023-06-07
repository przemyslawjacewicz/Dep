package pl.epsilondeltalimit.dep.v4_2

import pl.epsilondeltalimit.once.Once

import scala.collection.mutable

trait Register {
  def put[A](uid: String, value: Once[A]): Register

  def get[A](uid: String): Once[A]

  def contains(uid: String): Boolean
}

//todo: simplistic implementation
class SimpleMutableRegister extends Register {
  private val s = mutable.Map.empty[String, Once[_]]

  override def put[A](uid: String, value: Once[A]): Register = {
    s += (uid -> value)
    this
  }

  override def get[A](uid: String): Once[A] =
    s(uid).asInstanceOf[Once[A]]

  override def contains(uid: String): Boolean = s.contains(uid)
}
