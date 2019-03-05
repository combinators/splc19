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

  class SearchVertex {
    def apply() : Seq[BodyDeclaration[_]] = {
      Java(s"""
              |    public boolean visited = false;
              |
              |    public void init_vertex( WorkSpace w )
              |    {
              |        visited = false;
              |        w.init_vertex( ( Vertex ) this );
              |    }
              |
              |    public void display( )
              |    {
              |        if ( visited )
              |            System.out.print( "  visited" );
              |        else
              |            System.out.println( " !visited" );
              |        Super( ).display( );
              |    }
              |""".stripMargin).classBodyDeclarations()
    }

    val semanticType: Type = vertexLogic(vertexLogic.base, vertexLogic.var_search)
  }

  class DFSVertex {
    def apply() : Seq[BodyDeclaration[_]] = {
      Java(s"""
              | public void nodeSearch( WorkSpace w )
              |    {
              |        Vertex v;
              |
              |        // Step 1: Do preVisitAction.
              |        //            If we've already visited this node return
              |        w.preVisitAction( ( Vertex ) this );
              |
              |        if ( visited )
              |            return;
              |
              |        // Step 2: else remember that we've visited and
              |        //         visit all neighbors
              |        visited = true;
              |
              |        for ( VertexIter  vxiter = getNeighbors(); vxiter.hasNext(); )
              |        {
              |            v = vxiter.next( );
              |            w.checkNeighborAction( ( Vertex ) this, v );
              |            v.nodeSearch( w );
              |        }
              |
              |        // Step 3: do postVisitAction now
              |        w.postVisitAction( ( Vertex ) this );
              |    } // of dftNodeSearch
              |""".stripMargin).classBodyDeclarations()
    }

    val semanticType: Type = vertexLogic(vertexLogic.base, vertexLogic.var_dfs)

  }

  class connectedVertex {
    def apply() : Seq[BodyDeclaration[_]] = {
      Java(s"""
              |    public int componentNumber;
              |
              |    public void display( )
              |    {
              |        System.out.print( " comp# "+ componentNumber + " " );
              |        Super( ).display( );
              |    }
              |""".stripMargin).classBodyDeclarations()
    }

    val semanticType: Type = vertexLogic(vertexLogic.base, vertexLogic.var_conn)

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

  class stronglyCVertex {
    def apply() : Seq[BodyDeclaration[_]] = {
      Java(s"""
              |    public int finishTime;
              |    public int strongComponentNumber;
              |
              |    public void display() {
              |        System.out.print( " FinishTime -> " + finishTime + " SCCNo -> "
              |                        + strongComponentNumber );
              |        Super().display();
              |    }
              |""".stripMargin).classBodyDeclarations()
    }

    val semanticType: Type = vertexLogic(vertexLogic.base, vertexLogic.var_stronglyC)

  }

  class NumVertex {
    def apply() : Seq[BodyDeclaration[_]] = {
      Java(s"""
              |    public int VertexNumber;
              |
              |    public void display( )
              |    {
              |        System.out.print( " # "+ VertexNumber + " " );
              |        Super( ).display( );
              |    }
              |""".stripMargin).classBodyDeclarations()
    }
    val semanticType: Type = vertexLogic(vertexLogic.base, vertexLogic.number)
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

//  class VertexChaining(cons: Type*) {
//    val empty:Seq[BodyDeclaration[_]] = Seq.empty
//    def apply(bd:Seq[BodyDeclaration[_]]*) : Seq[BodyDeclaration[_]] = bd.foldRight(empty)(_ ++ _)
//
//    val semanticType:Type = cons.foldRight(vertexLogic(vertexLogic.base, vertexLogic.extensions))((current,last) => Arrow(current,last)).asInstanceOf[Constructor]
//  }

  class workSpaceChained1(t1:Type) {
    def apply(bd1:Seq[BodyDeclaration[_]]): Seq[BodyDeclaration[_]] =
      bd1

    val semanticType:Type = workSpaceLogic(workSpaceLogic.base, t1) =>:
      workSpaceLogic(workSpaceLogic.base, workSpaceLogic.extensions)
  }

  class workSpaceChained2(t1:Type, t2:Type) {
    def apply(bd1:Seq[BodyDeclaration[_]], bd2:Seq[BodyDeclaration[_]]): Seq[BodyDeclaration[_]] =
      bd1 ++ bd2

    val semanticType:Type = t1 =>: t2 =>: workSpaceLogic(workSpaceLogic.base, workSpaceLogic.extensions)

    //    val semanticType:Type = vertexLogic(vertexLogic.base, t1) =>:
    //                            vertexLogic(vertexLogic.base, t2) =>:
    //                            vertexLogic(vertexLogic.base, vertexLogic.extensions)
  }

  class workSpaceChained3(t1:Type, t2:Type,t3:Type) {
    def apply(bd1:Seq[BodyDeclaration[_]], bd2:Seq[BodyDeclaration[_]],bd3:Seq[BodyDeclaration[_]]): Seq[BodyDeclaration[_]] =
      bd1 ++ bd2 ++bd3

    val semanticType:Type = t1 =>: t2 =>: t3 =>: workSpaceLogic(workSpaceLogic.base, workSpaceLogic.extensions)

    //    val semanticType:Type = vertexLogic(vertexLogic.base, t1) =>:
    //                            vertexLogic(vertexLogic.base, t2) =>:
    //                            vertexLogic(vertexLogic.base, vertexLogic.extensions)
  }

  class workSpaceChained4(t1:Type, t2:Type,t3:Type,t4:Type) {
    def apply(bd1:Seq[BodyDeclaration[_]], bd2:Seq[BodyDeclaration[_]],bd3:Seq[BodyDeclaration[_]],bd4:Seq[BodyDeclaration[_]]): Seq[BodyDeclaration[_]] =
      bd1 ++ bd2 ++bd3++bd4

    val semanticType:Type = t1 =>: t2 =>: t3 =>:t4 =>: workSpaceLogic(workSpaceLogic.base, workSpaceLogic.extensions)

    //    val semanticType:Type = vertexLogic(vertexLogic.base, t1) =>:
    //                            vertexLogic(vertexLogic.base, t2) =>:
    //                            vertexLogic(vertexLogic.base, vertexLogic.extensions)
  }

 class VertexChained1(t1:Type) {
    def apply(bd1:Seq[BodyDeclaration[_]]): Seq[BodyDeclaration[_]] =
     bd1

    val semanticType:Type = vertexLogic(vertexLogic.base, t1) =>:
      vertexLogic(vertexLogic.base, vertexLogic.extensions)
 }

  class VertexChained2(t1:Type, t2:Type) {
    def apply(bd1:Seq[BodyDeclaration[_]], bd2:Seq[BodyDeclaration[_]]): Seq[BodyDeclaration[_]] =
      bd1 ++ bd2

    val semanticType:Type = t1 =>: t2 =>: vertexLogic(vertexLogic.base, vertexLogic.extensions)

    //    val semanticType:Type = vertexLogic(vertexLogic.base, t1) =>:
    //                            vertexLogic(vertexLogic.base, t2) =>:
    //                            vertexLogic(vertexLogic.base, vertexLogic.extensions)
  }

  class VertexChained3(t1:Type, t2:Type,t3:Type) {
    def apply(bd1:Seq[BodyDeclaration[_]], bd2:Seq[BodyDeclaration[_]],bd3:Seq[BodyDeclaration[_]]): Seq[BodyDeclaration[_]] =
      bd1 ++ bd2 ++bd3

    val semanticType:Type = t1 =>: t2 =>: t3 =>: vertexLogic(vertexLogic.base, vertexLogic.extensions)

    //    val semanticType:Type = vertexLogic(vertexLogic.base, t1) =>:
    //                            vertexLogic(vertexLogic.base, t2) =>:
    //                            vertexLogic(vertexLogic.base, vertexLogic.extensions)
  }

  class VertexChained4(t1:Type, t2:Type,t3:Type,t4:Type) {
    def apply(bd1:Seq[BodyDeclaration[_]], bd2:Seq[BodyDeclaration[_]],bd3:Seq[BodyDeclaration[_]],bd4:Seq[BodyDeclaration[_]]): Seq[BodyDeclaration[_]] =
      bd1 ++ bd2 ++bd3++bd4

    val semanticType:Type = t1 =>: t2 =>: t3 =>:t4 =>: vertexLogic(vertexLogic.base, vertexLogic.extensions)

    //    val semanticType:Type = vertexLogic(vertexLogic.base, t1) =>:
    //                            vertexLogic(vertexLogic.base, t2) =>:
    //                            vertexLogic(vertexLogic.base, vertexLogic.extensions)
  }

  class VertexChained5(t1:Type, t2:Type,t3:Type,t4:Type,t5:Type) {
    def apply(bd1:Seq[BodyDeclaration[_]], bd2:Seq[BodyDeclaration[_]],bd3:Seq[BodyDeclaration[_]],bd4:Seq[BodyDeclaration[_]],bd5:Seq[BodyDeclaration[_]]): Seq[BodyDeclaration[_]] =
      bd1 ++ bd2 ++bd3++bd4++bd5

    val semanticType:Type = t1 =>: t2 =>: t3 =>:t4 =>:t5 =>: vertexLogic(vertexLogic.base, vertexLogic.extensions)

    //    val semanticType:Type = vertexLogic(vertexLogic.base, t1) =>:
    //                            vertexLogic(vertexLogic.base, t2) =>:
    //                            vertexLogic(vertexLogic.base, vertexLogic.extensions)
  }


  class VertexChained6(t1:Type, t2:Type,t3:Type,t4:Type,t5:Type,t6:Type) {
    def apply(bd1:Seq[BodyDeclaration[_]], bd2:Seq[BodyDeclaration[_]],bd3:Seq[BodyDeclaration[_]],bd4:Seq[BodyDeclaration[_]],bd5:Seq[BodyDeclaration[_]],bd6:Seq[BodyDeclaration[_]]): Seq[BodyDeclaration[_]] =
      bd1 ++ bd2 ++bd3++bd4++bd5++bd6

    val semanticType:Type = t1 =>: t2 =>: t3 =>:t4 =>:t5 =>:t6 =>: vertexLogic(vertexLogic.base, vertexLogic.extensions)

    //    val semanticType:Type = vertexLogic(vertexLogic.base, t1) =>:
    //                            vertexLogic(vertexLogic.base, t2) =>:
    //                            vertexLogic(vertexLogic.base, vertexLogic.extensions)
  }



  // vertexLogic(vertexLogic.base, vertexLogic.var_neighborList)
}
