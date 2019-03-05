package gpl.productline

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.body.BodyDeclaration
import gpl.domain.{Graph, SemanticTypes}
import org.combinators.cls.interpreter.combinator
import org.combinators.cls.types.Type
import org.combinators.cls.types.syntax._
import org.combinators.templating.twirl.Java

trait NeighborDomain extends SemanticTypes {

  val graph:Graph

  @combinator object NeighborIfc {
    def apply(): CompilationUnit  = {
      Java(
        s"""
           |package gpl;
           |
           |public interface NeighborIfc {
           |
           |}
         """.stripMargin).compilationUnit
    }

    val semanticType: Type = neighborIfcLogic(neighborIfcLogic.base,neighborIfcLogic.complete )
  }

  @combinator object Neighbor {
    def apply(extensions:Seq[BodyDeclaration[_]]): CompilationUnit  = {
      Java(
        s"""
           |package gpl;
           |
           |public class Neighbor {
           |    public  Vertex neighbor;
           |    public  Vertex end;
           |    public  Edge edge;
           |
           |    public Neighbor( Vertex v,  Edge e ) {
           |        end = v;
           |        edge = e;
           |    }
           |
           |     public Neighbor()  {
           |        neighbor = null;
           |    }
           |
           |    public Neighbor( Vertex theNeighbor )
           |   {
           |        NeighborConstructor( theNeighbor );
           |    }
           |    public void NeighborConstructor( Vertex theNeighbor ) {
           |        neighbor = theNeighbor;
           |    }
           |    public Vertex getStart( ) { return null; }
           |    public Vertex getEnd( ) { return neighbor; }
           |
           |    public Vertex getOtherVertex( Vertex vertex )
           |    {
           |        return neighbor;
           |    }
           |
           |
           |${extensions.mkString("\n")}
           |}
         """.stripMargin).compilationUnit
    }

    val semanticType: Type =neighborLogic(neighborLogic.base,neighborLogic.extensions ) =>: neighborLogic(neighborLogic.base,neighborLogic.complete )
  }

  class NeighborWeighted {
    def apply(): Seq[BodyDeclaration[_]]  =  {
      Java(
        s"""
           |public int weight;
           |
           |    public Neighbor( Vertex theNeighbor, int theWeight ) {
           |        NeighborConstructor( theNeighbor, theWeight );
           |    }
           |
           |    public void NeighborConstructor( Vertex theNeighbor, int theWeight )
           |    {
           |        NeighborConstructor( theNeighbor );
           |        weight = theWeight;
           |    }
           |
           |    public void setWeight(int weight)
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
    }

    val semanticType: Type = neighborLogic(neighborLogic.base,neighborLogic.extensions )
  }

}
