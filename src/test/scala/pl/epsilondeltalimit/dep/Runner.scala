package pl.epsilondeltalimit.dep

import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.{Row, SparkSession}

import java.sql.Timestamp
import java.time.LocalDateTime

object Runner {
  def main(args: Array[String]): Unit = {
    implicit lazy val spark: SparkSession = SparkSession.getActiveSession.getOrElse(
      SparkSession.builder
        .appName("TestSession")
        .master("local[*]")
        .config("spark.executor.cores", "4")
        .config("spark.executor.memory", "4g")
        .config("spark.sql.adaptive.coalescePartitions.minPartitionSize", "1B")
//        .config("spark.sql.extensions", "io.delta.sql.DeltaSparkSessionExtension")
//        .config("spark.sql.catalog.spark_catalog", "org.apache.spark.sql.delta.catalog.DeltaCatalog")
        .config("spark.sql.warehouse.dir", "/tmp/spark-warehouse")
        .config("spark.sql.caseSensitive", "false")
        .config("spark.ui.enabled", "false")
//        .config("spark.databricks.delta.optimizeWrite.enabled", "true")
//        .config("spark.databricks.delta.autoCompact.enabled", "true")
        .config("spark.sql.shuffle.partitions", 1)
        .config("spark.sql.debug.maxToStringFields", 1000)
        .config("spark.sql.parquet.int96RebaseModeInWrite", "CORRECTED")
        .config("spark.sql.autoBroadcastJoinThreshold", "-1")
        .config("spark.sql.adaptive.enabled", "false")
        .getOrCreate()
    )

    spark
      .createDataFrame(
        spark.sparkContext.parallelize(Seq(
          Row("DARVA", Timestamp.valueOf(LocalDateTime.now()), true, null, null),
          Row("DAVINCI", Timestamp.valueOf(LocalDateTime.now()), true, null, null),
          Row("LCBFT", Timestamp.valueOf(LocalDateTime.now()), true, null, null),
          Row("QUALTRICS", Timestamp.valueOf(LocalDateTime.now()), true, null, null),
          Row("SHIFT", Timestamp.valueOf(LocalDateTime.now()), true, null, null)
        )),
        StructType.fromDDL("""
                             |ExtractArea    STRING      NOT NULL,
                             |ExtractDate    TIMESTAMP   NOT NULL,
                             |IsExported     BOOLEAN     ,
                             |IsPurged       BOOLEAN     ,
                             |PurgedDate     TIMESTAMP   
                             |""".stripMargin)
      )
      .toDF()
      .coalesce(1)
      .write
      .format("parquet")
      .save("/tmp/cockpit/ReportingHisto")

  }
}
