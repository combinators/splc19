package gpl.productline

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.body.BodyDeclaration
import com.github.javaparser.ast.expr.Expression
import gpl.domain.{Graph, JavaCode, SemanticTypes}
import org.combinators.cls.interpreter.combinator
import org.combinators.cls.types.Type
import org.combinators.cls.types.syntax._
import org.combinators.templating.twirl.Java

trait WorkspaceDomain extends SemanticTypes {

  val graph:Graph

  /**
    * This is the default for Workspace. We can reach into this class and add additional code
    * fragments into the fundamental methods.
    *
    * Extensions are methods that are not known in advance, and can be added at any time.
    */
  @combinator object workSpaceBase{
    def apply(): CompilationUnit  = {
      Java(
        s"""
           |package gpl;
           |
           |public class WorkSpace {  // supply default empty actions
           |    public void init_vertex( Vertex v ) {}
           |    public void preVisitAction( Vertex v ) {}
           |    public void postVisitAction( Vertex v ) {}
           |    public void nextRegionAction( Vertex v ) {}
           |    public void checkNeighborAction( Vertex vsource, Vertex vtarget ) {}
           |
           |}""".stripMargin).compilationUnit
    }

    val semanticType: Type = workSpaceLogic(workSpaceLogic.base, workSpaceLogic.complete)
  }

  /**
    * Modifies RegionWorkspace
    */
  class RegionWorkSpace{
    def apply(workspaceUnit:CompilationUnit) : CompilationUnit = {
      val clazz = workspaceUnit.getType(0)

      // add field: defaults to zero
      clazz.addFieldWithInitializer(Java("int").tpe(), "counter", Java("0").expression[Expression]())

      val initMethods = clazz.getMethodsBySignature("init_vertex", "Vertex")
      initMethods.forEach (method => JavaCode.appendStatements(method,
        Java("v.componentNumber = -1;").statements))

      val postMethods = clazz.getMethodsBySignature("postVisitAction", "Vertex")
      postMethods.forEach (method => JavaCode.appendStatements(method,
        Java("v.componentNumber = counter;").statements))

      val nextRegionMethods = clazz.getMethodsBySignature("nextRegionAction", "Vertex")
      nextRegionMethods.forEach (method => JavaCode.appendStatements(method,
        Java("counter++;").statements))

      workspaceUnit
//
//      Java(
//        s"""
//           |
//           |    int counter;
//           |
//           |    public RegionWorkSpace( )
//           |    {
//           |        counter = 0;
//           |    }
//           |
//           |    public void init_vertex( Vertex v )
//           |    {
//           |        v.componentNumber = -1;
//           |    }
//           |
//           |    public void postVisitAction( Vertex v )
//           |    {
//           |        v.componentNumber = counter;
//           |    }
//           |
//           |    public void nextRegionAction( Vertex v )
//           |    {
//           |        counter ++;
//           |
//           |}""".stripMargin).classBodyDeclarations()
    }

    val semanticType: Type = workSpaceLogic(workSpaceLogic.base, workSpaceLogic.complete) =>:
      workSpaceLogic(workSpaceLogic.base, workSpaceExtension.region)
  }

  class NumberWorkSpace{
    def apply() : Seq[BodyDeclaration[_]] = {
      Java(
        s"""
           |
           |    int vertexCounter;
           |
           |    public NumberWorkSpace( )
           |    {
           |        vertexCounter = 0;
           |    }
           |
           |    public void preVisitAction( Vertex v )
           |    {
           |        // This assigns the values on the way in
           |        if ( v.visited != true )
           |        {
           |            v.VertexNumber = vertexCounter++;
           |        }
           |    }
           |""".stripMargin).classBodyDeclarations()
    }

    val semanticType: Type = workSpaceLogic(workSpaceLogic.base, workSpaceLogic.var_num)
  }

  class FinishTimeWorkSpace{
    def apply() : Seq[BodyDeclaration[_]] = {
      Java(
        s"""
           |
           |    int FinishCounter;
           |
           |    public FinishTimeWorkSpace() {
           |        FinishCounter = 1;
           |    }
           |
           |    public void preVisitAction( Vertex v )
           |      {
           |        if ( v.visited!=true )
           |            FinishCounter++;
           |    }
           |
           |    public void postVisitAction( Vertex v ) {
           |        v.finishTime = FinishCounter++;
           |    } // of postVisit
           |
           |""".stripMargin).classBodyDeclarations()
    }

    val semanticType: Type = workSpaceLogic(workSpaceLogic.base, workSpaceLogic.var_ft)
  }

  class WorkSpaceTranspose{
    def apply() : Seq[BodyDeclaration[_]] = {
      Java(
        s"""
           |
           |    // Strongly Connected Component Counter
           |    int SCCCounter;
           |
           |    public WorkSpaceTranspose()
           |	{
           |        SCCCounter = 0;
           |    }
           |
           |    public void preVisitAction( Vertex v )
           |    {
           |        if ( v.visited!=true )
           |          {
           |            v.strongComponentNumber = SCCCounter;
           |        }
           |        ;
           |    }
           |
           |    public void nextRegionAction( Vertex v )
           |    {
           |        SCCCounter++;
           |    }
           |
           |""".stripMargin).classBodyDeclarations()
    }

    val semanticType: Type = workSpaceLogic(workSpaceLogic.base, workSpaceLogic.var_trans)
  }

}
