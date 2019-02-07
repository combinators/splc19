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

  class primAlgorithm {
    def apply() : Seq[BodyDeclaration[_]] = {
      Java(
        s"""
           |public void Prim( ) {
           |  Vertex start;
           |        start =(Vertex) vertices.get(0);
           |        newVertex.add(start);
           |        for (int n = 0; n < vertices.size() - 1; n++) {
           |            Vertex temp = new Vertex();
           |            temp.assignName(start.name);
           |            Edge tempedge = new Edge(start, start, 1000);
           |            for (Vertex v : newVertex) {
           |                for (Edge e : edges) {
           |                    if (e.getStart() == v && !containVertex(e.getEnd())) {
           |                        if (e.getWeight() < tempedge.getWeight()) {
           |                            temp = e.getEnd();
           |                            tempedge = e;
           |                        }
           |                    }
           |                }
           |            }
           |            newVertex.add(temp);
           |        }
           |        Iterator it = newVertex.iterator();
           |        while (it.hasNext()) {
           |            Vertex v = (Vertex) it.next();
           |            System.out.println(v.name);
           |        }
           |}""".stripMargin).classBodyDeclarations()
    }

    val semanticType: Type ='primImplementation //graphLogic(graphLogic.base, graphLogic.prim)//
  }

  class kruskalAlgorithm {
    def apply() : Seq[BodyDeclaration[_]] = {
      Java(
        s"""
           | int ver;
           |
           |    public Graph(int ver) {
           |        this.ver = ver;
           |    }
           |        public void addEgde(int source, int destination, int weight) {
           |        Edge edge = new Edge(source, destination, weight);
           |        // add to total edges
           |        edges.add(edge);
           |    }
           |
           |     public void kruskal(){
           |        PriorityQueue<Edge> pq = new PriorityQueue<>(edges.size(), Comparator.comparingInt(o -> o.getWeight()));
           |
           |        //add all the edges to priority queue, //sort the edges on weights
           |        for (int i = 0; i <edges.size() ; i++) {
           |            pq.add(edges.get(i));
           |        }
           |
           |        //create a parent []
           |        int [] parent = new int[ver];
           |
           |        //makeset
           |        makeSet(parent);
           |
           |        ArrayList<Edge> mst = new ArrayList<>();
           |
           |        //process vertices - 1 edges
           |        int index = 0;
           |        while(index<ver-1){
           |            Edge edge = pq.remove();
           |            //check if adding this edge creates a cycle
           |            int x_set = find(parent, edge.source);
           |            int y_set = find(parent, edge.destination);
           |
           |            if(x_set==y_set){
           |                //ignore, will create cycle
           |            }else {
           |                //add it to our final result
           |                mst.add(edge);
           |                index++;
           |                union(parent,x_set,y_set);
           |            }
           |        }
           |        //print MST
           |        System.out.println("Minimum Spanning Tree: ");
           |        printGraph(mst);
           |    }
           |
           |    public void makeSet(int [] parent){
           |        //Make set- creating a new element with a parent pointer to itself.
           |        for (int i = 0; i < ver ; i++) {
           |            parent[i] = i;
           |        }
           |    }
           |
           |    public int find(int [] parent, int vertex){
           |        //chain of parent pointers from x upwards through the tree
           |        // until an element is reached whose parent is itself
           |        if(parent[vertex]!=vertex)
           |            return find(parent, parent[vertex]);;
           |        return vertex;
           |    }
           |
           |    public void union(int [] parent, int x, int y){
           |        int x_set_parent = find(parent, x);
           |        int y_set_parent = find(parent, y);
           |        //make x as parent of y
           |        parent[y_set_parent] = x_set_parent;
           |    }
           |
           |    public void printGraph(ArrayList<Edge> edgeList){
           |        for (int i = 0; i <edgeList.size() ; i++) {
           |            Edge edge = edgeList.get(i);
           |            System.out.println("Edge-" + i + " source: " + edge.source +
           |                    " destination: " + edge.destination +
           |                    " weight: " + edge.getWeight());
           |        }
           |    }
           |""".stripMargin).classBodyDeclarations()
    }

    val semanticType: Type ='kruskalImplementation
  }

  // this SHOULD be replaced with dynamic combinator that glues together multiple extensions into one
