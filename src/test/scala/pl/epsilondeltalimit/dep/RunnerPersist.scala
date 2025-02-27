package pl.epsilondeltalimit.dep

import pl.epsilondeltalimit.dep.catalog.Catalog
import pl.epsilondeltalimit.dep.catalog.untyped.UntypedCatalog
import pl.epsilondeltalimit.dep.transformation.implicits._

import java.nio.file.{Files, Path}
import scala.io.Source

object RunnerPersist {

  def main(args: Array[String]): Unit = {

    // b
    val b = (c: Catalog) =>
      c.put {
        c.get[String]("a")
          .map(a => a + "b")
          .as("b")
      }

    // a
    val aPath = Files.createTempFile(null, null)
    save(aPath)("a")
    val a = (c: Catalog) => c.ref("a")(aPath)(read)

    // eval
    val catalog: Catalog = (new UntypedCatalog)
      .withTransformations(b, a)

    println(catalog.explain("b"))
    println(catalog.eval[String]("b"))

    val bPath = Files.createTempFile(null, null)
//    catalog.run[String]("b")(save(bPath))
    catalog.run {
      case "b" => b => save(bPath)(b.toString)
      case "a" => _ => {}
    }
  }

  def save(path: Path): String => Unit = content => {
    println(s"save: path=${path.toString}, content=$content")
    Files.write(path, content.getBytes)
  }

  def read(path: Path): String = {
    val b = Source.fromFile(path.toFile)
    val c = b.mkString
    b.close()
    c
  }
}
