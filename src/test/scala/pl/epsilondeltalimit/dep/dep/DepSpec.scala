package pl.epsilondeltalimit.dep.dep

import org.scalactic.Equality
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import pl.epsilondeltalimit.dep.transformation.CatalogTransformation

import java.util.concurrent.TimeUnit
import scala.concurrent.duration
import scala.concurrent.duration.Duration

class DepSpec extends AnyFlatSpec with Matchers {

  implicit val depEq: Equality[Dep[Int]] = (a: Dep[Int], b: Any) => {
    println(s"DEBUG: a.class=${a.getClass}, id=${a.id}, needs=${a.needs()}, value=${a.value()}")
    println(s"DEBUG: b.class=${b.getClass}, id=${b
      .asInstanceOf[Dep[_]]
      .id}, needs=${b.asInstanceOf[Dep[_]].needs()}, value=${b.asInstanceOf[Dep[_]].value()}")
    b match {
      case d: Dep[_] => a.getClass == d.getClass && a.id == d.id && a.needs() == d.needs() && a.value() == d.value()
      case _         => false
    }
  }

  behavior of "apply"

  it should "cache the result" in {

    def time(block: => Unit): Long = {
      val start = System.nanoTime()
      block
      val end = System.nanoTime()
      end - start
    }

    val dep = LeafDep[Unit]("dep", () => Set.empty, () => TimeUnit.SECONDS.sleep(1))

    time(dep()) should be > Duration(1, duration.SECONDS).toNanos
    time(dep()) should be < Duration(1, duration.SECONDS).toNanos
  }

  behavior of "map"

  val t: CatalogTransformation = _.put("u")(1)

  it should "create a Dep instance with proper id and needs" in {
    info("map")
    LeafDep("id", () => Set("u"), () => 1).map(_ + 1) should ===(BranchDep("id_M", () => Set("u", "id"), () => 2))
    BranchDep("id", () => Set("u"), () => 1).map(_ + 1) should ===(BranchDep("id_M", () => Set("u"), () => 2))

    info("map + as")
    LeafDep("id", () => Set("u"), () => 1).map(_ + 1).as("t") should ===(LeafDep("t", () => Set("u", "id"), () => 2))
    BranchDep("id", () => Set("u"), () => 1).map(_ + 1).as("t") should ===(LeafDep("t", () => Set("u"), () => 2))

    info("map + map")
    LeafDep("id", () => Set("u"), () => 1).map(_ + 1).map(_ + 1) should ===(
      BranchDep("id_M_M", () => Set("u", "id"), () => 3))
    BranchDep("id", () => Set("u"), () => 1).map(_ + 1).map(_ + 1) should ===(
      BranchDep("id_M_M", () => Set("u"), () => 3))

    info("map + map + as")
    LeafDep("id", () => Set("u"), () => 1).map(_ + 1).map(_ + 1).as("t") should ===(
      LeafDep("t", () => Set("u", "id"), () => 3))
    BranchDep("id", () => Set("u"), () => 1).map(_ + 1).map(_ + 1).as("t") should ===(
      LeafDep("t", () => Set("u"), () => 3))
  }

  behavior of "flatMap"

