package gpl.domain

import org.combinators.cls.interpreter.ReflectedRepository

/**
  * Every graph must specify information about vertices and edges
  */
abstract class Graph extends Vertex with Edge with Algo {
  def name:String
}

/**
  * Any information about vertices goes here. Right now only labeling is available.
  */
trait Vertex {
  def labeled : Boolean
  def colored : Boolean
}

// really the same thing as an Enum in Java
sealed trait EdgeStorage

case class NeighboringNodes() extends EdgeStorage
case class EdgeInstances() extends EdgeStorage
//
//sealed trait EdgeAlgo
//
//case class primAlg() extends EdgeAlgo
//case class kruskalAlg() extends EdgeAlgo

/**
  * Provides 'superclass' concepts for any edge
  */
trait Edge {
  def directed : Boolean
  def weighted : Boolean
  //def edgeAlgo: EdgeAlgo

  // are these edges actually instantiated and stored with the graph, or are they
  // only generated on demand
  def edgeStorage : EdgeStorage
}



trait UndirectedEdges {
  def directed:Boolean = false
}

trait UncoloredVertex {
  def colored:Boolean = false
}

trait LabeledVertex {
  def labeled:Boolean = true
}

trait NeighborStorage {
  def edgeStorage:EdgeStorage = NeighboringNodes()
}

trait WeightedEdges {
  def weighted:Boolean = true
}

trait UnWeightedEdges {
  def weighted:Boolean = false
}

abstract class Target extends Graph with Algo {

  // Ensure constructor validates algorithm models by checking
  if (!modelCheck(this)) {
    throw new scala.RuntimeException("Model not appropriate for algorithm")
  }

}

// these are the desired instances in the product line for structure
class FinalConcept(val algos:Seq[Algo], val wt:Boolean, val dir:Boolean, val stor:EdgeStorage) extends Graph  {
  override val name:String = "DirectedGraph"

  override def weighted: Boolean = wt
  override def directed: Boolean = dir
  override def labeled: Boolean = true
  override def colored: Boolean = false
  override def edgeStorage: EdgeStorage = stor
}



//// these are the desired instances in the product line for structure
//class DirectedWeightedGraphAdjacencyMatrix extends Graph  {
//  val name:String = "DirectedGraph"
//
//  override def weighted: Boolean = true
//  override def directed: Boolean = false
//  override def edgeStorage: EdgeStorage = NeighboringNodes()
//}

// Choose to store an undirected graph by having each vertex store its
// neighbor nodes; when asking for edges, they are instantiated on the fly
// on demand.
class undirectedNeighborNodes extends Graph  {
  override val name:String = "undirectedNeighborNodes"

  override def weighted: Boolean = true
  override def directed: Boolean = false
  override def colored: Boolean = true
  override def labeled: Boolean = true
  override def edgeStorage: EdgeStorage = NeighboringNodes()
}

// correct so far?
//
//class UndirectedGraph extends Graph {
//  val name:String = "UndirectedGraph"
//}


// all GPL algorithms must extend this trait

trait Algo {
  def name : String = ""

  def capabilities : Seq[String] = Seq.empty

  // by default always checks
  def modelCheck(g:Graph):Boolean = true
}

trait Search extends Algo {
  override def name : String = "Search" + super.name

}

// these are the desired algorithms over the graphs


trait Num extends Algo {
  override def name : String = "Number" + super.name

}

trait MST extends Algo {
  override def capabilities: Seq[String] = super.capabilities :+ "MST"
}

trait Prim extends MST {
  override def name : String = "Prim" + super.name

  override def modelCheck(g:Graph):Boolean = {
    g.weighted && !g.directed && super.modelCheck(g)
  }
}

trait Kruskal extends MST {
  override def name : String = "Kruskal" + super.name

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

