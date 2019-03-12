package gpl.productline

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.body.BodyDeclaration
import gpl.domain.{Graph, SemanticTypes}
import org.combinators.cls.interpreter.combinator
import org.combinators.cls.types.Type
import org.combinators.cls.types.syntax._
import org.combinators.templating.twirl.Java

trait EdgeDomain extends SemanticTypes {

  val graph:Graph

  @combinator object FixedWeightedEdgeIfc {
    def apply(): CompilationUnit  =  {
      Java(
        s"""
           |package gpl;
           |
           |public interface IEdge {
           |    public Vertex getStart( );
           |    public Vertex getEnd( );
           |    public void display( );
           |
           |    public Vertex getOtherVertex( Vertex vertex );
           |    public int getWeight();
           |}
         """.stripMargin).compilationUnit
    }

    val semanticType: Type = edgeIfcLogic(edgeIfcLogic.base,edgeIfcLogic.complete )

  }

  @combinator object edgeBase {
    def apply(extensions: Seq[BodyDeclaration[_]]): CompilationUnit = {
      Java(
        s"""
           |package gpl;
           |import java.util.LinkedList;
           |
           |// *************************************************************************
           |
           |public class Edge extends Neighbor {
           |    private  Vertex start;
           |    private  Vertex end;
           |    int source;
           |    int destination;
           |
           |    Edge( Vertex the_start,Vertex the_end ) {
           |        this.start = the_start;
           |        this.end = the_end;
           |    }
           |    public Edge(int source, int destination, int weight) {
           |        this.source = source;
           |        this.destination = destination;
           |        this.weight = weight;
           |    }
           |
           |    public Vertex getOtherVertex(Vertex vertex) {
           |        if (vertex == start)
           |            return end;
           |        else if (vertex == end)
           |            return start;
           |        else
           |            return null;
           |    }
           |
           |    public Vertex getStart() { return start;  }
           |    public Vertex getEnd() { return end; }
           |
           |    // inappropriate to include weight here, since that may never be realized.
           |    public String toString() {
           |      return "(" + start.name + " -> " + end.name + "(w=" + weight + "))";
           |    }
           |
           |    public void display()  {
           |      System.out.println( " start=" + start.name + " end=" + end.name+"weight"+weight );
           |    }
           |   ${extensions.mkString("\n")}
           |}""".stripMargin).compilationUnit
    }

    val semanticType: Type = edgeLogic(edgeLogic.base, edgeLogic.var_weighted) =>:
      edgeLogic(edgeLogic.base, edgeLogic.complete)
  }

  /** This is how you define a combinator that does NOTHING but it resolves a dependency. */
  class NoEdgeExtensions() {
    def apply(): Seq[BodyDeclaration[_]] = Seq.empty
    val semanticType:Type = edgeLogic(edgeLogic.base, edgeLogic.extensions)
  }

  class EdgeWeighted() {
    def apply(): Seq[BodyDeclaration[_]] = {
      Java(
        s"""
           |    private int weight;
           |
           |    Edge (Vertex the_start, Vertex the_end, int the_weight ) {
           |        this.start = the_start;
           |        this.end  = the_end;
           |        this.weight = the_weight;
           |    }
           |
           |    public void setWeight(int weight) {
           |        this.weight = weight;
           |    }
           |
           |    public int getWeight() { return weight; }
         """.stripMargin).classBodyDeclarations()
    }

    val semanticType: Type = edgeLogic(edgeLogic.base, edgeLogic.var_weighted)
  }
}
