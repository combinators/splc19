package gpl.productline

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.body.BodyDeclaration
import com.github.javaparser.ast.expr.Name
import gpl.domain._
import org.combinators.cls.interpreter.ReflectedRepository
import org.combinators.cls.types.{Arrow, Constructor, Type}
import org.combinators.templating.twirl.Java
import org.combinators.cls.types.syntax._

/**
  * Based upon the information stored in the model, the init() method adds
  * combinators into the repository, customzied based on the attributes in the
  * Scala model.
  */
trait extensions extends GraphDomain with VertexDomain with EdgeDomain with NeighborDomain with WorkspaceDomain with Base with SemanticTypes with GraphStructureDomain {

  // dynamic combinators added as needed in this trait
  override def init[G <: GraphDomain](gamma: ReflectedRepository[G], g: Graph):
  ReflectedRepository[G] = {
    var updated = super.init(gamma, g)
    println(">>> GPL  dynamic combinators.")

    // Start with Graph class, and mutate/add/remove as we move forward

    // from a specification, this goes and adds into the repository the combinators
    // that are necessary. It builds up dynamic combinator fragments as needed.
    // VERTEX extensions
    //    vertexLogic(vertexLogic.base, TYPE-1)
    var vertexExtensions = Seq(vertexLogic(vertexLogic.base))
    var workSpaceExtensions:Seq[Constructor]=Seq.empty
    var graphExtensions = Seq(graphLogic(graphLogic.base))

    // GRAPH features are processed here.
    // -----------------------------------
    g.storage match {
      case gpl.domain.NeighboringNodes() =>
        updated = updated.addCombinator (new VertexNeighborList(vertexExtensions.last, vertexLogic(vertexLogic.var_neighborList)))
        vertexExtensions = vertexExtensions :+ vertexLogic(vertexLogic.var_neighborList)
      case gpl.domain.EdgeInstances() =>

      case _ =>
        // any defaults would go here.
    }

    if (g.directed) {
      updated = updated.addCombinator(new DirectedGR())
    }

    if (g.weighted){
     // updated= updated.addCombinator(new WeightedGR())
      // updated = updated.addCombinator(new EdgeWeighted())
      // HACK top get to work
      updated = updated.addCombinator (new EdgeWeighted())
      updated = updated.addCombinator (new NeighborWeighted())
    } else {
      updated = updated.addCombinator (new NoEdgeExtensions())
    }

    //updated = updated.addCombinator(new primAlgorithm())
    if (g.capabilities.contains(Prim())) {
      updated = updated.addCombinator(new primAlgorithm())
      updated = updated.addCombinator(new graphChained1(graphLogic(graphLogic.prim)))
      updated = updated.addCombinator (new PredKey(vertexExtensions.last, vertexLogic(vertexLogic.var_predkey)))
      vertexExtensions = vertexExtensions :+ vertexLogic(vertexLogic.var_predkey)
      workSpaceExtensions = workSpaceExtensions:+ workSpaceLogic(workSpaceLogic.base,workSpaceLogic.var_region)
    }

    if (g.capabilities.contains(Kruskal())) {
      updated = updated.addCombinator(new kruskalAlgorithm())
      updated = updated.addCombinator(new graphChained1(graphLogic(graphLogic.kruskal)))
      workSpaceExtensions = workSpaceExtensions:+ workSpaceLogic(workSpaceLogic.base,workSpaceLogic.var_region)
    }

    // EITHER-OR for Connected or StronglyConnected -- surely can't have both

    // need to work on workSpace
    if (g.capabilities.contains(Connected())) {
      updated = updated.addCombinator(new SearchVertex(vertexExtensions.last, vertexLogic(vertexLogic.search)))  // need to have these in place THEN get request
      vertexExtensions = vertexExtensions :+ vertexLogic(vertexLogic.search)
      updated=updated.addCombinator(new searchGraph())
      updated= updated.addCombinator(new connectedGraph())
      updated= updated.addCombinator(new ConnectedVertex(vertexExtensions.last, vertexLogic(vertexLogic.connected)))
      vertexExtensions = vertexExtensions :+ vertexLogic(vertexLogic.connected)
      updated= updated.addCombinator(new RegionWorkSpace())
      workSpaceExtensions= workSpaceExtensions:+ workSpaceLogic(workSpaceLogic.base,workSpaceLogic.var_region)
      updated = updated.addCombinator(new graphChained2('searchCommon, 'connected))
    }

    //directed done, DFS done, transpose done
    // need to work on workSpace
    if (g.capabilities.contains(StronglyConnected())) {
      //updated = updated.addCombinator(new DFSVertex())
      updated = updated.addCombinator(new Transpose())
      updated = updated.addCombinator(new directedCommon())
      updated = updated.addCombinator(new stronglyCGraph())
      updated = updated.addCombinator(new StronglyCVertex(vertexExtensions.last, vertexLogic.var_stronglyC))
      vertexExtensions = vertexExtensions :+ vertexLogic(vertexLogic.var_stronglyC)
      updated = updated.addCombinator(new FinishTimeWorkSpace())
      updated = updated.addCombinator(new WorkSpaceTranspose())
      updated = updated.addCombinator(new graphChained3('transpose,'directed,'stronglyC))
      vertexExtensions = vertexExtensions :+ vertexLogic(vertexLogic.var_dfs)

      workSpaceExtensions= workSpaceExtensions:+ workSpaceLogic(workSpaceLogic.base,workSpaceLogic.var_ft)
      workSpaceExtensions= workSpaceExtensions:+ workSpaceLogic(workSpaceLogic.base,workSpaceLogic.var_trans)
    }

    //search and graphType???
    if (g.capabilities.contains(Number())) {
      updated = updated.addCombinator(new SearchVertex(vertexExtensions.last, vertexLogic(vertexLogic.search)))
      vertexExtensions = vertexExtensions :+ vertexLogic(vertexLogic.search)
      updated=updated.addCombinator(new searchGraph())
      updated=updated.addCombinator(new NumVertex(vertexExtensions.last, vertexLogic(vertexLogic.number)))
      vertexExtensions = vertexExtensions :+ vertexLogic(vertexLogic.number)
      updated=updated.addCombinator(new NumGraph())
      updated=updated.addCombinator(new NumberWorkSpace())
      updated = updated.addCombinator(new graphChained2('number,'searchCommon))
      workSpaceExtensions= workSpaceExtensions:+ workSpaceLogic(workSpaceLogic.base,workSpaceLogic.var_num)
    }

    if (g.capabilities.contains(Kruskal())) {
      updated = updated.addCombinator(new kruskalAlgorithm())
      updated = updated.addCombinator(new graphChained1('kruskalImplementation))
    }

    // shows how you would update based on the semantics from domain
    //updated = updated.addCombinator(new VertexExtension(body))

    // Goal is to create a combinator whose type is vertexSemantics(vertexSemantics.extensions
    // and whose implementation is string "extends X"

    // THIS can all be programmatically driven from some "model" which adds combinators dynamically in direct
    // response to the structure of said model.
    updated = updated
      .addCombinator(new RgWorkSpace())
      .addCombinator(new CCWorkSpace())
      .addCombinator(new VertexImplementations(vertexDirectedGRSemantics(vertexDirectedGRSemantics.implements)))


    // FINAL steps. Connect last of VertexLogic, GraphLogic to the complete

    // BASE     -->     COMPLETE
    //   (Base ->A), (A->B), (B->Complete)
    updated = updated.addCombinator(
      new ChainCompilationUnit(vertexExtensions.last, vertexLogic(vertexLogic.complete)))

    // any changes to the repository are passed back...
    updated
  }

  // inhabitation calls for the class.
  // the combinator is instantiated from this class AND IS GIVEN all body declarations
  class Chained(inner:Type, cons: Type*) {
    def apply(bd:Seq[BodyDeclaration[_]]*) : Seq[BodyDeclaration[_]] =
      bd.foldRight(Seq.empty.asInstanceOf[Seq[BodyDeclaration[_]]])(_ ++ _)

    val semanticType:Type = cons.foldRight(inner)((a,b) => Arrow(a,b))
  }

  /**
    * This combinator connects via inhabitation logic a chain of in-place modifications.
    *
    * @param type1
    * @param type2
    */
  class ChainCompilationUnit(type1:Type, type2:Type) {
    def apply(unit:CompilationUnit) : CompilationUnit = unit
    val semanticType: Type = type1 =>: type2
  }

  /** Known that the directedGR feature changes vertex to add these implementations. */
  // intermediate structure. IDEALLY one would just compute this on the fly based on the model.
  class DirectedGR {
    def apply() : Seq[Name] = Seq(Java("IEdge").name, Java("NeighborIfc").name)
    val semanticType:Type = vertexDirectedGRSemantics(vertexDirectedGRSemantics.implements)
  }

  class WeightedGR {
    def apply() : Seq[Name] = Seq(Java("IEdge").name, Java("NeighborIfc").name)
    val semanticType:Type = vertexWGRSemantics(vertexWGRSemantics.implements)
  }

  class RgWorkSpace{
    def apply() : Seq[Name] = Seq(Java("WorkSpace").name)
    val semanticType:Type = workSpaceCNSemantics(workSpaceCNSemantics.Extends)
  }

  class CCWorkSpace{
    def apply() : Seq[Name] = Seq(Java("WorkSpace").name)
    val semanticType:Type = workSpaceCCSemantics(workSpaceCCSemantics.Extends)
  }

  /** This reflects the lack of any interfaces being implemented by Vertex. */

  class emptyVertexImplements {
    def apply() : String = { "" }
    val semanticType:Type = vertexSemantics(vertexSemantics.implements)
  }
  /** this is about extend */
  class emptyWorkSpaceExtends {
    def apply() : String = { "" }
    val semanticType:Type = workSpaceSemantics(workSpaceSemantics.Extends)
  }

  /** Given a seq of names to be implementations, returns proper Java fragment as string. */
  class VertexImplementations(param:Type) {
    def apply(interfaces:Seq[Name]) : String = {
      s"implements ${interfaces.mkString(",")}"
    }
    val semanticType:Type = param =>: vertexSemantics(vertexSemantics.implements)
  }

  class WorkSpaceExtensions(param:Type) {
    def apply(interfaces:Seq[Name]) : String = {
      s"extends ${interfaces.mkString(",")}"
    }
    val semanticType:Type = param =>: workSpaceSemantics(workSpaceSemantics.Extends)
  }
  /** connected is done here, may need to fix Graph   */

  // do more computations here...

}