  it should "create a Dep instance with proper id and needs" in {
    info("flatMap")
    LeafDep("id", () => Set("u"), () => 1).flatMap(i => LeafDep("id2", () => Set("u2"), () => i + 1)) should ===(
      BranchDep("id_FM", () => Set("u", "id", "u2", "id2"), () => 2))
    LeafDep("id", () => Set("u"), () => 1).flatMap(i => BranchDep("id2", () => Set("u2"), () => i + 1)) should ===(
      BranchDep("id_FM", () => Set("u", "id", "u2"), () => 2))

    BranchDep("id", () => Set("u"), () => 1).flatMap(i => LeafDep("id2", () => Set("u2"), () => i + 1)) should ===(
      BranchDep("id_FM", () => Set("u", "u2", "id2"), () => 2))
    BranchDep("id", () => Set("u"), () => 1).flatMap(i => BranchDep("id2", () => Set("u2"), () => i + 1)) should ===(
      BranchDep("id_FM", () => Set("u", "u2"), () => 2))

    info("flatMap + as")
    LeafDep("id", () => Set("u"), () => 1)
      .flatMap(i => LeafDep("id2", () => Set("u2"), () => i + 1))
      .as("t") should ===(LeafDep("t", () => Set("u", "id", "u2", "id2"), () => 2))
    LeafDep("id", () => Set("u"), () => 1)
      .flatMap(i => BranchDep("id2", () => Set("u2"), () => i + 1))
      .as("t") should ===(LeafDep("t", () => Set("u", "id", "u2"), () => 2))

    BranchDep("id", () => Set("u"), () => 1)
      .flatMap(i => LeafDep("id2", () => Set("u2"), () => i + 1))
      .as("t") should ===(LeafDep("t", () => Set("u", "u2", "id2"), () => 2))
    BranchDep("id", () => Set("u"), () => 1)
      .flatMap(i => BranchDep("id2", () => Set("u2"), () => i + 1))
      .as("t") should ===(LeafDep("t", () => Set("u", "u2"), () => 2))

    info("flatMap + flatMap")
    LeafDep("id", () => Set("u"), () => 1)
      .flatMap(i => LeafDep("id2", () => Set("u2"), () => i + 1))
      .flatMap(j => LeafDep("id3", () => Set("u3"), () => j + 1)) should ===(
      BranchDep("id_FM_FM", () => Set("u", "id", "u2", "id2", "u3", "id3"), () => 3))
    LeafDep("id", () => Set("u"), () => 1)
      .flatMap(i => LeafDep("id2", () => Set("u2"), () => i + 1))
      .flatMap(j => BranchDep("id3", () => Set("u3"), () => j + 1)) should ===(
      BranchDep("id_FM_FM", () => Set("u", "id", "u2", "id2", "u3"), () => 3))

    LeafDep("id", () => Set("u"), () => 1)
      .flatMap(i => BranchDep("id2", () => Set("u2"), () => i + 1))
      .flatMap(j => LeafDep("id3", () => Set("u3"), () => j + 1)) should ===(
      BranchDep("id_FM_FM", () => Set("u", "id", "u2", "u3", "id3"), () => 3))
    LeafDep("id", () => Set("u"), () => 1)
      .flatMap(i => BranchDep("id2", () => Set("u2"), () => i + 1))
      .flatMap(j => BranchDep("id3", () => Set("u3"), () => j + 1)) should ===(
      BranchDep("id_FM_FM", () => Set("u", "id", "u2", "u3"), () => 3))

    BranchDep("id", () => Set("u"), () => 1)
      .flatMap(i => LeafDep("id2", () => Set("u2"), () => i + 1))
      .flatMap(j => LeafDep("id3", () => Set("u3"), () => j + 1)) should ===(
      BranchDep("id_FM_FM", () => Set("u", "u2", "id2", "u3", "id3"), () => 3))
    BranchDep("id", () => Set("u"), () => 1)
      .flatMap(i => LeafDep("id2", () => Set("u2"), () => i + 1))
      .flatMap(j => BranchDep("id3", () => Set("u3"), () => j + 1)) should ===(
      BranchDep("id_FM_FM", () => Set("u", "u2", "id2", "u3"), () => 3))

    BranchDep("id", () => Set("u"), () => 1)
      .flatMap(i => BranchDep("id2", () => Set("u2"), () => i + 1))
      .flatMap(j => LeafDep("id3", () => Set("u3"), () => j + 1)) should ===(
      BranchDep("id_FM_FM", () => Set("u", "u2", "u3", "id3"), () => 3))
    BranchDep("id", () => Set("u"), () => 1)
      .flatMap(i => BranchDep("id2", () => Set("u2"), () => i + 1))
      .flatMap(j => BranchDep("id3", () => Set("u3"), () => j + 1)) should ===(
      BranchDep("id_FM_FM", () => Set("u", "u2", "u3"), () => 3))

    info("flatMap + flatMap + as")
    LeafDep("id", () => Set("u"), () => 1)
      .flatMap(i => LeafDep("id2", () => Set("u2"), () => i + 1))
      .flatMap(j => LeafDep("id3", () => Set("u3"), () => j + 1))
      .as("t") should ===(LeafDep("t", () => Set("u", "id", "u2", "id2", "u3", "id3"), () => 3))
    LeafDep("id", () => Set("u"), () => 1)
      .flatMap(i => LeafDep("id2", () => Set("u2"), () => i + 1))
      .flatMap(j => BranchDep("id3", () => Set("u3"), () => j + 1))
      .as("t") should ===(LeafDep("t", () => Set("u", "id", "u2", "id2", "u3"), () => 3))

    LeafDep("id", () => Set("u"), () => 1)
      .flatMap(i => BranchDep("id2", () => Set("u2"), () => i + 1))
      .flatMap(j => LeafDep("id3", () => Set("u3"), () => j + 1))
      .as("t") should ===(LeafDep("t", () => Set("u", "id", "u2", "u3", "id3"), () => 3))
    LeafDep("id", () => Set("u"), () => 1)
      .flatMap(i => BranchDep("id2", () => Set("u2"), () => i + 1))
      .flatMap(j => BranchDep("id3", () => Set("u3"), () => j + 1))
      .as("t") should ===(LeafDep("t", () => Set("u", "id", "u2", "u3"), () => 3))

    BranchDep("id", () => Set("u"), () => 1)
      .flatMap(i => LeafDep("id2", () => Set("u2"), () => i + 1))
      .flatMap(j => LeafDep("id3", () => Set("u3"), () => j + 1))
      .as("t") should ===(LeafDep("t", () => Set("u", "u2", "id2", "u3", "id3"), () => 3))
    BranchDep("id", () => Set("u"), () => 1)
      .flatMap(i => LeafDep("id2", () => Set("u2"), () => i + 1))
      .flatMap(j => BranchDep("id3", () => Set("u3"), () => j + 1))
      .as("t") should ===(LeafDep("t", () => Set("u", "u2", "id2", "u3"), () => 3))

    BranchDep("id", () => Set("u"), () => 1)
      .flatMap(i => BranchDep("id2", () => Set("u2"), () => i + 1))
      .flatMap(j => LeafDep("id3", () => Set("u3"), () => j + 1))
      .as("t") should ===(LeafDep("t", () => Set("u", "u2", "u3", "id3"), () => 3))
    BranchDep("id", () => Set("u"), () => 1)
      .flatMap(i => BranchDep("id2", () => Set("u2"), () => i + 1))
      .flatMap(j => BranchDep("id3", () => Set("u3"), () => j + 1))
      .as("t") should ===(LeafDep("t", () => Set("u", "u2", "u3"), () => 3))

    info("flatMap + map")
    LeafDep("id", () => Set("u"), () => 1)
      .flatMap(i => LeafDep("id2", () => Set("u2"), () => i + 1))
      .map(_ + 1) should ===(BranchDep("id_FM_M", () => Set("u", "id", "u2", "id2"), () => 3))
    LeafDep("id", () => Set("u"), () => 1)
      .flatMap(i => LeafDep("id2", () => Set("u2"), () => i + 1))
      .map(_ + 1) should ===(BranchDep("id_FM_M", () => Set("u", "id", "u2", "id2"), () => 3))

    LeafDep("id", () => Set("u"), () => 1)
      .flatMap(i => BranchDep("id2", () => Set("u2"), () => i + 1))
      .map(_ + 1) should ===(BranchDep("id_FM_M", () => Set("u", "id", "u2"), () => 3))
    LeafDep("id", () => Set("u"), () => 1)
      .flatMap(i => BranchDep("id2", () => Set("u2"), () => i + 1))
      .map(_ + 1) should ===(BranchDep("id_FM_M", () => Set("u", "id", "u2"), () => 3))

    BranchDep("id", () => Set("u"), () => 1)
      .flatMap(i => LeafDep("id2", () => Set("u2"), () => i + 1))
      .map(_ + 1) should ===(BranchDep("id_FM_M", () => Set("u", "u2", "id2"), () => 3))
    BranchDep("id", () => Set("u"), () => 1)
      .flatMap(i => LeafDep("id2", () => Set("u2"), () => i + 1))
      .map(_ + 1) should ===(BranchDep("id_FM_M", () => Set("u", "u2", "id2"), () => 3))

    BranchDep("id", () => Set("u"), () => 1)
      .flatMap(i => BranchDep("id2", () => Set("u2"), () => i + 1))
      .map(_ + 1) should ===(BranchDep("id_FM_M", () => Set("u", "u2"), () => 3))
    BranchDep("id", () => Set("u"), () => 1)
      .flatMap(i => BranchDep("id2", () => Set("u2"), () => i + 1))
      .map(_ + 1) should ===(BranchDep("id_FM_M", () => Set("u", "u2"), () => 3))

    info("flatMap + map + as")
    LeafDep("id", () => Set("u"), () => 1)
      .flatMap(i => LeafDep("id2", () => Set("u2"), () => i + 1))
      .map(_ + 1)
      .as("t") should ===(LeafDep("t", () => Set("u", "id", "u2", "id2"), () => 3))
    LeafDep("id", () => Set("u"), () => 1)
      .flatMap(i => LeafDep("id2", () => Set("u2"), () => i + 1))
      .map(_ + 1)
      .as("t") should ===(LeafDep("t", () => Set("u", "id", "u2", "id2"), () => 3))

    LeafDep("id", () => Set("u"), () => 1)
      .flatMap(i => BranchDep("id2", () => Set("u2"), () => i + 1))
      .map(_ + 1)
      .as("t") should ===(LeafDep("t", () => Set("u", "id", "u2"), () => 3))
    LeafDep("id", () => Set("u"), () => 1)
      .flatMap(i => BranchDep("id2", () => Set("u2"), () => i + 1))
      .map(_ + 1)
      .as("t") should ===(LeafDep("t", () => Set("u", "id", "u2"), () => 3))

    BranchDep("id", () => Set("u"), () => 1)
      .flatMap(i => LeafDep("id2", () => Set("u2"), () => i + 1))
      .map(_ + 1)
      .as("t") should ===(LeafDep("t", () => Set("u", "u2", "id2"), () => 3))
    BranchDep("id", () => Set("u"), () => 1)
      .flatMap(i => LeafDep("id2", () => Set("u2"), () => i + 1))
      .map(_ + 1)
      .as("t") should ===(LeafDep("t", () => Set("u", "u2", "id2"), () => 3))

    BranchDep("id", () => Set("u"), () => 1)
      .flatMap(i => BranchDep("id2", () => Set("u2"), () => i + 1))
      .map(_ + 1)
      .as("t") should ===(LeafDep("t", () => Set("u", "u2"), () => 3))
    BranchDep("id", () => Set("u"), () => 1)
      .flatMap(i => BranchDep("id2", () => Set("u2"), () => i + 1))
      .map(_ + 1)
      .as("t") should ===(LeafDep("t", () => Set("u", "u2"), () => 3))
  }

