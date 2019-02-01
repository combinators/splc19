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


  @combinator object EdgeIter{
    def apply(): CompilationUnit = {
      Java(
        s"""
           |package gpl;
           |public class EdgeIter
           |{
           |    // methods whose bodies will be overridden by subsequent layers
           |    public boolean hasNext( ) { return false; }
           |    public EdgeIfc next( ) { return null; }
           |
           |}
         """.stripMargin).compilationUnit
    }

    val semanticType: Type = edgeIterLogic(edgeIterLogic.base,edgeIterLogic.complete )

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
           |
           |   ${extensions.mkString("\n")}
           |}""".stripMargin).compilationUnit
    }

    val semanticType: Type = edgeLogic(edgeLogic.base, edgeLogic.extensions) =>:
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
           |    public void setWeight(int weight)
           |    {
           |        int x = 10;  // ignore. Just checking
           |        this.weight = weight;
           |    }
           |
           |    public int getWeight()
           |    {
           |        return this.weight;
           |    }
           |
         """.stripMargin).classBodyDeclarations()
    }

    val semanticType: Type = edgeLogic(edgeLogic.base, edgeLogic.var_weighted)
  }

// val semanticType: Type = graphLogic(graphLogic.base, graphLogic.extensions) =>:
  //      graphLogic(graphLogic.base, graphLogic.complete)
}
