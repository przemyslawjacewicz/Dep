package pl.epsilondeltalimit.design

import org.apache.spark.sql.DataFrame

object DelayedResourceDesign_v2 {

  trait DelayedResource[R]

  object DelayedResource {

    // creates a delayed resource from unevaluated r of type R
    def unit[R](r: => R): DelayedResource[R] =
      ???

    // checkouts a delayed resource from global register
    // will need to access register
    def checkout[R](id: String): DelayedResource[R] =
      ???

    def map2[L, R, A](l: DelayedResource[L], r: DelayedResource[R])(f: (L, R) => A): DelayedResource[R] =
      ???

    // returns the resource, will block until the resource is available
    // must know how to wait for the resource
    def run[R](r: DelayedResource[R]): R =
      ???

    // commits a delayed resource into the register
    // need to access register
    // should probably return the register with committed delayed resource
    // add a version with r: R argument ?
    def commit[R](r: DelayedResource[R], id: String): Unit =
      ???
  }

  def process(l: DelayedResource[DataFrame], r: DelayedResource[DataFrame]): DelayedResource[DataFrame] =
    DelayedResource.map2(l, r)((left, right) => left.unionByName(right))

  def processUsingRegister(lId: String, rId: String): Unit = {
    val l = DelayedResource.checkout[DataFrame](lId)
    val r = DelayedResource.checkout[DataFrame](rId)

    DelayedResource.commit(DelayedResource.map2(l, r)((left, right) => left.unionByName(right)), "r")
  }
}
