package pl.epsilondeltalimit.dep.alternatives.v2

import org.apache.spark.sql.DataFrame

// big goal: create a structure that will allow to code computations with dependencies between resources used for the
// computation
// implementation:
// - define resources and dependencies
// - dependency resolution should create a graph of resources (DAG) => find a library for that
// - use the graph to run computations by selecting the order of resources to be forced
object Dep {

  trait Catalog

  // represents a lazy resource of type A that is dependent on other resources
  // lazy = not yet evaluated
  // dependencies = other resources that need to be available to evaluate this resource
  // example: a dataframe read from filesystem that is dependent on other dataframes i.e. to get this dataframe we need
  // to join/union between other dataframes that are also read from filesystem
  type Dep[A] = Catalog => A

  object Dep {

    // catalog = a container of resources with dependency information

    // commits a description for a resource without dependencies into the catalog with an id
    // example: commit(spark.read.load("/path/to/dataframe"), "df1")
    // need to access catalog
    // should probably return the new catalog with committed resource
    def commit[A](a: => A, id: String): Unit =
      ???

    def commit[A](a: Dep[A], id: String, deps: String*): Unit =
      ???

    // checkouts a resource from catalog to be used in subsequent calculations
    // can checkout resources not yet committed e.g. order of resource commits does not matter
    // need to access catalog
    // should probably return the catalog also
    def checkout[A](id: String): Dep[A] =
      ???

    // creates a resource of type C that is dependant of dep1 and dep2
    // example: creates a dataframe that needs a dataframe contained in dep1 and dep2 to be available
    // the created Dep instance should mark dep1 and dep2 as its dependencies
    // need to access catalog
    // should probably return the catalog also
    def map2[A, B, C](dep1: Dep[A], dep2: Dep[B])(f: (A, B) => C): Dep[C] =
      ???

    // forces evaluation of all resources, should take care of the dependencies
    def run[A](c: Catalog)(d: Dep[A]): A =
      ???
  }

  def processBlocking(c: Catalog)(id1: String, id2: String): DataFrame = {
    val df1 = Dep.run(c)(Dep.checkout[DataFrame](id1)) // has no dependencies -> can be checked directly
    val df2 = Dep.run(c)(Dep.checkout[DataFrame](id2)) // has no dependencies -> can be checked directly
    df1.unionByName(df2)
  }

  def process(c: Catalog)(id1: String, id2: String): DataFrame = {
    val dep1 = Dep.checkout[DataFrame](id1)
    val dep2 = Dep.checkout[DataFrame](id2)

    val dep = Dep.map2(dep1, dep2)((df1, df2) => df1.unionByName(df2))

    Dep.commit(dep, "dep", id1, id2)

    Dep.run(c)(Dep.checkout("dep"))
  }

}
