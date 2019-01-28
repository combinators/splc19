package gpl.productline

import com.fasterxml.jackson.databind.util.NameTransformer.Chained
import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.body.BodyDeclaration
import com.github.javaparser.ast.stmt.Statement
import gpl.domain.{Graph, SemanticTypes}
import org.combinators.cls.interpreter.combinator
import org.combinators.cls.types.{Arrow, Type}
import org.combinators.cls.types.syntax._
import org.combinators.templating.twirl.Java
import java.util
import java.util.Collections
import java.util.Comparator


trait GraphStructureDomain extends SemanticTypes {

  val graph:Graph

  @combinator object sampleTemplate {
    def apply() : Seq[BodyDeclaration[_]] = {
      // NOT WORKINGalgorithms.java.render().compilationUnit()
      Java(
        s"""
           |Graph Prim( Vertex r ) {
           |        Vertex root;
           |
           |        root = r;
           |        Vertex x;
           |
           |        // 2. and 3. Initializes the vertices
           |        for ( VertexIter vxiter = getVertices(); vxiter.hasNext(); )
           |        {
           |        x = vxiter.next();
           |        x.pred = null;
           |        x.key = Integer.MAX_VALUE;
           |        }
           |
           |        // 4. and 5.
           |        root.key = 0;
           |        root.pred = null;
           |
           |        // 2. S <- empty set
           |
           |        // 1. Queue <- V[G], copy the vertex in the graph in the priority queue
           |        LinkedList Queue = new LinkedList();
           |        Set indx = new HashSet( );
           |
           |        // Inserts the root at the head of the queue
           |        Queue.add( root );
           |        indx.add( root.getName( ) );
           |        for ( VertexIter vxiter = getVertices(); vxiter.hasNext(); )
           |        {
           |        x = vxiter.next();
           |        if ( x.key != 0 ) // this means, if this is not the root
           |        {
           |        Queue.add( x );
           |        indx.add( x.getName( ) );
           |        }
           |        }
           |
           |        // Inserts the root at the head of the queue
           |        // Queue.addFirst( root );
           |
           |        // 6. while Q!=0
           |        Vertex ucurrent;
           |        int j,k,l;
           |        int pos;
           |        LinkedList Uneighbors;
           |        Vertex u,v;
           |        EdgeIfc en;
           |        NeighborIfc vn;
           |
           |        int wuv;
           |        boolean isNeighborInQueue = false;
           |
           |        // Queue is a list ordered by key values.
           |        // At the beginning all key values are INFINITUM except
           |        // for the root whose value is 0.
           |        while ( Queue.size()!=0 )
           |        {
           |        // 7. u <- Extract-Min(Q);
           |        // Since this is an ordered queue the first element is the min
           |        u = ( Vertex )Queue.removeFirst();
           |        indx.remove( u.getName( ) );
           |
           |        // 8. for each vertex v adjacent to u
           |        Uneighbors = u.getNeighborsObj( );
           |
           |        k = 0;
           |        for( EdgeIter edgeiter = u.getEdges(); edgeiter.hasNext(); k++ )
           |        {
           |        vn = ( NeighborIfc )Uneighbors.get( k );
           |        en = edgeiter.next();
           |
           |        v = en.getOtherVertex(u);
           |
           |        // Check to see if the neighbor is in the queue
           |        isNeighborInQueue = false;
           |
           |        // if the Neighor is in the queue
           |        if ( indx.contains( v.getName( ) ) )
           |        isNeighborInQueue = true;
           |        wuv = en.getWeight();
           |
           |        // 9. Relax (u,v w)
           |        if ( isNeighborInQueue && ( wuv < v.key ) )
           |        {
           |        v.key = wuv;
           |        v.pred = u.getName();
           |        Uneighbors.set( k,vn ); // adjust values in the neighbors
           |
           |        // update the values of v in the queue
           |        // Remove v from the Queue so that we can reinsert it
           |        // in a new place according to its new value to keep
           |        // the Linked List ordered
           |        Object residue = ( Object ) v;
           |        Queue.remove( residue );
           |        // Object residue = Queue.remove( indexNeighbor );
           |
           |        indx.remove( v.getName( ) );
           |
           |        // Get the new position for v
           |        int position = Collections.binarySearch( Queue,v,
           |        new Comparator() {
           |public int compare( Object o1, Object o2 )
           |        {
           |        Vertex v1 = ( Vertex )o1;
           |        Vertex v2 = ( Vertex )o2;
           |
           |        if ( v1.key < v2.key )
           |        return -1;
           |        if ( v1.key == v2.key )
           |        return 0;
           |        return 1;
           |        }
           |        } );
           |
           |        // Adds v in its new position in Queue
           |        if ( position < 0 )  // means it is not there
           |        {
           |        Queue.add( - ( position+1 ),v );
           |        }
           |        else      // means it is there
           |        {
           |        Queue.add( position,v );
           |        }
           |        indx.add( v.getName( ) );
           |
           |        } // if 8-9.
           |        } // for all neighbors
           |        } // of while
           |
           |        // Creates the new Graph that contains the SSSP
           |        String theName;
           |        Graph newGraph = new  Graph();
           |
           |        // Creates and adds the vertices with the same name
           |        for ( VertexIter vxiter = getVertices( ); vxiter.hasNext( ); )
           |        {
           |        Vertex vtx = vxiter.next( );
           |        theName = vtx.name;
           |
           |        newGraph.addVertex( new  Vertex().assignName( theName ) );
           |        }
           |
           |        // Creates the edges from the NewGraph
           |        Vertex theVertex, thePred;
           |        Vertex theNewVertex, theNewPred;
           |        EdgeIfc   e;
           |
           |        // Creates and adds the vertices with the same name
           |        for ( VertexIter vxiter = getVertices( ); vxiter.hasNext( ); )
           |        {
           |        // theVertex and its Predecessor
           |        theVertex = vxiter.next( );
           |
           |        thePred = findsVertex( theVertex.pred );
           |
           |        // if theVertex is the source then continue we dont need
           |        // to create a new edge at all
           |        if ( thePred==null )
           |        continue;
           |
           |        // Find the references in the new Graph
           |        theNewVertex = newGraph.findsVertex( theVertex.name );
           |        theNewPred = newGraph.findsVertex( thePred.name );
           |
           |        // Creates the new edge from predecessor -> vertex in the newGraph
           |        // and ajusts the adorns based on the old edge
           |        EdgeIfc theNewEdge = newGraph.addEdge( theNewPred, theNewVertex );
           |        e = findsEdge( thePred,theVertex );
           |        theNewEdge.adjustAdorns( e );
           |        }
           |        return newGraph;
           |}""".stripMargin).classBodyDeclarations()
    }

