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
  // We can't do this cuz RegionWorkspace is not just adding
//  class RegionWorkSpace{
//    def apply(workspaceUnit:CompilationUnit) : CompilationUnit = {
//      val clazz = workspaceUnit.getType(0)
//
//      // add field: defaults to zero
//      clazz.addFieldWithInitializer(Java("int").tpe(), "counter", Java("0").expression[Expression]())
//
//      val initMethods = clazz.getMethodsBySignature("init_vertex", "Vertex")
//      initMethods.forEach (method => JavaCode.appendStatements(method,
//        Java("v.componentNumber = -1;").statements))
//
//      val postMethods = clazz.getMethodsBySignature("postVisitAction", "Vertex")
//      postMethods.forEach (method => JavaCode.appendStatements(method,
//        Java("v.componentNumber = counter;").statements))
//
//      val nextRegionMethods = clazz.getMethodsBySignature("nextRegionAction", "Vertex")
//      nextRegionMethods.forEach (method => JavaCode.appendStatements(method,
//        Java("counter++;").statements))
//
//      workspaceUnit
////
////      Java(
////        s"""
////           |
////           |    int counter;
////           |
////           |    public RegionWorkSpace( )
////           |    {
////           |        counter = 0;
////           |    }
////           |
////           |    public void init_vertex( Vertex v )
////           |    {
////           |        v.componentNumber = -1;
////           |    }
////           |
////           |    public void postVisitAction( Vertex v )
////           |    {
////           |        v.componentNumber = counter;
////           |    }
////           |
////           |    public void nextRegionAction( Vertex v )
////           |    {
////           |        counter ++;
////           |
////           |}""".stripMargin).classBodyDeclarations()
//    }
//
//    val semanticType: Type = workSpaceLogic(workSpaceLogic.base, workSpaceLogic.complete) =>:
//      workSpaceLogic(workSpaceLogic.base, workSpaceExtension.region)
//  }

  class RegionWorkSpace{
    def apply() : CompilationUnit = {

      Java(
        s"""
           |package gpl;
           |public class RegionWorkSpace extends  WorkSpace{
           |   int counter;
           |
           |    public RegionWorkSpace( )
           |    {
           |        counter = 0;
           |    }
           |
           |    public void init_vertex( Vertex v )
           |    {
           |        v.componentNumber = -1;
           |    }
           |
           |    public void postVisitAction( Vertex v )
           |    {
           |        v.componentNumber = counter;
           |    }
           |
           |    public void nextRegionAction( Vertex v )
           |    {
           |        counter ++;
           |    }
           |    }
           |     // of checkNeighboor
           |""".stripMargin).compilationUnit
    }

    val semanticType: Type = regionWorkSpaceLogic(regionWorkSpaceLogic.base, regionWorkSpaceLogic.complete)
  }


  class CycleWorkSpace{
    def apply() : CompilationUnit = {

      Java(
        s"""
           |package gpl;
           |public class CycleWorkSpace extends  WorkSpace{
           |    public boolean AnyCycles;
           |    public int     counter;
           |    public boolean isDirected;
           |
           |    public static final int WHITE = 0;
           |    public static final int GRAY  = 1;
           |    public static final int BLACK = 2;
           |
           |    public CycleWorkSpace( boolean UnDir ) {
           |        AnyCycles = false;
           |        counter   = 0;
           |        isDirected = UnDir;
           |    }
           |
           |    public void init_vertex( Vertex v )
           |      {
           |       v.VertexCycle = Integer.MAX_VALUE;
           |        v.VertexColor = WHITE; // initialize to white color
           |    }
           |
           |    public void preVisitAction( Vertex v ) {
           |
           |        // This assigns the values on the way in
           |        if ( v.visited!=true )
           |        { // if it has not been visited then set the
           |            // VertexCycle accordingly
           |            v.VertexCycle = counter++;
           |            v.VertexColor = GRAY; // we make the vertex gray
           |        }
           |    }
           |
           |    public void postVisitAction( Vertex v )
           |      {
           |        v.VertexColor = BLACK; // we are done visiting so make it black
           |        counter--;
           |    } // of postVisitAction
           |
           |    public void checkNeighborAction( Vertex vsource,
           |                     Vertex vtarget )
           |      {
           |        // if the graph is directed is enough to check that the source node
           |        // is gray and the adyacent is gray also to find a cycle
           |        // if the graph is undirected we need to check that the adyacent is not
           |        // the father, if it is the father the difference in the VertexCount is
           |        // only one.
           |        if ( isDirected )
           |        {
           |            if ( ( vsource.VertexColor == GRAY ) && ( vtarget.VertexColor == GRAY ) )
           |              {
           |                AnyCycles = true;
           |            }
           |        }
           |        else
           |        { // undirected case
           |            if ( ( vsource.VertexColor == GRAY ) && ( vtarget.VertexColor == GRAY )
           |                 && vsource.VertexCycle != vtarget.VertexCycle+1 )
           |              {
           |                AnyCycles = true;
           |            }
           |        }
           |
           |    }
           |    }
           |     // of checkNeighboor
           |""".stripMargin).compilationUnit
    }

    val semanticType: Type = cycleWorkSpaceLogic(cycleWorkSpaceLogic.base, cycleWorkSpaceLogic.complete)
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
    def apply() : CompilationUnit = {
      Java(
        s"""
           |package gpl;
           |public class FinishTimeWorkSpace extends  WorkSpace {
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
           |}
           |""".stripMargin).compilationUnit
    }

    val semanticType: Type = ftWorkSpaceLogic(ftWorkSpaceLogic.base, ftWorkSpaceLogic.complete)
  }

  class WorkSpaceTranspose{
    def apply() : CompilationUnit = {
      Java(
        s"""
           |package gpl;
           |public class WorkSpaceTranspose extends  WorkSpace {
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
           |}
           |""".stripMargin).compilationUnit
    }

    val semanticType: Type = WorkSpaceTpLogic(WorkSpaceTpLogic.base, WorkSpaceTpLogic.complete)
  }

}
