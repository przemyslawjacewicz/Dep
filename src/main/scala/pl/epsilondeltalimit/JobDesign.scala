package pl.epsilondeltalimit

import org.apache.spark.sql.SparkSession

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

//todo: customize execution context
object JobDesign {

  type Job[A] = SparkSession => A

  def async[A](spark: SparkSession)(job: Job[A]): Future[A] = Future(job(spark))

}
