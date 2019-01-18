package gpl.domain

import org.combinators.cls.interpreter.ReflectedRepository

/**
  * Every graph must specify its type of edge and type of vertex
  */
abstract class Graph extends Vertex with Edge {
  val name:String
}

trait Vertex {
  def labeled : Boolean = false
}


/**
  * Provides 'superclass' concepts for any edge
  */
trait Edge {
  def directed : Boolean = false
  def weighted : Boolean = false
}

// A Marker Interface that suggests there will be weights in an edge
trait WeightedEdge extends Edge {
  override def weighted: Boolean = true
}

trait unWeightedEdge extends Edge {
  override def weighted: Boolean = false
}



trait DirectedEdge extends Edge {
  override def directed = true
}

trait unDirectedEdge extends Edge {
  override def directed = false
}


// these are the desired instances in the product line for structure
class DirectedGraph extends Graph with DirectedEdge {
  val name:String = "DirectedGraph"
}

class UndirectedGraph extends Graph {
  val name:String = "UndirectedGraph"
}

class DirectedWeightedGraph extends Graph with DirectedEdge with WeightedEdge {
  val name:String = "DirectedWeightedGraph"
}

// all GPL algorithms must extend this trait
trait Algo { }

trait Search extends Algo {

}

// these are the desired algorithms over the graphs


trait Num extends Algo
trait Prim extends Algo
class BFS extends Search
class DFS extends Search
// this is the final specification
class FinalDirectedGraph extends DirectedGraph with Num with Prim {

}

/**

// G <:
object sample {

//  // how to declare a graph with directed edges.

  val myGraph: Model = DirectedGraph(, DirectedEdge.type, "directedEdge")
}
*/

class GraphDomain(val graph:Graph) {
//  // exists so def init methods can be included in any trait
//  @combinator object DefaultGraph{
//
//    val semanticType: Type = graph(.generator)
//  }
}



trait Base {

  /**
    * To be overridden by sub-typed traits that are part of the dynamic constructions process.
    */
  def init[G <: GraphDomain](gamma: ReflectedRepository[G], graph:Graph): ReflectedRepository[G] = gamma
}