  behavior of "map2"

  it should "create a Dep instance with proper id and needs" in {
    info("map2")
    LeafDep("id", () => Set("u"), () => 1).map2(LeafDep("id2", () => Set("u2"), () => 2))(_ + _) should ===(
      BranchDep("id_M2", () => Set("u", "id", "u2", "id2"), () => 3))
    LeafDep("id", () => Set("u"), () => 1).map2(BranchDep("id2", () => Set("u2"), () => 2))(_ + _) should ===(
      BranchDep("id_M2", () => Set("u", "id", "u2"), () => 3))

    BranchDep("id", () => Set("u"), () => 1).map2(LeafDep("id2", () => Set("u2"), () => 2))(_ + _) should ===(
      BranchDep("id_M2", () => Set("u", "u2", "id2"), () => 3))
    BranchDep("id", () => Set("u"), () => 1).map2(BranchDep("id2", () => Set("u2"), () => 2))(_ + _) should ===(
      BranchDep("id_M2", () => Set("u", "u2"), () => 3))

    info("map2 + as")
    LeafDep("id", () => Set("u"), () => 1).map2(LeafDep("id2", () => Set("u2"), () => 2))(_ + _).as("t") should ===(
      LeafDep("t", () => Set("u", "id", "u2", "id2"), () => 3))
    LeafDep("id", () => Set("u"), () => 1).map2(BranchDep("id2", () => Set("u2"), () => 2))(_ + _).as("t") should ===(
      LeafDep("t", () => Set("u", "id", "u2"), () => 3))

    BranchDep("id", () => Set("u"), () => 1).map2(LeafDep("id2", () => Set("u2"), () => 2))(_ + _).as("t") should ===(
      LeafDep("t", () => Set("u", "u2", "id2"), () => 3))
    BranchDep("id", () => Set("u"), () => 1).map2(BranchDep("id2", () => Set("u2"), () => 2))(_ + _).as("t") should ===(
      LeafDep("t", () => Set("u", "u2"), () => 3))
  }

}
