package pl.epsilondeltalimit.dep.alternatives.v5

import org.apache.spark.sql.SparkSession

trait Transformation extends (Catalog => Catalog) {

  implicit lazy val spark: SparkSession = SparkSession.builder
    .appName("Runner")
    .master("local[2]")
    .config("spark.driver.extraJavaOptions", "-Dlog4j.configuration=log4j2.properties")
    .getOrCreate()

}
