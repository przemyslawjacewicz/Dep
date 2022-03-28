package pl.epsilondeltalimit.table

import org.apache.spark.sql.{DataFrame, SparkSession}

case class Table(id: String, location: String) {
  def getDF()(spark: SparkSession): DataFrame = spark.read.load(location)
}
