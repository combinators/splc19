package gpl.productline

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.body.{BodyDeclaration, FieldDeclaration, MethodDeclaration}
import gpl.domain.{Graph, GraphDomain, SemanticTypes}
import org.combinators.cls.interpreter.combinator
import org.combinators.cls.types.{Arrow, Type, Constructor}
import org.combinators.cls.types.Type
import org.combinators.cls.types.syntax._
import org.combinators.templating.twirl.Java
//(extensions:Seq[BodyDeclaration[_]]): CompilationUnit
//(): CompilationUnit =
trait VertexDomain extends SemanticTypes {

  val graph:Graph

  @combinator object vertexBase{
    def apply(extensions:Seq[BodyDeclaration[_]]): CompilationUnit = {
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
           |  public  Vertex representative;
           |  public LinkedList members;
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
           |        public void display( )
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
           |        //    public EdgeIfc next( )
           |        //    {
           |        //      return ( ( EdgeIfc ) ( ( Neighbor )iter.next( ) ).edge );
           |        //    }
           |            public boolean hasNext( )
           |            {
           |              return iter.hasNext( );
           |            }
           |        };
           |    }
           |
           |
           |
           |${extensions.mkString("\n")}
           |}""".stripMargin).compilationUnit
    }

    val semanticType: Type = vertexLogic(vertexLogic.base,vertexLogic.extensions) =>:
                             vertexLogic(vertexLogic.base, vertexLogic.complete)
  }

  class ColoredVertex {
    def apply() : Seq[BodyDeclaration[_]] = {
            Java(s"""
                    |    private int color;
                    |
                    |    public void setColor(int c) {
                    |        this.color = c;
                    |    }
                    |
                    |    public int getColor() {
                    |        return this.color;
                    |    }
                    |""".stripMargin).classBodyDeclarations()
        }

        val semanticType: Type = vertexLogic(vertexLogic.base, vertexLogic.var_colored)

    }


  /**
    * Extensions to the Vertex concept
    *
    * 1. adjacentVertices as linked list
    * 2. get neighbors as iterator
    */
    class VertexNeighborList {
           def apply(): Seq[BodyDeclaration[_]] = {
      Java(
        s"""
           | public LinkedList<Vertex> adjacentVertices = new LinkedList<>();
           |/*
           |    public VertexIter getNeighbors( ) {
           |
           |        return new VertexIter( )
           |        {
           |            private Iterator<Vertex> iter = adjacentVertices.iterator();
           |            public Vertex next( )
           |            {
           |               return iter.next( );
           |            }
           |
           |            public boolean hasNext( )
           |            {
           |               return iter.hasNext( );
           |            }
           |        };
           |    }
           |//--------------------
           |// differences
           |//--------------------
           |
           |    public void addAdjacent( Vertex n ) {
           |        adjacentVertices.add( n );
           |    }
           |
           |
           |    public LinkedList<Vertex> getNeighborsObj( )
           |    {
           |      return adjacentVertices;
           |    }
           |
           |    public EdgeIter getEdges( )
           |    {
           |        final Vertex self = this;
           |
           |        return new EdgeIter( )
           |        {
           |            private Iterator<Vertex> iter = adjacentVertices.iterator( );
           |            public EdgeIfc next( )
           |            {
           |              Vertex other = iter.next( );
           |              return new Edge (self, other);
           |            }
           |            public boolean hasNext( )
           |            {
           |              return iter.hasNext( );
           |            }
           |        };
           |    }
           |*/
         """.stripMargin).classBodyDeclarations()
    }

    val semanticType: Type = vertexLogic(vertexLogic.base, vertexLogic.var_neighborList)
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

    val semanticType: Type = vertexIterLogic(vertexIterLogic.base,vertexIterLogic.complete )

  }

  @combinator object FixedWeightedEdgeIfc {
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

  class neighborWeighted {
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

//  class VertexChaining(cons: Type*) {
//    val empty:Seq[BodyDeclaration[_]] = Seq.empty
//    def apply(bd:Seq[BodyDeclaration[_]]*) : Seq[BodyDeclaration[_]] = bd.foldRight(empty)(_ ++ _)
//
//    val semanticType:Type = cons.foldRight(vertexLogic(vertexLogic.base, vertexLogic.extensions))((current,last) => Arrow(current,last)).asInstanceOf[Constructor]
//  }

//
//  class VertexChained1(t1:Type) {
//    def apply(bd1:Seq[BodyDeclaration[_]]): Seq[BodyDeclaration[_]] =
//      bd1
//
//    val semanticType:Type = vertexLogic(vertexLogic.base, t1) =>:
//      vertexLogic(vertexLogic.base, vertexLogic.extensions)
//  }

  class VertexChained2(t1:Type, t2:Type) {
    def apply(bd1:Seq[BodyDeclaration[_]], bd2:Seq[BodyDeclaration[_]]): Seq[BodyDeclaration[_]] =
      bd1 ++ bd2

    val semanticType:Type = t1 =>: t2 =>: vertexLogic(vertexLogic.base, vertexLogic.extensions)

    //    val semanticType:Type = vertexLogic(vertexLogic.base, t1) =>:
//                            vertexLogic(vertexLogic.base, t2) =>:
//                            vertexLogic(vertexLogic.base, vertexLogic.extensions)
  }

  // vertexLogic(vertexLogic.base, vertexLogic.var_neighborList)
}