/*
  @combinator object HACK_GLUEPrim {
    def apply(bd:Seq[BodyDeclaration[_]]): Seq[BodyDeclaration[_]] = bd

    val semanticType: Type = 'primImplementation =>:
      graphLogic(graphLogic.base, graphLogic.extensions)
  }
  */

/*
  @combinator object HACK_GLUEKruskal {
    def apply(bd:Seq[BodyDeclaration[_]]): Seq[BodyDeclaration[_]] = bd

    val semanticType: Type = 'kruskalImplementation =>:
      graphLogic(graphLogic.base, graphLogic.extensions)
  }
*/

  class graphChained1(t1:Type) {
    def apply(bd1:Seq[BodyDeclaration[_]]): Seq[BodyDeclaration[_]] =
      bd1

    val semanticType:Type = t1 =>:
      graphLogic(graphLogic.base, graphLogic.extensions)
  }



  @combinator object undirectedGenR {
    def apply() : Seq[BodyDeclaration[_]] = {
      Java(
        s"""
           |    LinkedList<Edge> edges = new LinkedList();
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

    val semanticType: Type = 'undirectedGenR//'undirectedGenR
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
      val options = Seq(graphLogic(graphLogic.base, 'undirectedGR),
                graphLogic(graphLogic.base, 'undirectedGenR))
      val cv = new ChainedHere(graphLogic(graphLogic.base, graphLogic.extensions), options : _ *)
   //   println ("cv:" + cv.semanticType)
   //   println ("my:" + semanticType)

      Java(
        s"""
           |package gpl;
           |
           |import java.util.*;
           |
           |public class Graph  {
           |public static List<Vertex> newVertex = new ArrayList<Vertex>();//vertex visited
           |LinkedList vertices;
           |   public Graph(){
           |     vertices = new LinkedList();
           |   }
           |   public VertexIter getVertices( ) {
           |return new VertexIter(this);
           |   }
           |
           |   public void sortVertices(Comparator c) {
           |      Collections.sort(vertices, c);
           |   }
           |   public EdgeIfc addEdge( Vertex v1, Vertex v2 ) { return null; }
           |   public Vertex findsVertex( String name ) { return null; }
           |   public void display() {  System.out.println( "******************************************" );
           |        System.out.println( "Vertices " );
           |        for ( VertexIter vxiter = getVertices(); vxiter.hasNext() ; )
           |            vxiter.next().display();
           |
           |        System.out.println( "******************************************" );
           |        System.out.println( "Edges " );
           |        for ( EdgeIter edgeiter = getEdges(); edgeiter.hasNext(); )
           |            edgeiter.next().display();
           |
           |        System.out.println( "******************************************" );}
           |   public void addVertex( Vertex v ) {vertices.add(v); }
           |
           |    LinkedList<Edge> edges = new LinkedList();
           |
           |    public void sortEdges(Comparator c) {
           |        Collections.sort(edges, c);
           |    }
           |
           |    public EdgeIter getEdges() {
           |        return new EdgeIter() {
           |                private Iterator iter = edges.iterator();
           |             //   public EdgeIfc next() { return (EdgeIfc)iter.next(); }
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
           |            theEdge =(EdgeIfc) edgeiter.next();
           |            if ( ( theEdge.getStart().getName().equals( theSource.getName() ) &&
           |                  theEdge.getEnd().getName().equals( theTarget.getName() ) ) ||
           |                 ( theEdge.getStart().getName().equals( theTarget.getName() ) &&
           |                  theEdge.getEnd().getName().equals( theSource.getName() ) ) )
           |                return theEdge;
           |        }
           |        return null;
           |    }
           |
           |            public void addAnEdge( Vertex start,  Vertex end, int weight )
           |   {
           |        Edge e= new Edge(start,end, weight);
           |        edges.add(e);
           |    }
           |
           |    public void addEdge( Vertex start,  Vertex end, int weight )
           |   {
           |         Edge e = new Edge(start, end, weight);
           |         edges.add(e);
           |        //addEdge( start,end ); // adds the start and end as adjacent
           |    }
           |
           |     public static boolean containVertex (Vertex vte) {
           |        for (Vertex v : newVertex) {
           |            if (v.name.equals(vte.name))
           |                return true;
           |        }
           |        return false;
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
