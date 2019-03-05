package gpl.productline

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.body.BodyDeclaration
import gpl.domain.{Graph, SemanticTypes}
import org.combinators.cls.interpreter.combinator
import org.combinators.cls.types.Type
import org.combinators.cls.types.syntax._
import org.combinators.templating.twirl.Java

trait WorkspaceDomain extends SemanticTypes {

  val graph:Graph

  @combinator object workSpaceBase{
    def apply(): CompilationUnit = {
      Java(
        s"""
           |package gpl;
           |
           |import java.util.*;
           |public class WorkSpace
           |{ // supply default empty actions
           |    public void init_vertex( Vertex v ) {}
           |    public void preVisitAction( Vertex v ) {}
           |    public void postVisitAction( Vertex v ) {}
           |    public void nextRegionAction( Vertex v ) {}
           |    public void checkNeighborAction( Vertex vsource, Vertex vtarget ) {}
           |
           |
           |}""".stripMargin).compilationUnit
    }

    val semanticType: Type = workSpaceLogic(workSpaceLogic.base, workSpaceLogic.complete)
  }

  @combinator object RegionWorkSpace{
    def apply(): CompilationUnit = {
      Java(
        s"""
           |package gpl;
           |
           |import java.util.*;
           |public class RegionWorkSpace extends  WorkSpace
           |{
           |    int counter;
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
           |}""".stripMargin).compilationUnit
    }

    val semanticType: Type = regionWorkSpaceLogic(regionWorkSpaceLogic.base, regionWorkSpaceLogic.complete)
  }


  @combinator object NumberWorkSpace{
    def apply(): CompilationUnit = {
      Java(
        s"""
           |package gpl;
           |
           |import java.util.*;
           |public class NumberWorkSpace extends  WorkSpace
           |{
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
           |}""".stripMargin).compilationUnit
    }

    val semanticType: Type = numWorkSpaceLogic(numWorkSpaceLogic.base, numWorkSpaceLogic.complete)
  }

  @combinator object FinishTimeWorkSpace{
    def apply(): CompilationUnit = {
      Java(
        s"""
           |package gpl;
           |
           |import java.util.*;
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
           |
           |}""".stripMargin).compilationUnit
    }

    val semanticType: Type = ftWorkSpaceLogic(ftWorkSpaceLogic.base, ftWorkSpaceLogic.complete)
  }

  @combinator object WorkSpaceTranspose{
    def apply(): CompilationUnit = {
      Java(
        s"""
           |package gpl;
           |
           |import java.util.*;
           |public class WorkSpaceTranspose extends  WorkSpace {
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
           |    }
           |
           |    public void nextRegionAction( Vertex v )
           |    {
           |        SCCCounter++;
           |    }
           |
           |}""".stripMargin).compilationUnit
    }

    val semanticType: Type = WorkSpaceTpLogic(WorkSpaceTpLogic.base, WorkSpaceTpLogic.complete)
  }

}
