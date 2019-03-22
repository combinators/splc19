package gpl.productline

import com.github.javaparser.ast.{CompilationUnit, Modifier}
import com.github.javaparser.ast.body.{BodyDeclaration, FieldDeclaration, MethodDeclaration}
import com.github.javaparser.ast.expr.Expression
import gpl.domain.{Graph, GraphDomain, JavaCode, SemanticTypes}
import org.combinators.cls.interpreter.combinator
import org.combinators.cls.types.{Arrow, Constructor, Type}
import org.combinators.cls.types.Type
import org.combinators.cls.types.syntax._
import org.combinators.templating.twirl.Java
//(extensions:Seq[BodyDeclaration[_]]): CompilationUnit
//(): CompilationUnit =
trait VertexDomain extends SemanticTypes {

  val graph:Graph

  // extensions:Seq[BodyDeclaration[_]]
  @combinator object vertexBase{
    def apply(): CompilationUnit = {
      Java(
        s"""
           |package gpl;
           |
           |import java.util.*;
           |
           |public class Vertex  {
           |  public final String name;
           |
           |  public Vertex (String n) {
           |     this.name = n;
           |  }
           |
           |   public String pred; // the predecessor vertex if any
           |   public int key; // weight so far from s to it
           |
           |    public LinkedList neighbors = new LinkedList( );
           |
           |    public LinkedList getNeighborsObj( ) {
           |      return neighbors;
           |    }
           |
           |    public void display( ) {
           |        System.out.print( " Node " + name + " connected to: " );
           |
           |        for ( Iterator<Vertex> vxiter = getNeighbors( ); vxiter.hasNext( ); ) {
           |            System.out.print( vxiter.next().name + ", " );
           |        }
           |
           |        System.out.println( );
           |    }
           |
           |    public Iterator<Vertex> getNeighbors( ) { return neighbors.iterator(); }
           |
           |//--------------------
           |// differences
           |//--------------------
           |
           |    public void addNeighbor( Neighbor n ) {
           |      neighbors.add( n );
           |    }
           |
           |    public Iterator<Edge> getEdges( ) { return neighbors.iterator( ); }
           |
           |}""".stripMargin).compilationUnit
    }

    val semanticType: Type = vertexLogic(vertexLogic.base, vertexLogic.complete)//vertexLogic(vertexLogic.base,vertexLogic.extensions) =>:

  }

  /**
    * Add to existing declarations.
    *
    * Modified incoming class (vertexUnit)
    */
  class SearchVertex(val incoming:Type, val outgoing:Type) {
    def apply(vertexUnit:CompilationUnit) : CompilationUnit = {

      val clazz = vertexUnit.getType(0)

      // add field
      clazz.addFieldWithInitializer(Java("boolean").tpe(), "visited", Java("false").expression[Expression](), Modifier.PUBLIC)

      // Add init_vertex method
     val methods = Java(
        s"""
           |public void init_vertex( WorkSpace w ) {
           |  visited = false;
           |  w.init_vertex(this);
           |}
         """.stripMargin).methodDeclarations()
      methods.foreach(m => clazz.addMember(m))

      val dispStmts = Java(
        s"""
           |if (visited) {
           |  System.out.print("  visited");
           |} else {
           |  System.out.println(" !visited");
           |}""".stripMargin).statements

      val dispMethods = clazz.getMethodsBySignature("display")
      dispMethods.forEach (method => JavaCode.prependStatements(method, dispStmts))

      vertexUnit
    }

    val semanticType: Type = incoming =>: outgoing
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
              |        for ( Iterator<Vertex>  vxiter = getNeighbors(); vxiter.hasNext(); )
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

  /**
    * Add to existing declarations.
    *
    * Modified incoming class (vertexUnit) and adds componentNumber
    */
  class ConnectedVertex(val incoming:Type, val outgoing:Type) {
    def apply(vertexUnit:CompilationUnit) : CompilationUnit = {

      val clazz = vertexUnit.getType(0)

      // add field
      clazz.addFieldWithInitializer(Java("int").tpe(), "componentNumber", Java("0").expression[Expression](), Modifier.PUBLIC)

      val dispStmts = Java("""System.out.print( " comp# "+ componentNumber + " " );""").statements

      val dispMethods = clazz.getMethodsBySignature("display")
      dispMethods.forEach (method => JavaCode.prependStatements(method, dispStmts))

      vertexUnit
    }
    val semanticType: Type = incoming =>: outgoing
  }

  class stronglyCVertex {
    def apply() : Seq[BodyDeclaration[_]] = {
      Java(s"""
              |    public int finishTime;
              |    public int strongComponentNumber;
              |
              |    public void display() {
              |        System.out.print( " FinishTime -> " + finishTime + " SCCNo -> " + strongComponentNumber );
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
           |
           | public VertexIter getNeighbors( ) { return adjacentVertices.iterator(); }
           |
           |//--------------------
           |// differences
           |//--------------------
           |
           |    public void addAdjacent( Vertex n ) {
           |        adjacentVertices.add( n );
           |    }
           |
           |    public LinkedList<Vertex> getNeighborsObj() {
           |      return adjacentVertices;
           |    }
           |
           |    public Iterator<Edge> getEdges( ) { return adjacentVertices.iterator( ); }
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