    val semanticType: Type = 'primImplementation
  }

  @combinator object undirectedGenR {
    def apply() : Seq[BodyDeclaration[_]] = {
      Java(
        s"""
           |    LinkedList edges = new LinkedList();
           |
           |    public void sortEdges(Comparator c) {
           |        Collections.sort(edges, c);
           |    }
           |
           |    // Adds an edge without weights if Weighted layer is not present
           |    public EdgeIfc addEdge(Vertex start,  Vertex end) {
           |        Edge theEdge = new  Edge();
           |        theEdge.EdgeConstructor( start, end );
           |        edges.add( theEdge );
           |        start.addNeighbor( new  Neighbor( end, theEdge ) );
           |        end.addNeighbor( new  Neighbor( start, theEdge ) );
           |
           |        return theEdge;
           |    }
           |
           |    public EdgeIter getEdges() {
           |        return new EdgeIter() {
           |                private Iterator iter = edges.iterator();
           |                public EdgeIfc next() { return (EdgeIfc)iter.next(); }
           |                public boolean hasNext() { return iter.hasNext(); }
           |            };
           |    }
           |
           |    // Finds an Edge given both of its vertices
           |    public  EdgeIfc findsEdge( Vertex theSource,
           |                    Vertex theTarget )
           |       {
           |        EdgeIfc theEdge;
           |
           |        for( EdgeIter edgeiter = theSource.getEdges(); edgeiter.hasNext(); )
           |         {
           |            theEdge = edgeiter.next();
           |            if ( ( theEdge.getStart().getName().equals( theSource.getName() ) &&
           |                  theEdge.getEnd().getName().equals( theTarget.getName() ) ) ||
           |                 ( theEdge.getStart().getName().equals( theTarget.getName() ) &&
           |                  theEdge.getEnd().getName().equals( theSource.getName() ) ) )
           |                return theEdge;
           |        }
           |        return null;
           |    }
           |
           |    public void display() {
           |        System.out.println( "******************************************" );
           |        System.out.println( "Vertices " );
           |        for ( VertexIter vxiter = getVertices(); vxiter.hasNext() ; )
           |            vxiter.next().display();
           |
           |        System.out.println( "******************************************" );
           |        System.out.println( "Edges " );
           |        for ( EdgeIter edgeiter = getEdges(); edgeiter.hasNext(); )
           |            edgeiter.next().display();
           |
           |        System.out.println( "******************************************" );
           |
           |}""".stripMargin).classBodyDeclarations()
    }

