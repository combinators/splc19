package gpl.domain

import com.github.javaparser.ast.CompilationUnit
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
    def apply (part:Type): Constructor = 'GraphLogic (part)

    val extensions:Type = 'Extensions
    val implements:Type = 'Implements
    val base:Type = 'Base
    val prim:Type='Prim
    val kruskal:Type='Kruskal
    val connected:Type='Connected
    val number:Type = 'Number
    val stronglyC:Type='StronglyC
    val directed:Type='Directed
    val undirected: Type='Undirected
    val searchCommon:Type='SearchCommon
    val complete:Type = 'Complete
    val cycle: Type='Cycle


    // features
    val empty : Type = 'Empty
  }

  object workSpaceLogic {
    def apply (part:Type, features:Type): Constructor = 'WorkSpaceLogic (part, features)

    val extensions:Type = 'Extensions
    val implements:Type = 'Implements
    val base:Type = 'Base
    val complete:Type = 'Complete
    val var_region='Region
    val var_num='Number
    val var_ft='FinishTime
    val var_trans='Transpose
    val var_cyc='Cycle
    // features
    val empty : Type = 'Empty
  }

  // Simpler
  object workSpaceExtension {
    def apply (part:Type): Constructor = 'WorkSpaceExtension(part)

    val region = 'Region
    val cycle='Cycle
  }

  object cycleWorkSpaceLogic {
    def apply (part:Type, features:Type): Constructor = 'CycleWorkSpaceLogic (part, features)

    val extensions:Type = 'Extensions
    val implements:Type = 'Implements
    val base:Type = 'Base
    val complete:Type = 'Complete
    // features
    val empty : Type = 'Empty
  }

  object regionWorkSpaceLogic {
    def apply (part:Type, features:Type): Constructor = 'RegionWorkSpaceLogic (part, features)

    val extensions:Type = 'Extensions
    val implements:Type = 'Implements
    val base:Type = 'Base
    val complete:Type = 'Complete
    // features
    val empty : Type = 'Empty
  }

  object numWorkSpaceLogic {
    def apply (part:Type, features:Type): Constructor = 'NumWorkSpaceLogic (part, features)

    val extensions:Type = 'Extensions
    val implements:Type = 'Implements
    val base:Type = 'Base
    val complete:Type = 'Complete
    // features
    val empty : Type = 'Empty
  }

  object ftWorkSpaceLogic {
    def apply (part:Type, features:Type): Constructor = 'FtWorkSpaceLogic (part, features)

    val extensions:Type = 'Extensions
    val implements:Type = 'Implements
    val base:Type = 'Base
    val complete:Type = 'Complete
    // features
    val empty : Type = 'Empty
  }

  object WorkSpaceTpLogic {
    def apply (part:Type, features:Type): Constructor = 'WorkSpaceTpLogic (part, features)

    val extensions:Type = 'Extensions
    val implements:Type = 'Implements
    val base:Type = 'Base
    val complete:Type = 'Complete
    // features
    val empty : Type = 'Empty
  }

  object generator {
    def apply (part:Type): Constructor = 'Generator(part)

    val complete:Type = 'Complete
  }

  // Simpler
  object vertexExtension {
    def apply (part:Type): Constructor = 'VertexExtension(part)

    val search = 'Search
    val connected = 'Connected
  }

  // parts of the widgets during move : Dynamic Behavior
  object vertexLogic {
    def apply (part:Type): Constructor = 'VertexLogic (part)

    // always start with BASE
    val base:Type = 'Base
    val empty : Type = 'Empty
    val complete : Type = 'Complete

    val extensions:Type = 'Extensions

    // known variations for storage of neighbor information
    val var_predkey = 'PredKey
    val var_neighborList = 'NeighborList
    val var_edgeList = 'EdgeList
    val search='Search
    val number ='Number
    val var_dfs='DFS
    val var_cyc='Cycle
    val var_stronglyC='StronglyC
    val connected = 'Connected
    val var_colored = 'Colored
    val var_weighted = 'Weighted
    val implements:Type = 'Implements

  }

  // parts of the widgets during move : Dynamic Behavior
  object edgeLogic {
    def apply (part:Type, features:Type): Constructor = 'EdgeLogic (part, features)

    val extensions:Type = 'Extensions
    val implements:Type = 'Implements

    // known variations
    val var_weighted:Type = 'Weighted

    val base:Type = 'Base
    val empty : Type = 'Empty
    val complete : Type = 'Complete
  }

  object vertexIterLogic{
    def apply (part:Type): Constructor  = 'VertexIterLogic (part)
    val extensions:Type = 'Extensions
    val implements:Type = 'Implements
    val base:Type = 'Base
    val empty : Type = 'Empty
    val complete : Type = 'Complete
  }

  object edgeIfcLogic{
    def apply (part:Type, features:Type): Constructor = 'EdgeIfc (part, features)
    val extensions:Type = 'Extensions
    val implements:Type = 'Implements
    val base:Type = 'Base
    val empty : Type = 'Empty
    val complete : Type = 'Complete
  }

  object edgeIterLogic{
    def apply (part:Type, features:Type): Constructor = 'EdgeIter (part, features)
    val extensions:Type = 'Extensions
    val implements:Type = 'Implements
    val base:Type = 'Base
    val empty : Type = 'Empty
    val complete : Type = 'Complete
  }

  object neighborIfcLogic{
    def apply (part:Type, features:Type): Constructor = 'neighborIfc (part, features)
    val extensions:Type = 'Extensions
    val implements:Type = 'Implements
    val base:Type = 'Base
    val empty : Type = 'Empty
    val complete : Type = 'Complete
  }

  object neighborLogic{
    def apply (part:Type, features:Type): Constructor = 'neighbor (part, features)
    val extensions:Type = 'Extensions
    val implements:Type = 'Implements
    val base:Type = 'Base
    val empty : Type = 'Empty
    val complete : Type = 'Complete
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

  val vertexCNSemantics= new VertexExtension("VertexCN")

  val vertexCCSemantics= new VertexExtension("VertexCC")


  object vertexNodeSearchSemantics{
    def apply (part:Type): Constructor = 'VertexNodeSearch (part)
    val bfs : Type = 'BFS
    val dfs : Type = 'DFS
    val extensions:Type = 'Extensions
    val base:Type = 'Base
  }

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

  /**
    * Extend the apply() method of this class to properly include the desired changes to a CompilationUnit
    *
    * Consider fixing to resolve as decorator rather than using CLS. decorator approach with code generation
    *
    * Using CLS works when components may have other components on which they depend, and use
    * CLS to detect
    *
    * Most commonly related to DeltaJ and possibly Batory X11
    *
    * @param incoming
    * @param outgoing
    */
  abstract class UnitModifier(incoming:Type, outgoing:Type) {
    def apply(unit:CompilationUnit) : CompilationUnit = {
      modify(unit)
      unit
    }

    /**
      * Every subclass knows how to modify the unit.
      *
      * Pass in a UNIT so we can update the imports clauses and perhaps add other inner classes as well
      */
    def modify(unit:CompilationUnit) : Unit

    val semanticType: Type = incoming =>: outgoing
  }

}