package pl.epsilondeltalimit.dep.alternatives.v6.transformation

import org.apache.spark.sql.Row
import org.apache.spark.sql.types.StructType
import pl.epsilondeltalimit.dep.alternatives.v6.{Catalog, Dep, Transformation}

object TransformationE extends Transformation {
  override def apply(catalog: Catalog): Catalog = {
    val a = catalog.get("a")
    val b = catalog.get("b")
    val c = catalog.get("c")
    val d = catalog.get("d")

    val z = spark.createDataFrame(
      spark.sparkContext.emptyRDD[Row],
      StructType.fromDDL("f1 INT, f2 LONG, f3 STRING")
    )
    catalog.put(Dep.mapN("e")(a, b, c, d)(dfs => dfs.fold(z)(_.unionByName(_))))
  }
}
