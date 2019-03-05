package gpl.domain

import org.combinators.cls.interpreter.ReflectedRepository

/**
  * Every graph must specify information about vertices and edges
  *
  * Each Graph has specific set of capabilities
  */
abstract class Graph(val capabilities:Seq[Algo]) extends Vertex with Edge  {
  def name:String

  // Dispatch to all algorithms the graph to check for validity
  def modelCheck(g:Graph):Boolean = {
    capabilities.forall(alg => alg.valid(g))
  }
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

//abstract class Target extends Graph with Algo {
//
//  // Ensure constructor validates algorithm models by checking
//  if (!modelCheck(this)) {
//    throw new scala.RuntimeException("Model not appropriate for algorithm")
//  }
//
//}

// these are the desired instances in the product line for structure
class FinalConcept(val algos:Seq[Algo], val wt:Boolean, val dir:Boolean, val stor:EdgeStorage) extends Graph(algos)  {
  override val name:String = "DirectedGraph"

  override def weighted: Boolean = wt
  override def directed: Boolean = dir
  override def labeled: Boolean = true
  override def colored: Boolean = false
  override def edgeStorage: EdgeStorage = stor
}


class undirectedPrimNeighborNodes extends Graph(Seq(Prim()))  {
  override val name:String = "undirected StronglyC NeighborNodes"
  override def weighted: Boolean = true
  override def directed: Boolean = false
  override def colored: Boolean = true
  override def labeled: Boolean = true
  override def edgeStorage: EdgeStorage = NeighboringNodes()
}

// all GPL algorithms must extend this trait
abstract class Algo() {

  // every algorithm has a name
  def name:String

  // every algorithm knows whether it is compatible with graph structure
  def valid(g: Graph): Boolean
}

case class Search() extends Algo {
   def name : String = "Search"

  // Search can be done on any graph
  def valid(g: Graph): Boolean = true
}

case class Number() extends Algo {
  def name : String = "Number"

  // Valid can be done on any graph
  def valid(g: Graph): Boolean = true
}

abstract class MST() extends Algo {
  def name:String = "MST"

  // MST requires undirected graph and weighted
  def valid(g: Graph): Boolean = !g.directed && g.weighted
}

case class Prim() extends MST {
  override def name:String = super.name + "PRIM"
}

case class Kruskal() extends MST {
  override def name:String = super.name + "Kruskal"
}

class BFS extends Search {
  override def name:String = super.name + "BFS"
}
class DFS extends Search {
  override def name:String = super.name + "DFS"
}

case class Connected() extends Algo {
  def name:String = "Connected"

  // Can always perform connectivity
  def valid(g: Graph): Boolean = true
}

case class StronglyConnected() extends Algo {
  override def name:String = "StronglyConnected"

  // Must be strongly connected
  override def valid(g: Graph): Boolean = g.directed
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

