package gpl.domain

import org.combinators.cls.interpreter.ReflectedRepository

/**
  * Every graph must specify information about vertices and edges
  */
abstract class Graph extends Vertex with Edge {
  val name:String
}

/**
  * Any information about vertices goes here. Right now only labeling is available.
  */
trait Vertex {
  def labeled : Boolean = true
}

// really the same thing as an Enum in Java
sealed trait EdgeStorage

case class AdjacencyMatrix() extends EdgeStorage
case class NeighboringNodes() extends EdgeStorage
case class EdgeInstances() extends EdgeStorage

sealed trait EdgeAlgo

case class primAlg() extends EdgeAlgo
case class kruskalAlg() extends EdgeAlgo

/**
  * Provides 'superclass' concepts for any edge
  */
trait Edge {
  def directed : Boolean = false
  def weighted : Boolean = false
  def edgeAlgo: EdgeAlgo

  // are these edges actually instantiated and stored with the graph, or are they
  // only generated on demand
  def edgeStorage : EdgeStorage
}

// A Marker Interface that suggests there will be weights in an edge
//trait WeightedEdge extends Edge {
//  override def weighted: Boolean = true
//}
//
//trait unWeightedEdge extends Edge {
//  override def weighted: Boolean = false
//}
//
//
//
//trait DirectedEdge extends Edge {
//  override def directed = true
//}
//
//trait unDirectedEdge extends Edge {
//  override def directed = false
//}


// these are the desired instances in the product line for structure
class DirectedWeightedGraphAdjacencyMatrix extends Graph  {
  val name:String = "DirectedGraph"

  override def weighted: Boolean = true
  override def directed: Boolean = true
  override def edgeAlgo: EdgeAlgo= primAlg()
  override def edgeStorage: EdgeStorage = AdjacencyMatrix()
}

class DirectedunWeightedGraphAdjacencyMatrix extends Graph  {
  val name:String = "DirectedGraph"

  override def weighted: Boolean = false
  override def directed: Boolean = true
  override def edgeAlgo: EdgeAlgo= primAlg()
  override def edgeStorage: EdgeStorage = AdjacencyMatrix()
}

class unDirectedunWeightedGraphAdjacencyMatrix extends Graph  {
  val name:String = "DirectedGraph"

  override def weighted: Boolean = false
  override def directed: Boolean = false
  override def edgeAlgo: EdgeAlgo= primAlg()
  override def edgeStorage: EdgeStorage = AdjacencyMatrix()
}

class unDirectedWeightedGraphAdjacencyMatrix extends Graph  {
  val name:String = "DirectedGraph"

  override def weighted: Boolean = true
  override def directed: Boolean = false
  override def edgeAlgo: EdgeAlgo= primAlg()
  override def edgeStorage: EdgeStorage = AdjacencyMatrix()
}

// correct so far?
//
//class UndirectedGraph extends Graph {
//  val name:String = "UndirectedGraph"
//}


// all GPL algorithms must extend this trait

trait Algo {
  def algoName : String
}

trait Search extends Algo {
  def algoName : String = "search"
}

// these are the desired algorithms over the graphs


trait Num extends Algo {
  def algoName : String = "Number"
}
trait Prim extends Algo {
  def algoName : String = "Prim"
}

trait Kruskal extends Algo {
  def algoName : String = "Kruskal"
}


class BFS extends Search
class DFS extends Search


// this is the final specification: NOTE: ONLY ONE ALGO ALLOWED
//class FinalDirectedGraph extends DirectedWeightedGraphAdjacencyMatrix with Prim {

//}

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