    val semanticType: Type = graphLogic(graphLogic.base, 'undirectedGenR)//'undirectedGenR
  }

  //        val options = Seq(graphLogic(graphLogic.base, 'primImplementation),
  //          graphLogic(graphLogic.base, 'Extension3))
  //
  //        // note: SEQ : _ * turns a sequence into a variable arguments list IN A FUNCTION CALL
  //        updated = updated.addCombinator (
  //          new Chained (graphLogic(graphLogic.base, graphLogic.extensions), options : _ *)
  //

  class ChainedHere(inner:Type, cons: Type*) {
    def apply(bd:Seq[BodyDeclaration[_]]*) : Seq[BodyDeclaration[_]] =
      bd.foldRight(Seq.empty.asInstanceOf[Seq[BodyDeclaration[_]]])(_ ++ _)

    val semanticType:Type = cons.foldRight(inner)((a,b) => Arrow(a,b))
  }

  @combinator object graphBase {
    def apply(body : Seq[BodyDeclaration[_]]): CompilationUnit = {
      //println ("SAMPLE:" )
      val options = Seq(graphLogic(graphLogic.base, 'primImplementation),
                graphLogic(graphLogic.base, 'undirectedGenR))
      val cv = new ChainedHere(graphLogic(graphLogic.base, graphLogic.extensions), options : _ *)
   //   println ("cv:" + cv.semanticType)
   //   println ("my:" + semanticType)

      Java(
        s"""
           |package gpl;
           |
           |import java.util.LinkedList;
           |
           |import java.util.Iterator;
           |import java.util.Collections;
           |import java.util.Set;
           |import java.util.Comparator;
           |import java.util.HashMap;
           |import java.util.Map;
           |import java.util.HashSet;
           |
           |public class Graph  {
           |LinkedList vertices;
           |   Graph(){
           |     vertices = new LinkedList();
           |   }
           |
           |   public VertexIter getVertices( ) {
           |      return new VertexIter(this);
           |   }
           |
           |   public void sortVertices(Comparator c) {
           |      Collections.sort(vertices, c);
           |   }
           |
           |   EdgeIfc addEdge( Vertex v1, Vertex v2 ) { return null; }
           |   Vertex findsVertex( String name ) { return null; }
           |   void display() { }
           |   void addVertex( Vertex v ) { }
           |
           | LinkedList edges = new LinkedList();
           |
           |    public void sortEdges(Comparator c) {
           |        Collections.sort(edges, c);
           |    }
           |
           |    public EdgeIter getEdges() {
           |        return new EdgeIter() {
           |                private Iterator iter = edges.iterator();
           |                public EdgeIfc next() { return (EdgeIfc)iter.next(); }
           |                public boolean hasNext() { return iter.hasNext(); }
           |            };
           |    }
           |
           |    // Finds an Edge given both of its vertices
           |    public  EdgeIfc findsEdge( Vertex theSource,
           |                    Vertex theTarget )
           |       {
           |        EdgeIfc theEdge;
           |
           |        for( EdgeIter edgeiter = theSource.getEdges(); edgeiter.hasNext(); )
           |         {
           |            theEdge = edgeiter.next();
           |            if ( ( theEdge.getStart().getName().equals( theSource.getName() ) &&
           |                  theEdge.getEnd().getName().equals( theTarget.getName() ) ) ||
           |                 ( theEdge.getStart().getName().equals( theTarget.getName() ) &&
           |                  theEdge.getEnd().getName().equals( theSource.getName() ) ) )
           |                return theEdge;
           |        }
           |        return null;
           |    }
           |
           | ${body.mkString("\n")}
           |
           |}
         """.stripMargin).compilationUnit
    }

    val semanticType: Type = graphLogic(graphLogic.base, graphLogic.extensions) =>:
      graphLogic(graphLogic.base, graphLogic.complete)
  }

}
