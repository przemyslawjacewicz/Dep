package pl.epsilondeltalimit

import org.apache.spark.sql.SparkSession

trait SparkSessionProvider {
  implicit lazy val spark: SparkSession = SparkSession.builder
    .appName("Runner")
    .master("local[2]")
    .config("spark.driver.extraJavaOptions", "-Dlog4j.configuration=log4j2.properties")
    .getOrCreate()

}
