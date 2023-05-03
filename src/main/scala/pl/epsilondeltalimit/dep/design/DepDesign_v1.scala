package pl.epsilondeltalimit.dep.design

import org.apache.spark.sql.DataFrame

// big goal: implement a structure that will allow to code computations with dependencies
object DepDesign_v1 {

  // represents a resource of type A that is dependent on other resources
  // example: a dataframe that is dependent on other dataframes i.e. to get this dataframe we need to join/union between
  // dependant dataframes
  trait Dep[A]

  object Dep {

    //register = a container of resources with dependency information

    // commits a resource without dependencies into the register with an id
    // need to access register
    // should probably return the register with committed resource
    // should the resource be lazy ?
    def commit[A](a: A, id: String): Unit =
      ???

    // checkouts a resource from register
    // need to access register
    // should probably return the register
    def checkout[A](id: String): A =
      ???
  }

  def process(id1: String, id2: String): Unit = {
    val df1 = Dep.checkout[DataFrame](id1) // blocking -> waits until resource with id1 is available
    val df2 = Dep.checkout[DataFrame](id2) // blocking -> waits until resource with id2 is available

    Dep.commit(df1.unionByName(df2), "r")
  }
}
