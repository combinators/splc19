package gpl.domain

import org.combinators.cls.types._
import org.combinators.cls.types.syntax._

/**
  * These codify the semantic types used by the Solitaire combinators.
  *
  * For any of these that are ever going to be translated directly into Java Type Names, you must
  * make them Constructor.
  */
trait SemanticTypes {

  lazy val Gtp = Variable("Gtp")
  lazy val Wgt = Variable("Wgt")

  lazy val kindingGtp: Kinding = Kinding(Gtp)
    .addOption('Directed)
    .addOption('Undirected)
        .addOption('Empty)

  lazy val kindingWgt: Kinding = Kinding(Wgt)
    .addOption('Weighted)
    .addOption('UnWeighted)
    .addOption('Empty)


  // parts of the widgets during move : Dynamic Behavior
  object graphLogic {
    def apply (part:Type, features:Type): Constructor = 'GraphLogic (part, features)

    val extensions:Type = 'Extensions
    val implements:Type = 'Implements
    val base:Type = 'Base
    val complete:Type = 'Complete

    // features
    val empty : Type = 'Empty
  }

  // parts of the widgets during move : Dynamic Behavior
  object vertexLogic {
    def apply (part:Type, features:Type): Constructor = 'VertexLogic (part, features)

    val extensions:Type = 'Extensions
    val implements:Type = 'Implements
    val base:Type = 'Base
    val base2:Type = 'Base2

    val empty : Type = 'Empty
  }

  // parts of the widgets during move : Dynamic Behavior
  object edgeLogic {
    def apply (part:Type, features:Type): Constructor = 'EdgeLogic (part, features)

    val extensions:Type = 'Extensions
    val implements:Type = 'Implements
    val base:Type = 'Base
    val base2:Type = 'Base2
    val empty : Type = 'Empty
  }

  // Original

  // parts of the widgets during move : Dynamic Behavior
  object tinySemantics {
    def apply (part:Type): Constructor = 'Tiny (part)

    val extensions:Type = 'Extensions
    val base:Type = 'Base
  }

  // parts of the widgets during move : Dynamic Behavior
  object graphSemantics {
    def apply (part:Type): Constructor = 'Graph (part)

    val extensions:Type = 'Extensions
    val base:Type = 'Base
  }

  class GraphExtension(symName:String) {
    def apply(part: Type): Constructor = Constructor("Graph" + symName, part)

    val implements: Type = 'Implements
    val extensions: Type = 'Extensions
    val base: Type = 'Base
  }

  // each refinement is given its own extension "name"
  val GraphRefine1 = new GraphExtension("Refine1")
  val GraphRefine2 = new GraphExtension("Refine2")
  val GraphRefine3 = new GraphExtension("Refine3")

  /*
  object graphWGRSemantics {
    def apply (part:Type): Constructor = 'Graph (part)

    val extensions:Type = 'Extensions
    val base:Type = 'Base
  }
  */

  /*
  object graphProgSemantics {
    def apply (part:Type): Constructor = 'Graph (part)

    val extensions:Type = 'Extensions
    val base:Type = 'Base
  }

  object graphBMSemantics {   //benchmark
    def apply (part:Type): Constructor = 'Graph (part)

    val extensions:Type = 'Extensions
    val base:Type = 'Base
  }
  */

  /*
  object graphCNSemantics {  //connected
    def apply (part:Type): Constructor = 'Graph (part)

    val extensions:Type = 'Extensions
    val base:Type = 'Base
  }
  */
  /*

  object graphDGRSemantics { //directedGR
    def apply (part:Type): Constructor = 'Graph (part)

    val extensions:Type = 'Extensions
    val base:Type = 'Base
  }
  */
/*
  object graphCCSemantics { //connected
    def apply (part:Type): Constructor = 'Graph (part)

    val extensions:Type = 'Extensions
    val base:Type = 'Base
  }
  */

  /*
  object graphDCSemantics { //connected
    def apply (part:Type): Constructor = 'Graph (part)

    val extensions:Type = 'Extensions
    val base:Type = 'Base
  }
  */
  val graphWGRSemantics= new GraphExtension("graphWGR")
  val graphProgSemantics= new GraphExtension("graphProg")
  val graphBMSemantics= new GraphExtension("graphBM")
  val graphCNSemantics= new GraphExtension("graphCN")
  val graphDGRSemantics= new GraphExtension("graphDGR")
  val graphCCSemantics= new GraphExtension("graphCC")
  val graphDCSemantics= new GraphExtension("graphDC")

  // vertex

  object vertexSemantics{
    def apply (part:Type): Constructor = 'Vertex (part)
    val implements:Type = 'Implements
    val extensions:Type = 'Extensions
    val base:Type = 'Base

  }

  // create new extension ON DEMAND whenever you want
  class VertexExtension(symName:String) {
    def apply (part:Type): Constructor = Constructor("Vertex" + symName, part)
    val implements:Type = 'Implements
    val extensions:Type = 'Extensions
    val base:Type = 'Base

  }

  val vertexWGRSemantics = new VertexExtension("VertexWGR")

  val vertexDirectedGRSemantics= new VertexExtension("VertexDirectedGRS")

  //val vertexNodeSearchSemantics= new VertexExtension("VertexNodeSearch")

  val vertexCNSemantics= new VertexExtension("VertexCN")

  val vertexCCSemantics= new VertexExtension("VertexCC")


  object vertexNodeSearchSemantics{
    def apply (part:Type): Constructor = 'VertexNodeSearch (part)
    val bfs : Type = 'BFS
    val dfs : Type = 'DFS
    val extensions:Type = 'Extensions
    val base:Type = 'Base

  }

  /*
  * *
  object vertexDirectedGRSemantics{
    def apply (part:Type): Constructor = 'VertexNodeDirectedGR (part)
    val implements:Type = 'Implements
    val extensions:Type = 'Extensions
    val base:Type = 'Base

  }
    */

//
//  object vertexWGRSemantics{
//    def apply (part:Type): Constructor = 'VertexWGR (part)
//    val extensions:Type = 'Extensions
//    val base:Type = 'Base
//
//  }


/*
  object vertexCNSemantics{
    def apply (part:Type): Constructor = 'VertexCNSearch (part)
    val extensions:Type = 'Extensions
    val base:Type = 'Base

  }
  */

  /*
  object vertexCCSemantics{
    def apply (part:Type): Constructor = 'VertexCNSearch (part)
    val extensions:Type = 'Extensions
    val base:Type = 'Base

  }
  */

  object vertexIterSemantics{
    def apply (part:Type): Constructor = 'VertexIter (part)
    val extensions:Type = 'Extensions
    val base:Type = 'Base

  }

  object edgeIfcSemantics{
    def apply (part:Type): Constructor = 'EdgeIfc (part)
    val extensions:Type = 'Extensions
    val base:Type = 'Base

  }

  object neighborIfcSemantics{
    def apply (part:Type): Constructor = 'NeighborIfc (part)
    val extensions:Type = 'Extensions
    val base:Type = 'Base

  }


  object edgeIterSemantics{
    def apply (part:Type): Constructor = 'EdgeIter (part)
    val extensions:Type = 'Extensions
    val base:Type = 'Base

  }
  object workSpaceSemantics{
    def apply (part:Type): Constructor = 'WorkSpace (part)
    val Extends:Type = 'Extends
    val extensions:Type = 'Extensions
    val base:Type = 'Base
  }


  class WSExtension(symName:String) {
    def apply (part:Type): Constructor = Constructor("WS" + symName, part)
    val Extends:Type = 'Extends
    val extensions:Type = 'Extensions
    val base:Type = 'Base

  }

  val workSpaceCNSemantics= new WSExtension("WSCN")

  val workSpaceCCSemantics= new WSExtension("WSCC")
  /*
  object workSpaceCNSemantics{
    def apply (part:Type): Constructor = 'WorkSpaceCN (part)
    val Extends:Type = 'Extends
    val extensions:Type = 'Extensions
    val base:Type = 'Base
  }
  */
/*
  object workSpaceCCSemantics{
    def apply (part:Type): Constructor = 'WorkSpaceCN (part)
    val Extends:Type = 'Extends
    val extensions:Type = 'Extensions
    val base:Type = 'Base
  }
  */


}