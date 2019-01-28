package gpl.productline

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.body.BodyDeclaration
import gpl.domain.{Graph, GraphDomain, SemanticTypes}
import org.combinators.cls.interpreter.combinator
import org.combinators.cls.types.Type
import org.combinators.templating.twirl.Java

import org.combinators.cls.types.Type
import org.combinators.cls.types.syntax._
import org.combinators.templating.twirl.Java

trait VertexDomain extends SemanticTypes {

  val graph:Graph

  @combinator object vertexBase{
    def apply(): CompilationUnit = {
      Java(
        s"""
           |package gpl;
           |
           |import java.util.LinkedList;
           |
           |import java.util.Iterator;
           |import java.util.Collections;
           |import java.util.Comparator;
           |import java.util.HashMap;
           |import java.util.Map;
           |
           |public class Vertex  {
           |  public String name = null;
           |
           |  public  Vertex assignName( String name ) {
           |      this.name = name;
           |      return ( Vertex ) this;
           |  }
           |
           |  public String getName( ) {  return this.name; }
           |
           |   public String pred; // the predecessor vertex if any
           |   public int key; // weight so far from s to it
           |
           |    public LinkedList neighbors = new LinkedList( );
           |
           |    public LinkedList getNeighborsObj( )
           |    {
           |      return neighbors;
           |    }
           |
           |    public VertexIter getNeighbors( )
           |    {
           |        return new VertexIter( )
           |        {
           |            private Iterator iter = neighbors.iterator( );
           |            public Vertex next( )
           |            {
           |              return ( ( Neighbor )iter.next( ) ).end;
           |            }
           |            public boolean hasNext( )
           |            {
           |              return iter.hasNext( );
           |            }
           |        };
           |    }
           |
           |    public void display( )
           |    {
           |        System.out.print( " Node " + name + " connected to: " );
           |
           |        for ( VertexIter vxiter = getNeighbors( ); vxiter.hasNext( ); )
           |        {
           |            System.out.print( vxiter.next().getName() + ", " );
           |        }
           |
           |        System.out.println( );
           |    }
           |//--------------------
           |// differences
           |//--------------------
           |
           |    public void addNeighbor( Neighbor n )
           |    {
           |        neighbors.add( n );
           |    }
           |
           |    public EdgeIter getEdges( )
           |    {
           |        return new EdgeIter( )
           |        {
           |            private Iterator iter = neighbors.iterator( );
           |            public EdgeIfc next( )
           |            {
           |              return ( ( EdgeIfc ) ( ( Neighbor )iter.next( ) ).edge );
           |            }
           |            public boolean hasNext( )
           |            {
           |              return iter.hasNext( );
           |            }
           |        };
           |    }
           |
           |
           |
           |
           |   }
           |
           |
           |
           |
         """.stripMargin).compilationUnit
    }

    val semanticType: Type = vertexLogic(vertexLogic.base, vertexLogic.complete)
  }

  @combinator object VertexIter{
    def apply(): CompilationUnit  = {
      Java(
        s"""
           |package gpl;
           |
           |import java.util.Iterator;
           |public class VertexIter{
           |   private Iterator iter;
           |
           |   VertexIter() { } // used for anonymous class
           |   VertexIter( Graph g ) { iter = g.vertices.iterator(); }
           |   public Vertex next() { return (Vertex)iter.next(); }
           |   public boolean hasNext() { return iter.hasNext(); }
           |
           |}
         """.stripMargin).compilationUnit
    }

    val semanticType: Type = vertexIterLogic(vertexIterLogic.base,vertexLogic.complete )

  }

  @combinator object EdgeIfc {
    def apply(): CompilationUnit  =  {
      Java(
        s"""
           |package gpl;
           |
           |public interface EdgeIfc {
           |    public Vertex getStart( );
           |    public Vertex getEnd( );
           |    public void display( );
           |
           |    public Vertex getOtherVertex( Vertex vertex );
           |    public void adjustAdorns( EdgeIfc the_edge );
           |    public int getWeight();
           |
           |}
         """.stripMargin).compilationUnit
    }

    val semanticType: Type = edgeIfcLogic(edgeIfcLogic.base,edgeIfcLogic.complete )

  }

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
    def apply(): CompilationUnit  = {
      Java(
        s"""
           |package gpl;
           |
           |public class Neighbor {
           |    public  Vertex end;
           |    public  Edge edge;
           |
           |    public Neighbor() {
           |        end = null;
           |        edge = null;
           |    }
           |
           |    public Neighbor( Vertex v,  Edge e ) {
           |        end = v;
           |        edge = e;
           |    }
           |
           |}
           |
         """.stripMargin).compilationUnit
    }

    val semanticType: Type = neighborLogic(neighborLogic.base,neighborLogic.complete )

  }

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



}
