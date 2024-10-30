package pl.epsilondeltalimit.dep.catalog

final class Key[T] {
  def ~>(value: T): Record = new RecordImpl(Map(this -> value))
}

object Key {
  def apply[T] = new Key[T]
}
