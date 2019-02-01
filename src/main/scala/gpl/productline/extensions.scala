package gpl.productline

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.body.BodyDeclaration
import com.github.javaparser.ast.expr.{Name, SimpleName}
import gpl.domain.{Base, Graph, GraphDomain, SemanticTypes}
import org.combinators.cls.interpreter.{ReflectedRepository, combinator}
import org.combinators.cls.types.{Arrow, Constructor, Type}
import org.combinators.templating.twirl.Java
import org.combinators.cls.types.syntax._

/**
  * Based upon the information stored in the model, the init() method adds
  * combinators into the repository, customzied based on the attributes in the
  * Scala model.
  */
trait extensions extends GraphDomain with VertexDomain with EdgeDomain with Base with SemanticTypes with GraphStructureDomain {

  // dynamic combinators added as needed in this trait
  override def init[G <: GraphDomain](gamma: ReflectedRepository[G], g: Graph):
  ReflectedRepository[G] = {
    var updated = super.init(gamma, g)
    println(">>> GPL  dynamic combinators.")

    // from a specification, this goes and adds into the repository the combinators
    // that are necessary. It builds up dynamic combinator fragments as needed.
    // VERTEX extensions
    var vertexExtensions:Seq[Symbol] = Seq.empty
    g.edgeStorage match {
      case gpl.domain.NeighboringNodes() =>
        updated = updated.addCombinator (new VertexNeighborList())
        vertexExtensions = vertexExtensions :+ vertexLogic.var_neighborList
      case gpl.domain.EdgeInstances() =>

      case _ =>
        // any defaults would go here.
    }

    // VERTEX extensions
    if (g.colored) {
      updated = updated.addCombinator (new ColoredVertex())
      vertexExtensions = vertexExtensions :+ vertexLogic.var_colored
    }

    // AT THIS POINT I have all Vertex extensions KNOWN then can CHAIN TOGETHER
    // the different Seq[BodyDeclaration[_]] from the vertexExtensions
    updated = updated.addCombinator (new VertexChained2(vertexExtensions(0), vertexExtensions(1)))

    // might not be useful in long run..
    //val cons = new VertexExtension("DirectedGRSemantics").implements
    // EDGE EXTENSIONS
    if (g.directed) {
      updated = updated.addCombinator(new DirectedGR())
    }

    // EDGE extensions
    if (g.weighted){
     // updated= updated.addCombinator(new WeightedGR())
      // updated = updated.addCombinator(new EdgeWeighted())
      // HACK top get to work
      updated = updated.addCombinator (new NoEdgeExtensions())
    } else {
      updated = updated.addCombinator (new NoEdgeExtensions())
    }

    // eventually need to choose based upon chosen algorithms
    updated= updated.addCombinator(new MSTPrim)

    g.edgeStorage match {
      case gpl.domain.EdgeInstances () =>
        // add combinators
        updated = updated.addCombinator(new GraphExtension1)
        updated = updated.addCombinator(new GraphExtension3)

//        val options = Seq(graphLogic(graphLogic.base, 'primImplementation),
//          graphLogic(graphLogic.base, 'Extension3))
//
//        // note: SEQ : _ * turns a sequence into a variable arguments list IN A FUNCTION CALL
//        updated = updated.addCombinator (
//          new Chained (graphLogic(graphLogic.base, graphLogic.extensions), options : _ *)
//        )


        updated = updated.addCombinator (new TwoGraph(
          'primImplementation,
          graphLogic(graphLogic.base, 'Extension3)
        ))
//        updated = updated.addCombinator(new ChainedGraph(
//          graphLogic(graphLogic.base, 'Extension1),
//          graphLogic(graphLogic.base, 'Extension3)
//        ))

      case gpl.domain.NeighboringNodes() => {
        updated = updated.addCombinator(new GraphExtension3)
        updated = updated.addCombinator(new GraphExtension5)

        updated = updated.addCombinator (new Chained (graphLogic(graphLogic.base, graphLogic.extensions),
          graphLogic(graphLogic.base, 'Extension3),
          graphLogic(graphLogic.base, 'Extension5)))
//        updated = updated.addCombinator (new TwoGraph(
//          graphLogic(graphLogic.base, 'Extension3),
//          graphLogic(graphLogic.base, 'Extension5)
//        ))
//        updated = updated.addCombinator(new ChainedGraph(
//          graphLogic(graphLogic.base, 'Extension3),
//          graphLogic(graphLogic.base, 'Extension5)
//        ))
      }
//      case gpl.domain.EdgeInstances() => {
//        updated = updated.addCombinator(new GraphExtension3)
//        updated = updated.addCombinator(new GraphExtension5)
//
//        updated = updated.addCombinator (new TwoGraph(
//          graphLogic(graphLogic.base, 'Extension3),
//          graphLogic(graphLogic.base, 'Extension7)
//        ))
//      }
    }

    var body: Seq[BodyDeclaration[_]] = Seq.empty

    val thisBody = refine1() ++ refine2()
    val specialImplements = Seq(Java("Special").simpleName(), Java("Another").simpleName())

    updated = updated
      .addCombinator(new TinyClass(thisBody, specialImplements))


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
      .addCombinator(new ChainedVertex(vertexDirectedGRSemantics(vertexDirectedGRSemantics.extensions),
        vertexNodeSearchSemantics(vertexNodeSearchSemantics.extensions),
        vertexCNSemantics(vertexCNSemantics.extensions),
        vertexWGRSemantics(vertexWGRSemantics.extensions),
        vertexCCSemantics(vertexCCSemantics.extensions)))
      .addCombinator(new ChainedWorkSpace(
        workSpaceCNSemantics(workSpaceCNSemantics.extensions),
        workSpaceCCSemantics(workSpaceCCSemantics.extensions)))
//      .addCombinator(new ChainedGraph(graphCCSemantics(graphCCSemantics.extensions),
//        graphCNSemantics(graphCNSemantics.extensions), graphDGRSemantics(graphDGRSemantics.extensions),
//        graphBMSemantics(graphBMSemantics.extensions), graphProgSemantics(graphProgSemantics.extensions),
//        graphWGRSemantics(graphWGRSemantics.extensions),
//        graphDCSemantics(graphDCSemantics.extensions)))

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

  class TinyClass(extensions:Seq[BodyDeclaration[_]], implements:Seq[SimpleName] = Seq.empty) {
    def apply(): CompilationUnit = {
      val implementsClause = if (implements.nonEmpty) {
        "implements " + implements.mkString(",")
      } else {
        ""
      }

      Java(s"""
              |class Tiny $implementsClause {
              |  ${extensions.mkString("\n")}
              |}
          """.stripMargin).compilationUnit
    }

    val semanticType: Type = tinySemantics(tinySemantics.base)
  }


  def refine1(): Seq[BodyDeclaration[_]] = {
    Java(
      s"""
         |int m1() {
         |  System.out.println ("in m1-refine1");
         |  return 5;
         |}
         """.stripMargin).
      classBodyDeclarations()
  }


  def refine2(): Seq[BodyDeclaration[_]] = {
    Java(
      s"""
         |int m1() {
         |  System.out.println ("in m1-refine2");
         |  return 7;
         |}
         """.stripMargin).
      classBodyDeclarations()
  }

  def refine3(): Seq[BodyDeclaration[_]] = {
    Java(
      s"""
         |int m2() {
         |  System.out.println ("in m2-refine13");
         |  return 5;
         |}
         """.stripMargin).
      classBodyDeclarations()
  }

  def addToClass(clazz:CompilationUnit, name:String, extra:BodyDeclaration[_]) : Unit = {
    val method = clazz.getType(0).getMethodsByName(name).get(0)

    val oldBody = method.getBody.get

//    method.setBody(Java(
//      s"""
//         |{
//         |
//         |  ${oldBody.toString}
//         |}
//       """.stripMargin))
  }

/*
  class ChainedVertex(cons1: Type, cons2: Type, cons3:Type, cons4:Type) {
    def apply(one:Seq[BodyDeclaration[_]], two:Seq[BodyDeclaration[_]], three:Seq[BodyDeclaration[_]],
              four:Seq[BodyDeclaration[_]]   ) :
       Seq[BodyDeclaration[_]] = one ++ two ++three++four

    val semanticType:Type = cons1 =>: cons2 =>: cons3 =>:cons4 =>:
      vertexSemantics(vertexSemantics.extensions)
  }
  */

class VertexMethods(bd:Seq[BodyDeclaration[_]]) {
  def apply(): Seq[BodyDeclaration[_]] = bd

  val semanticType: Type = vertexSemantics(vertexSemantics.extensions)
}


  class ChainedVertex(cons: Type*) {
    def apply(bd:Seq[BodyDeclaration[_]]*) : Seq[BodyDeclaration[_]] =
      bd.foldRight(Seq.empty.asInstanceOf[Seq[BodyDeclaration[_]]])(_ ++ _)

    val semanticType:Type = cons.foldRight(vertexSemantics(vertexSemantics.extensions).asInstanceOf[Type])((a,b) => Arrow(a,b))
  }

  /**
    * Given ANY number of parameters of Types A, B, C , ...which means A =>: B =>: C =>: ...
    * AND all parameters to apply() MUST BE OF THE SAME TYPE in this case Seq[ BodyDeclaration[_] ]
    * @param cons
    */
  class ChainedGraph(cons: Type*) {
    def apply(bd:Seq[BodyDeclaration[_]]*) : Seq[BodyDeclaration[_]] =
      bd.foldRight(Seq.empty.asInstanceOf[Seq[BodyDeclaration[_]]])(_ ++ _)

    val semanticType:Type = cons.foldRight(graphSemantics(graphSemantics.extensions).asInstanceOf[Type])((a,b) => Arrow(a,b))
  }
/*
  class ChainedGraphOLD(cons1: Type, cons2: Type, cons3:Type, cons4:Type,cons5:Type) {
    def apply(one:Seq[BodyDeclaration[_]], two:Seq[BodyDeclaration[_]], three:Seq[BodyDeclaration[_]],
              four:Seq[BodyDeclaration[_]],five: Seq[BodyDeclaration[_]]   ) :
    Seq[BodyDeclaration[_]] = one ++ two ++three++four++five

    val semanticType:Type = cons1 =>: cons2 =>: cons3 =>:cons4 =>:cons5=>:
      graphSemantics(graphSemantics.extensions)
  }
  */
/*
  class ChainedWorkSpace(cons1: Type, cons2: Type) {
    def apply(one:Seq[BodyDeclaration[_]], two:Seq[BodyDeclaration[_]]) :
    Seq[BodyDeclaration[_]] = one ++ two

    val semanticType:Type = cons1 =>: cons2 =>:
      workSpaceSemantics(workSpaceSemantics.extensions)
  }
  */

  class ChainedWorkSpace(cons: Type*) {
    def apply(bd:Seq[BodyDeclaration[_]]*) : Seq[BodyDeclaration[_]] =
      bd.foldRight(Seq.empty.asInstanceOf[Seq[BodyDeclaration[_]]])(_ ++ _)

    val semanticType:Type = cons.foldRight(workSpaceSemantics(workSpaceSemantics.extensions).asInstanceOf[Type])((a,b) => Arrow(a,b))
  }

  /** Known that the directedGR feature changes vertex to add these implementations. */
  // intermediate structure. IDEALLY one would just compute this on the fly based on the model.
  class DirectedGR {
    def apply() : Seq[Name] = Seq(Java("EdgeIfc").name, Java("NeighborIfc").name)
    val semanticType:Type = vertexDirectedGRSemantics(vertexDirectedGRSemantics.implements)

  }

  class WeightedGR {
    def apply() : Seq[Name] = Seq(Java("EdgeIfc").name, Java("NeighborIfc").name)
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

  class GraphExtension1 {
    def apply(): Seq[BodyDeclaration[_]] = {
      Java(
        s"""
           |public void method1() {
           |  System.out.println ("meth1");
           |}
           |
           |public void method2() {
           |   System.out.println ("meth2");
           |}
         """.stripMargin).classBodyDeclarations()
    }

    val semanticType: Type = graphLogic(graphLogic.base, 'Extension1)
  }

  class GraphExtension5 {
    def apply(): Seq[BodyDeclaration[_]] = {
      Java(
        s"""
           |public void method5() {
           |  System.out.println ("meth5");
           |}
           |
           |public void method6() {
           |   System.out.println ("meth6");
           |}
         """.stripMargin).classBodyDeclarations()
    }

    val semanticType: Type = graphLogic(graphLogic.base, 'Extension5)
  }


  class GraphExtension3 {
    def apply(): Seq[BodyDeclaration[_]] = {
      Java(
        s"""
           |public void method3() {
           |  System.out.println ("meth3");
           |}
           |
           |public void method4() {
           |   System.out.println ("meth4");
           |}
         """.stripMargin).classBodyDeclarations()
    }

    val semanticType: Type = graphLogic(graphLogic.base, 'Extension3)
  }

  class GraphExtension7 {
    def apply(): Seq[BodyDeclaration[_]] = {
      Java(
        s"""
           |public void method3() {
           |  System.out.println ("meth3");
           |}
           |
           |public void method4() {
           |   System.out.println ("meth4");
           |}
         """.stripMargin).classBodyDeclarations()
    }

    val semanticType: Type = graphLogic(graphLogic.base, 'Extension7)
  }
  class MSTPrim {
    def apply(): Seq[BodyDeclaration[_]] = {
      Java(
        s"""
           |public void prim() {
           |  System.out.println ("prim");
           |}
         """.stripMargin).classBodyDeclarations()
    }

    val semanticType: Type = graphLogic(graphLogic.base, 'Extension7)
  }

  class MSTKruskal {
    def apply(): Seq[BodyDeclaration[_]] = {
      Java(
        s"""
           |public void kruskal() {
           |  System.out.println ("kruskal");
           |}

         """.stripMargin).classBodyDeclarations()
    }

    val semanticType: Type = graphLogic(graphLogic.base, 'Kruskal)
  }

  class TwoGraph(t1: Type, t2: Type) {
    def apply(bd1:Seq[BodyDeclaration[_]], bd2:Seq[BodyDeclaration[_]]) : Seq[BodyDeclaration[_]] =
      bd1 ++ bd2

    val semanticType:Type = t1 =>: t2 =>: graphLogic(graphLogic.base, graphLogic.extensions)
  }

}
