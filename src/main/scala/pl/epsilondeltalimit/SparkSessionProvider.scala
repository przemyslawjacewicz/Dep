package pl.epsilondeltalimit

import org.apache.spark.sql.SparkSession

trait SparkSessionProvider {
  implicit lazy val spark: SparkSession = SparkSession.builder
    .appName("Runner")
    .master("local[2]")
    .config("spark.driver.extraJavaOptions", s"-Dlog4j.configuration=log4j.properties")
    .getOrCreate()

}
