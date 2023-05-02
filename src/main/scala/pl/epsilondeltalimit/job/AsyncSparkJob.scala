package pl.epsilondeltalimit.job

import org.apache.spark.sql.SparkSession
import pl.epsilondeltalimit.context.AsyncJobContext

import scala.concurrent.ExecutionContext

object AsyncSparkJob {

  type AsyncSparkJob[T <: {val id: String}] = (SparkSession, AsyncJobContext[T]) => AsyncJobContext[T]

  //  def unit(f: Future[DataFrame]): AsyncJob = _ => f

  //  def map2[T](aj1: AsyncJob[T], aj2: AsyncJob[T])(f: (T, T) => T): AsyncJob[T] = spark =>
  //    aj1(spark).flatMap(df1 => aj2(spark).map(df2 => f(df1, df2)))

}
