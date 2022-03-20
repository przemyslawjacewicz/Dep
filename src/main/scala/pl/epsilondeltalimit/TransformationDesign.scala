package pl.epsilondeltalimit

import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark.sql.{DataFrame, SparkSession}

import java.util.concurrent.TimeUnit

object TransformationDesign {

//  type Transformation = SparkSession => DataFrame
//
//  def unit(df: DataFrame): Transformation = _ => df
//
//  //  def provide(id: String): Transformation = spark => ???
//
//  def run =
//
//
//  def getDf(location: String): Transformation = spark => {
//    while (!isReady(location)(spark)) {
//      TimeUnit.SECONDS.wait(5)
//    }
//    load(location)(spark)
//  }
//
//  //todo: check based on location existence, should check based on other condition
//  def isReady(location: String): SparkSession => Boolean = spark => {
//    val fs = FileSystem.get(spark.sparkContext.hadoopConfiguration)
//    fs.exists(new Path(location))
//  }
//
//  def load(location: String): Transformation = spark => spark.read.format("parquet").load(location)

}
