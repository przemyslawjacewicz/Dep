package pl.epsilondeltalimit.job

import org.apache.spark.sql.SparkSession

import scala.concurrent.Future

//todo: customize execution context
object Job {

  type Job[A] = SparkSession => A

  def unit[A](a: A): Job[A] = _ => a

  //  def async[A](spark: SparkSession)(job: Job[A]): Future[A] = Future(job(spark))

}
