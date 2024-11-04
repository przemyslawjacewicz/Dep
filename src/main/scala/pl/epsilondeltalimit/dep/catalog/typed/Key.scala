package pl.epsilondeltalimit.dep.catalog.typed

final class Key[T] {
  def ~>(value: T): Record = new RecordImpl(Map(this -> value))
}

object Key {
  def apply[T] = new Key[T]
}
