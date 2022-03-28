package pl.epsilondeltalimit

import org.apache.spark.sql.SparkSession

object TransformationRunner {
  def main(args: Array[String]): Unit = {
    /*
        withDataframe(<table1 reference>)
          {table1 => //table1 is registered as dependency for the transformation
            withTable(<table2 reference>)
              {table2 => //table2 is registered as dependency for the transformation
                //access to tables here }
                 }

        (<table1 reference>).map{t1DF =>
                withDataframe(<table1 reference>).map{t2DF =>

                  }
                  }
                }
    */


  }
}