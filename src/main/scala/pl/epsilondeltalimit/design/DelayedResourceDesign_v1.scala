package pl.epsilondeltalimit.design

import org.apache.spark.sql.DataFrame

// NOTE: target implementation should be generic, now focusing on dataframes
object DelayedResourceDesign_v1 {

  // represents a resource of type R that is not available now but will be available in the future
  // example: a dataframe that is read from a specific location after other application writes it at the location
  trait DelayedResource[R]

  object DelayedResource {

    // wraps a value of type R into a DelayedResource
    def unit[R](r: R): DelayedResource[R] =
      ???

    // checkouts a delayed resource from global register
    // will need to access register
    def checkout[R](id: String): DelayedResource[R] =
      ???

    // returns the resource, will block until the resource is available
    // must know how to wait for the resource
    def get[R](r: DelayedResource[R]): R =
      ???

    // commits a delayed resource into the register
    // need to access register
    // should probably return the register with committed delayed resource
    def commit[R](r: DelayedResource[R], id: String): Unit =
      ???
  }

  // we want to evaluate an arbitrary processing of two resources that are not yet available
  def process(l: DelayedResource[DataFrame], r: DelayedResource[DataFrame]): DataFrame = {
    val left  = DelayedResource.get(l) // blocking -> wait until l is available
    val right = DelayedResource.get(r) // blocking - wait until r is available

    // left will need to be available before right
    left.unionByName(right)
  }

  // should probably return the register with commited deleyed resource
  def processUsingRegister(lId: String, rId: String): Unit = {
    val left  = DelayedResource.get(DelayedResource.checkout[DataFrame](lId))
    val right = DelayedResource.get(DelayedResource.checkout[DataFrame](rId))

    DelayedResource.commit(DelayedResource.unit(left.unionByName(right)), "r")
  }
}
