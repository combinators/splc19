package gpl.productline

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.body.{BodyDeclaration, FieldDeclaration, MethodDeclaration}
import gpl.domain.{Graph, SemanticTypes}
import org.combinators.cls.interpreter.combinator
import org.combinators.cls.types.Type
import org.combinators.cls.types.syntax._
import org.combinators.templating.twirl.Java

trait EdgeDomain extends SemanticTypes {

  val graph:Graph

  @combinator object edgeBase {
    def apply(): CompilationUnit = {
      Java(
        s"""
           |package gpl;
           |import java.util.LinkedList;
           |
           |// *************************************************************************
           |
           |public class Edge extends Neighbor implements EdgeIfc {
           |    private  Vertex start;
           |
           |    public void EdgeConstructor( Vertex the_start,
           |                      Vertex the_end ) {
           |        start = the_start;
           |        end = the_end;
           |    }
           |
           |    public void adjustAdorns( EdgeIfc the_edge ) {}
           |
           |
           |    public Vertex getOtherVertex(Vertex vertex)
           |    {
           |        if(vertex == start)
           |            return end;
           |        else if(vertex == end)
           |            return start;
           |        else
           |            return null;
           |    }
           |
           |    public Vertex getStart()
           |    {
           |        return start;
           |    }
           |
           |    public Vertex getEnd()
           |    {
           |        return end;
           |    }
           |
           |    public void display() {
           |        System.out.println( " start=" + start.getName() + " end=" + end.getName() );
           |    }
           |}
           |
         """.stripMargin).compilationUnit
    }

    val semanticType: Type = edgeLogic(edgeLogic.base, edgeLogic.empty)
  }

  @combinator object weightedEdge {
    def apply(unit:CompilationUnit): CompilationUnit = {
      val extras = Java(
        s"""
           |private int weight;
           |
           |
           |             public void setWeight(int weight)
           |    {
           |        this.weight = weight;
           |    }
           |
           |    public int getWeight()
           |    {
           |        return this.weight;
           |    }
           |
         """.stripMargin).classBodyDeclarations()

      extras.foreach(bd => {
        bd match {
          case fd:FieldDeclaration => unit.getType(0).addMember(fd)
          case md:MethodDeclaration => unit.getType(0).addMember(md)
        }
      })

      unit
    }

    val semanticType: Type = edgeLogic(edgeLogic.base, edgeLogic.empty) =>:
                             edgeLogic(edgeLogic.base, edgeLogic.complete)
  }

// val semanticType: Type = graphLogic(graphLogic.base, graphLogic.extensions) =>:
  //      graphLogic(graphLogic.base, graphLogic.complete)
}
