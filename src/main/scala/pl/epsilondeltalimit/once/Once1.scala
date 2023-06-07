package pl.epsilondeltalimit.once

import scala.collection.mutable
import scala.language.implicitConversions

class Once1[T1, R](r: T1 => R) extends (T1 => R) {
  private lazy val cached = new mutable.HashMap[T1, R].empty

  override def apply(t1: T1): R = {
    if (!cached.contains(t1)) {
      cached.put(t1, r(t1))
    }
    cached(t1)
  }
}
