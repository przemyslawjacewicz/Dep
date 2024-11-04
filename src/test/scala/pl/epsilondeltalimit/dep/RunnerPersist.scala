package pl.epsilondeltalimit.dep

import pl.epsilondeltalimit.dep.catalog.Catalog
import pl.epsilondeltalimit.dep.catalog.untyped.UntypedCatalog
import pl.epsilondeltalimit.dep.transformation.CatalogTransformation
import pl.epsilondeltalimit.dep.transformation.implicits._

import java.nio.file.{Files, Path}
import scala.io.Source

object RunnerPersist {

  def main(args: Array[String]): Unit = {

    val b: CatalogTransformation = c =>
      c.put {
        c.get[Path]("a")
          .map { aPath =>
            val a     = read(aPath)
            val bPath = save(a + "b")
            println(s"b: $bPath")
            bPath
          }
          .as("b")
      }
    val a: CatalogTransformation = c =>
      c.put("a") {
        val aPath = save("a")
        println(s"a: $aPath")
        aPath
      }

    val catalog: Catalog = (new UntypedCatalog)
      .withTransformations(b, a)

    println(catalog.explain("b"))
    println(catalog.eval[String]("b"))

  }

  def save(content: String): Path =
    Files.write(Files.createTempFile(null, null), content.getBytes)

  def read(path: Path): String = {
    val b = Source.fromFile(path.toFile)
    val c = b.mkString
    b.close()
    c
  }
}
