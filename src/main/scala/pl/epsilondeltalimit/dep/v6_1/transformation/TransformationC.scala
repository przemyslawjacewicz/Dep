package pl.epsilondeltalimit.dep.v6_1.transformation

import org.apache.spark.sql.DataFrame
import pl.epsilondeltalimit.dep.v6_1.{Catalog, Transformation}

object TransformationC extends Transformation[DataFrame]{
  override def apply(catalog: Catalog): Catalog = {

    val xx = catalog.get[DataFrame]("a").map("c"){a =>
      catalog.get[DataFrame]("b").map("c"){b =>
        a.unionByName(b)
      }
    }

    catalog.get[DataFrame]("a").map { a =>
      catalog.get[DataFrame]("b").map { b =>
        a.unionByName(b)
      }
    }

    ???
  }
}
