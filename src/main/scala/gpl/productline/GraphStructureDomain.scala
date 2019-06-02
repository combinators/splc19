package gpl.productline

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.body.BodyDeclaration
import gpl.domain.{Graph, SemanticTypes}
import org.combinators.cls.interpreter.combinator
import org.combinators.cls.types.syntax._
import org.combinators.cls.types.{Arrow, Type}
import org.combinators.templating.twirl.Java

trait GraphStructureDomain extends SemanticTypes with VertexDomain {

  val graph:Graph

  /**
    * Base class 'Graph' which is meant for all extensions. It derives its
    * fundamental operations from the generator for accessing information.
    *
    * Note that the class is further processed to include modifications based
    * upon the domain model.
    */
  @combinator object graphBase {
    def apply(gen: StructureGenerator): CompilationUnit = {

      Java(
        s"""
           |package gpl;
           |
           |import java.util.*;
           |
           |public class Graph  {
           |
           |  public static List<Vertex> newVertex = new ArrayList<Vertex>();
           |  LinkedList vertices;
           |
           |   public Graph(){
           |     vertices = new LinkedList();
           |   }
           |   public Iterator<Vertex> getVertices( ) { return vertices.iterator(); }
           |
           |   public void sortVertices(Comparator c) {
           |      Collections.sort(vertices, c);
           |   }
           |
           |  // public Vertex findsVertex(String name) {
           |  //      for (Iterator<Vertex> it = getVertices(); it.hasNext(); ) {
           |  //          Vertex v = it.next();
           |  //          if (v.name.equals(name)) {
           |  //              return v;
           |  //          }
           |  //      }
           |  //      return null;
           |  //  }
           |
           |   public Iterator<Edge> getEdges(Vertex u) {
           |    	LinkedList<Edge> filter = new LinkedList<Edge>();
           |        for (Iterator<Edge> it = edges.iterator(); it.hasNext(); ) {
           |        	Edge e = it.next();
           |        	if (e.getStart().equals(u) || e.getEnd().equals(u)) { filter.add(e); }
           |        }
           |
           |        return filter.iterator();
           |    }
           |
           |    public Edge getEdge(Vertex u, Vertex v) {
           |    	LinkedList<Edge> filter = new LinkedList<Edge>();
           |        for (Iterator<Edge> it = edges.iterator(); it.hasNext(); ) {
           |        	Edge e = it.next();
           |        	if (e.getStart().equals(u) && e.getEnd().equals(v)) { return e; }
           |          if (e.getStart().equals(v) && e.getEnd().equals(u)) { return e; }
           |        }
           |
           |        return null;
           |    }
           |
           |   public void display() {
           |        System.out.println( "******************************************" );
           |        System.out.println( "Vertices " );
           |        for ( Iterator<Vertex> vxiter = getVertices(); vxiter.hasNext() ; )
           |            vxiter.next().display();
           |
           |        System.out.println( "******************************************" );
           |        System.out.println( "Edges " );
           |        for ( Iterator<Edge> edgeiter = getEdges(); edgeiter.hasNext(); )
           |            edgeiter.next().display();
           |
           |        System.out.println( "******************************************" );
           |    }
           |    //Defined in both directedCommon and undirectedCommon
           |    //public void addVertex( Vertex v ) {vertices.add(v); }
           |
           |    LinkedList<Edge> edges = new LinkedList();
           |
           |    public void sortEdges(Comparator c) {
           |        Collections.sort(edges, c);
           |    }
           |
           |    public Iterator<Edge> getEdges() { return edges.iterator(); }
           |
           |    public void addEdge( Vertex start,  Vertex end, int weight ) {
           |         Edge e = new Edge(start, end, weight);
           |         e.source = index(start);
           |         e.destination = index(end);
           |         edges.add(e);
           |    }
           |
           |     public Vertex getVertex (int index) {
           |        int idx = 0;
           |        for (Iterator<Vertex> it = getVertices(); it.hasNext(); ) {
           |            Vertex v = it.next();
           |            if (index == idx) { return v; }
           |            idx++;
           |        }
           |        return null;
           |    }
           |
           |     public int index (Vertex vte) {
           |        int idx = 0;
           |        for (Iterator<Vertex> it = getVertices(); it.hasNext(); ) {
           |            Vertex v = it.next();
           |            if (v.name.equals(vte.name)) {
           |                return idx;
           |            }
           |            idx++;
           |        }
           |        return -1;
           |    }
           |}
         """.stripMargin).compilationUnit
    }

    val semanticType: Type = 'StructureGenerator =>: graphLogic(graphLogic.base)
  }

  class primAlgorithm(incoming:Type, outgoing:Type) {


    // pass in a generator as argument -- not code itself, but code that would know how
    // to generate. HEre CLS no wires together everything, which avoids my needing to
    // do it manually. pre-selecting components and then use CLS to determine wiring.
    // FeatureIDE pre-select components but then manually add constraints to ensure/filter
    // only the valid ones.
    def apply(gen: StructureGenerator, unit:CompilationUnit) : CompilationUnit = {

      // design-time factory not a run-time factory.
      // why no develop own Domain specific language. A strong desire to work
      // within a single development environment and use just the target language under consideration.
      // ANy DSL loses the rich computational ability of whatever language you are using.
      // DSL researchers over-sell the idea.
      //
      // The individual code I write must be readable AND the resulting code in the target language
      // after synthesis must be readable; and this latter is not the concern of DSL because they
      // typically are only concerned about proper functionality of the generated code, not whether
      // it is human-readable.
      //
      // one last argument against creating a DSL is we are already trying to find a way to
      // blend feature-model capabilities with domain-modeling capabilities and so the DSL would
      // have to be a superset of both.
      //
      // interaction with 3rd-party target libraries would have to be modeled within the DSL
      // itself (or simply be supported by foreign API calls).
      //
      // finally without creating the added burden of designing a DSL we wanted to investigate
      // how to effectively blend/coordinate feature-modeling capabilities with domain-modeling
      //
      // the model is a declarative DSL model from which we synthesize the target language in a
      // way that produces more readable code that can easily integrate with arbitrary libraries
      // in the target language.
      //
      // We are truly interested in producing a minimal code base that can easily be understood
      //val gen = new StructureGenerator()

      val str = s"""
           |public Graph Prim() {
           |		Vertex root = null;
           |
           |		// 2. and 3. Initializes the vertices
           |    ${gen.getVerticesBegin("v")}
           |		   if (root == null) { root = v; }
           |       v.pred = null;
           |       v.key = Integer.MAX_VALUE;
           |    ${gen.getVerticesEnd("v")}
           |
           |		// 4. and 5.
           |		root.key = 0;
           |		root.pred = null;
           |
           |		// 1. Queue <- V[G], copy the vertex in the graph in the priority queue
           |		PriorityQueue<Vertex> queue = new PriorityQueue<>(
           |      ${getComparatorBegin("Vertex", "v")}
           |        if (v1.key < v2.key) return -1;
           |        if (v1.key == v2.key) return 0;
           |        return 1;
           |      ${getComparatorEnd("Vertex", "v")});
           |		Set<String> inQueue = new HashSet<>();
           |
           |		// Inserts the root at the head of the queue
           |		queue.add(root);
           |		inQueue.add(root.name);
           |    ${gen.getVerticesBegin("v")}
           |      if (v.key != 0) { // this means, if this is not the root
           |        queue.add(v);
           |        inQueue.add(v.name);
           |      }
           |    ${gen.getVerticesEnd("v")}
           |
           |		// Queue is a list ordered by key values.
           |		// At the beginning all key values are INFINITUM except
           |		// for the root whose value is 0.
           |		while (queue.size() !=0) {
           |			// 7. u <- Extract-Min(Q);
           |			// Since this is an ordered queue the first element is the min
           |			Vertex u = queue.remove();
           |			inQueue.remove (u.name);
           |
           |			// 8. for each vertex v adjacent to u
           |      ${gen.getEdgesOfBegin("en", "u")}
           |        Vertex v = en.getOtherVertex(u);
           |
           |        // Check to see if the neighbor is in the queue
           |        int wuv = en.getWeight();
           |
           |        // 9. Relax (u,v w)
           |        if (inQueue.contains(v.name) && (wuv < v.key)) {
           |          v.key = wuv;
           |          v.pred = u.name;
           |          queue.remove(v);  // NOT EFFICIENT. Should support decreaseKey operation
           |          queue.add(v);
           |         }
           |       ${gen.getEdgesOfEnd("en")}
           |		} // of while
           |
           |		// Creates the new Graph that contains the SSSP
           |		String theName;
           |		Graph newGraph = new  Graph();
           |
           |		// Creates and adds the vertices with the same name
           |    ${gen.getVerticesBegin("vx")}
           |       newGraph.addVertex(new Vertex(vx.name));
           |    ${gen.getVerticesEnd("vx")}
           |
           |		// Creates and adds the vertices with the same name
           |		${gen.getVerticesBegin("theVertex")}
           |			Vertex thePred = findsVertex(theVertex.pred);
           |
           |			// if theVertex is the source then continue we dont need
           |			// to create a new edge at all
           |			if (thePred == null)
           |				continue;
           |
           |			Edge oldEdge = getEdge(findsVertex(theVertex.name), findsVertex(thePred.name));
           |      oldEdge.display();
           |
           |      // Creates the new edge from predecessor -> vertex in the newGraph
           |      newGraph.addEdge(newGraph.findsVertex(theVertex.name),
           |      newGraph.findsVertex(thePred.name),
           |      oldEdge.getWeight());
           |		${gen.getVerticesEnd("theVertex")}
           |
           |		return newGraph;
           |	}
           |""".stripMargin

      // add to existing unit
      val clazz = unit.getType(0)
      Java(str).methodDeclarations()
        .foreach(m => clazz.addMember(m))

      unit
    }

    val semanticType: Type = 'StructureGenerator =>: incoming =>: outgoing  // graphLogic(graphLogic.prim)
  }

  class kruskalAlgorithm(incoming:Type, outgoing:Type) {
    def apply(gen: StructureGenerator, unit:CompilationUnit) : CompilationUnit = {

      val methods = Java(s"""
           |    public void Kruskal(){
           |        PriorityQueue<Edge> pq = new PriorityQueue<>(edges.size(), Comparator.comparingInt(o -> o.getWeight()));
           |
           |        // add all the edges to priority queue, // sort the edges on weights
           |        ${gen.getEdgesBegin("en")}
           |            pq.add(en);
           |        ${gen.getEdgesEnd("en")}
           |
           |        //create a parent [] and set it up properly so parent[i] = i
           |        int [] parent = new int[N];
           |        makeSet(parent);
           |
           |        ArrayList<Edge> mst = new ArrayList<>();
           |
           |        // process N - 1 edges
           |        int index = 0;
           |        while (index < N-1) {
           |            Edge edge = pq.remove();
           |            // check if adding this edge creates a cycle
           |            int x_set = find(parent, edge.source);
           |            int y_set = find(parent, edge.destination);
           |
           |            if (x_set != y_set) {
           |                // add it to our final result if not creating a cycle
           |                mst.add(edge);
           |                index++;
           |                union(parent,x_set,y_set);
           |            }
           |        }
           |        // print MST
           |        System.out.println("Minimum Spanning Tree: ");
           |        printGraph(mst);
           |    }
           |
           |    public void makeSet(int [] parent){
           |        // Make set- creating a new element with a parent pointer to itself.
           |        for (int i = 0; i < N ; i++) {
           |            parent[i] = i;
           |        }
           |    }
           |
           |    public int find(int [] parent, int vertex){
           |        // chain of parent pointers from x upwards through the tree
           |        // until an element is reached whose parent is itself
           |        if (parent[vertex] != vertex)
           |            return find(parent, parent[vertex]);
           |        return vertex;
           |    }
           |
           |    public void union(int [] parent, int x, int y){
           |        int y_set_parent = find(parent, y);
           |
           |        parent[y_set_parent] = find(parent, x);      // make x as parent of y
           |    }
           |
           |    public void printGraph(ArrayList<Edge> edgeList){
           |        for (Edge e : edgeList) {
           |            System.out.println(e);
           |        }
           |    }
           |""".stripMargin).methodDeclarations()

      // add to existing unit
      val clazz = unit.getType(0)
      methods.foreach(m => clazz.addMember(m))

      Java("""public Graph(int N) {
          |        this();
          |        this.N = N;
          |}""".stripMargin).constructors().foreach(f => clazz.addMember(f))

      // add 'int N' as class attribute.
      // add field(s)
      Java("int N;").fieldDeclarations()
        .foreach(f => clazz.addMember(f))

      unit
    }

    val semanticType: Type = 'StructureGenerator =>: incoming =>: outgoing
  }

  class CycleGraph(incoming:Type, outgoing:Type) {
    def apply(gen: StructureGenerator, unit:CompilationUnit) : CompilationUnit = {
      val strr=
        s"""
           |public boolean CycleCheck() {
           |        CycleWorkSpace c = new CycleWorkSpace( isDirected );
           |        GraphSearch( c );
           |        return c.AnyCycles;
           |    }""".stripMargin

      val clazz = unit.getType(0)
      Java(strr).methodDeclarations()
        .foreach(m => clazz.addMember(m))
      unit
    }

    val semanticType: Type = 'StructureGenerator =>: incoming =>: outgoing //graphLogic(graphLogic.base, graphLogic.prim)//
  }

  class connectedGraph(incoming:Type, outgoing:Type) {
    def apply(gen: StructureGenerator, unit:CompilationUnit) : CompilationUnit ={
      val strr=
        s"""
           |    public void ConnectedComponents( )
           |    {
           |        GraphSearch( new RegionWorkSpace( ) );
           |    }""".stripMargin
      val clazz = unit.getType(0)
      Java(strr).methodDeclarations()
        .foreach(m => clazz.addMember(m))

      unit
    }

    val semanticType: Type ='StructureGenerator =>: incoming =>: outgoing//graphLogic(graphLogic.base, graphLogic.prim)//
  }

  class searchGraph(incoming:Type, outgoing:Type) {
    def apply(gen: StructureGenerator, unit:CompilationUnit) : CompilationUnit = {
      val str=
        s"""
           |    public void GraphSearch( WorkSpace w )
           |    {
           |        // Step 1: initialize visited member of all nodes
           |        Iterator<Vertex> vxiter = getVertices( );
           |        if ( vxiter.hasNext( ) == false )
           |        {
           |            return;
           |        }
           |
           |        // Showing the initialization process
           |        while(vxiter.hasNext( ) )
           |        {
           |            Vertex v = vxiter.next( );
           |            v.init_vertex( w );
           |        }
           |
           |        // Step 2: traverse neighbors of each node
           |        for (vxiter = getVertices( ); vxiter.hasNext( ); )
           |        {
           |            Vertex v = vxiter.next( );
           |            if ( !v.visited )
           |            {
           |                w.nextRegionAction( v );
           |                v.nodeSearch( w );
           |            }
           |        } //end for
           |
           |}""".stripMargin

      val clazz = unit.getType(0)
      Java(str).methodDeclarations()
        .foreach(m => clazz.addMember(m))

      unit
    }

    val semanticType: Type = 'StructureGenerator =>: incoming =>: outgoing//graphLogic(graphLogic.base, graphLogic.prim)//
  }

//  class SearchGraph(incoming:Type, outgoing:Type) extends UnitModifier(incoming, outgoing) {
//    override def modify(graphUnit: CompilationUnit): Unit = {
//
//      val clazz = graphUnit.getType(0)
//      val methods = Java(
//        s"""
//           | public void GraphSearch( WorkSpace w )
//           |    {
//           |        // Step 1: initialize visited member of all nodes
//           |        Iterator<Vertex> vxiter = getVertices( );
//           |        if ( vxiter.hasNext( ) == false )
//           |        {
//           |            return;
//           |        }
//           |
//           |        // Showing the initialization process
//           |        while(vxiter.hasNext( ) )
//           |        {
//           |            Vertex v = vxiter.next( );
//           |            v.init_vertex( w );
//           |        }
//           |
//           |        // Step 2: traverse neighbors of each node
//           |        for (vxiter = getVertices( ); vxiter.hasNext( ); )
//           |        {
//           |            Vertex v = vxiter.next( );
//           |            if ( !v.visited )
//           |            {
//           |                w.nextRegionAction( v );
//           |                v.nodeSearch( w );
//           |            }
//           |        } //end for
//           |
//           |} """.stripMargin).methodDeclarations()
//
//      methods.foreach(m => clazz.addMember(m))
//    }
//  }
//
//  class searchGraph {
//    def apply() : Seq[BodyDeclaration[_]] = {
//      Java(
//        s"""
//           |    public void GraphSearch( WorkSpace w )
//           |    {
//           |        // Step 1: initialize visited member of all nodes
//           |        Iterator<Vertex> vxiter = getVertices( );
//           |        if ( vxiter.hasNext( ) == false )
//           |        {
//           |            return;
//           |        }
//           |
//           |        // Showing the initialization process
//           |        while(vxiter.hasNext( ) )
//           |        {
//           |            Vertex v = vxiter.next( );
//           |            v.init_vertex( w );
//           |        }
//           |
//           |        // Step 2: traverse neighbors of each node
//           |        for (vxiter = getVertices( ); vxiter.hasNext( ); )
//           |        {
//           |            Vertex v = vxiter.next( );
//           |            if ( !v.visited )
//           |            {
//           |                w.nextRegionAction( v );
//           |                v.nodeSearch( w );
//           |            }
//           |        } //end for
//           |
//           |}""".stripMargin).classBodyDeclarations()
//    }
//
//    val semanticType: Type = graphLogic.searchCommon //graphLogic(graphLogic.base, graphLogic.prim)//
//  }





  // ability to assign integers to each vertex in the graph (based on NumberWorkspace capability)
  class NumGraph {
    def apply() : Seq[BodyDeclaration[_]] = {
      Java(
        s"""
           |    public void NumberVertices( )
           |    {
           |        GraphSearch( new NumberWorkSpace( ) );
           |    }""".stripMargin).classBodyDeclarations()
    }

    val semanticType: Type = graphLogic.number //graphLogic(graphLogic.base, graphLogic.prim)//
  }
//
//  class stronglyCGraph(incoming:Type, outgoing:Type) {
//    def apply(gen: StructureGenerator, unit:CompilationUnit) : CompilationUnit ={
//      val strr=
//        s"""
//           | public  Graph ComputeTranspose( Graph the_graph )
//           |   {
//           |        int i;
//           |        String theName;
//           |        Map newVertices = new HashMap( );
//           |
//           |        // Creating the new Graph
//           |        Graph newGraph = new  Graph();
//           |
//           |        // Creates and adds the vertices with the same name
//           |        for ( VertexIter vxiter = getVertices(); vxiter.hasNext(); )
//           |        {
//           |            theName = vxiter.next().getName();
//           |            Vertex v = new  Vertex( ).assignName( theName );
//           |            newGraph.addVertex( v );
//           |            newVertices.put( theName, v );
//           |        }
//           |
//           |        Vertex theVertex, newVertex;
//           |        Vertex theNeighbor;
//           |        Vertex newAdjacent;
//           |        EdgeIfc newEdge;
//           |
//           |        // adds the transposed edges
//           |        VertexIter newvxiter = newGraph.getVertices( );
//           |        for ( VertexIter vxiter = getVertices(); vxiter.hasNext(); )
//           |        {
//           |            // theVertex is the original source vertex
//           |            // the newAdjacent is the reference in the newGraph to theVertex
//           |            theVertex = vxiter.next();
//           |
//           |            newAdjacent = newvxiter.next( );
//           |
//           |            for( VertexIter neighbors = theVertex.getNeighbors(); neighbors.hasNext(); )
//           |            {
//           |                // Gets the neighbor object
//           |                theNeighbor = neighbors.next();
//           |
//           |                // the new Vertex is the vertex that was adjacent to theVertex
//           |                // but now in the new graph
//           |                newVertex = ( Vertex ) newVertices.get( theNeighbor.getName( ) );
//           |
//           |                // Creates a new Edge object and adjusts the adornments
//           |                newEdge = newGraph.addEdge( newVertex, newAdjacent );
//           |            } // all adjacentNeighbors
//           |        } // all the vertices
//           |
//           |        return newGraph;
//           |
//           |    } // of ComputeTranspose
//           |
//           |public  Graph StrongComponents() {
//           |
//           |        FinishTimeWorkSpace FTWS = new FinishTimeWorkSpace();
//           |
//           |        // 1. Computes the finishing times for each vertex
//           |        GraphSearch( FTWS );
//           |
//           |        // 2. Order in decreasing  & call DFS Transposal
//           |        sortVertices(
//           |         new Comparator() {
//           |            public int compare( Object o1, Object o2 )
//           |                {
//           |                Vertex v1 = ( Vertex )o1;
//           |                Vertex v2 = ( Vertex )o2;
//           |
//           |                if ( v1.finishTime > v2.finishTime )
//           |                    return -1;
//           |
//           |                if ( v1.finishTime == v2.finishTime )
//           |                    return 0;
//           |                return 1;
//           |            }
//           |        } );
//           |
//           |        // 3. Compute the transpose of G
//           |        // Done at layer transpose
//           |        Graph gaux = ComputeTranspose( ( Graph )this );
//           |
//           |        // 4. Traverse the transpose G
//           |        WorkSpaceTranspose WST = new WorkSpaceTranspose();
//           |        gaux.GraphSearch( WST );
//           |
//           |        return gaux;
//           |
//           |    } // of Strong Components
//           |
//           |""".stripMargin
//      val clazz = unit.getType(0)
//      Java(strr).methodDeclarations()
//        .foreach(m => clazz.addMember(m))
//
//      unit
//    }
//
//    val semanticType: Type ='StructureGenerator =>: incoming =>: outgoing//graphLogic(graphLogic.base, graphLogic.prim)//
//  }

//  public void GraphSearch( WorkSpace w )
//  {
//    // Step 1: initialize visited member of all nodes
//    Iterator<Vertex> vxiter = getVertices( );
//    if ( vxiter.hasNext( ) == false )
//    {
//      return;
//    }
//
//    // Showing the initialization process
//    while(vxiter.hasNext( ) )
//    {
//      Vertex v = vxiter.next( );
//      v.init_vertex( w );
//    }
//
//    // Step 2: traverse neighbors of each node
//    for (vxiter = getVertices( ); vxiter.hasNext( ); )
//    {
//      Vertex v = vxiter.next( );
//      if ( !v.visited )
//      {
//        w.nextRegionAction( v );
//        v.nodeSearch( w );
//      }
//    } //end for


  class stronglyCGraph(incoming:Type, outgoing:Type)  {
     def apply(gen: StructureGenerator,unit: CompilationUnit): CompilationUnit = {

      val methods =
        s"""
           | public  Graph ComputeTranspose( Graph the_graph )
           |   {
           |        int i;
           |        String theName;
           |        Map newVertices = new HashMap( );
           |
           |        // Creating the new Graph
           |        Graph newGraph = new  Graph();
           |
           |        // Creates and adds the vertices with the same name
           |            ${gen.getVerticesBegin("x")}
           |
           |           // theName = vxiter.next().getName();
           |            theName = x.getName();
           |            Vertex v = new  Vertex(theName );
           |            newGraph.addVertex( v );
           |            newVertices.put( theName, v );
           |            ${gen.getVerticesEnd("x")}
           |
           |
           |        Vertex theVertex, newVertex;
           |        Vertex theNeighbor;
           |        Vertex newAdjacent;
           |        Edge newEdge;
           |
           |        // adds the transposed edges
           |
           |        Iterator<Vertex> newvxiter = newGraph.getVertices( );
           |        ${gen.getVerticesBegin("v")}
           |
           |            // theVertex is the original source vertex
           |            // the newAdjacent is the reference in the newGraph to theVertex
           |            theVertex = v;
           |
           |            newAdjacent = newvxiter.next( );
           |
           |            for( Iterator<Vertex> neighbors = theVertex.getNeighbors(); neighbors.hasNext(); )
           |            {
           |                // Gets the neighbor object
           |                theNeighbor = neighbors.next();
           |
           |                // the new Vertex is the vertex that was adjacent to theVertex
           |                // but now in the new graph
           |                newVertex = ( Vertex ) newVertices.get( theNeighbor.getName( ) );
           |
           |                // Creates a new Edge object and adjusts the adornments
           |                newEdge = newGraph.addEdge( newVertex, newAdjacent );
           |            } // all adjacentNeighbors
           |         // all the vertices
           |        ${gen.getVerticesEnd("v")}
           |
           |        return newGraph;
           |
           |    } // of ComputeTranspose
           |
           |public  Graph StrongComponents() {
           |
           |        FinishTimeWorkSpace FTWS = new FinishTimeWorkSpace();
           |
           |        // 1. Computes the finishing times for each vertex
           |        GraphSearch( FTWS );
           |
           |        // 2. Order in decreasing  & call DFS Transposal
           |        sortVertices(
           |         new Comparator() {
           |            public int compare( Object o1, Object o2 )
           |                {
           |                Vertex v1 = ( Vertex )o1;
           |                Vertex v2 = ( Vertex )o2;
           |
           |                if ( v1.finishTime > v2.finishTime )
           |                    return -1;
           |
           |                if ( v1.finishTime == v2.finishTime )
           |                    return 0;
           |                return 1;
           |            }
           |        } );
           |
           |        // 3. Compute the transpose of G
           |        // Done at layer transpose
           |        Graph gaux = ComputeTranspose( ( Graph )this );
           |
           |        // 4. Traverse the transpose G
           |        WorkSpaceTranspose WST = new WorkSpaceTranspose();
           |        gaux.GraphSearch( WST );
           |
           |        return gaux;
           |
           |    } // of Strong Components
           |
           | """.stripMargin

       val clazz = unit.getType(0)
       Java(methods).methodDeclarations()
         .foreach(m => clazz.addMember(m))

       unit
     }

    val semanticType: Type = 'StructureGenerator =>: incoming =>: outgoing//graphLogic(graphLogic.base, graphLogic.prim)//
  }



//  class Transpose {
//    def apply() : Seq[BodyDeclaration[_]] = {
//      Java(
//        s"""
//           |    public  Graph ComputeTranspose( Graph the_graph ) {
//           |        int i;
//           |        String theName;
//           |        Map newVertices = new HashMap( );
//           |
//           |        // Creating the new Graph
//           |        Graph newGraph = new  Graph();
//           |
//           |        // Creates and adds the vertices with the same name
//           |        for ( Iterator<Vertex> vxiter = getVertices(); vxiter.hasNext(); )
//           |        {
//           |            theName = vxiter.next().name;
//           |            Vertex v = new  Vertex( ).assignName( theName );
//           |            newGraph.addVertex( v );
//           |            newVertices.put( theName, v );
//           |        }
//           |
//           |        Vertex theVertex, newVertex;
//           |        Vertex theNeighbor;
//           |        Vertex newAdjacent;
//           |        IEdge newEdge;
//           |
//           |        // adds the transposed edges
//           |        Iterator<Vertex> newvxiter = newGraph.getVertices( );
//           |        for ( Iterator<Vertex> vxiter = getVertices(); vxiter.hasNext(); ) {
//           |            // theVertex is the original source vertex
//           |            // the newAdjacent is the reference in the newGraph to theVertex
//           |            theVertex = vxiter.next();
//           |
//           |            newAdjacent = newvxiter.next( );
//           |
//           |            for( Iterator<Vertex> neighbors = theVertex.getNeighbors(); neighbors.hasNext(); ) {
//           |                // Gets the neighbor object
//           |                theNeighbor = neighbors.next();
//           |
//           |                // the new Vertex is the vertex that was adjacent to theVertex
//           |                // but now in the new graph
//           |                newVertex = ( Vertex ) newVertices.get( theNeighbor.getName( ) );
//           |
//           |                // Creates a new Edge object and adjusts the adornments
//           |                newEdge = newGraph.addEdge( newVertex, newAdjacent );
//           |            } // all adjacentNeighbors
//           |        } // all the vertices
//           |
//           |        return newGraph;
//           |
//           |    } // of ComputeTranspose
//           |""".stripMargin).classBodyDeclarations()
//    }
//
//    val semanticType: Type ='transpose //graphLogic(graphLogic.base, graphLogic.prim)//
//  }


  class directedCommon(incoming:Type, outgoing:Type) extends UnitModifier(incoming, outgoing) {
    override def modify(graphUnit: CompilationUnit): Unit = {

      val clazz = graphUnit.getType(0)
      Java("public static final boolean isDirected = true;"
        ).fieldDeclarations()
        .foreach(f => clazz.addMember(f))
      val methods = Java(
        s"""
           |
           |    public void addVertex( Vertex v ) {
           |       vertices.add( v );
           |    }
           |
           |     public Edge addEdge( Vertex start,  Vertex end ) {
           |        start.addAdjacent( end );
           |        Edge e= new Edge(start,end);
           |        e.source= index(start);
           |        e.destination= index(end);
           |        edges.add(e);
           |        return e;
           |    }
           |
           |//     public IEdge addEdge(Vertex start, Vertex end) {
           |//        start.addAdjacent(end);
           |//        return (IEdge) start;
           |//    }
           |
           |    // Finds a vertex given its name in the vertices list
           |    public  Vertex findsVertex( String theName )
           |      {
           |        int i=0;
           |        Vertex theVertex;
           |
           |        // if we are dealing with the root
           |        if ( theName==null )
           |            return null;
           |
           |        for( i=0; i<vertices.size(); i++ ) {
           |            theVertex = ( Vertex )vertices.get( i );
           |            if ( theName.equals( theVertex.name ) )
           |                return theVertex;
           |        }
           |        return null;
           |    }
           |
           | """.stripMargin).methodDeclarations()

      methods.foreach(m => clazz.addMember(m))
    }
  }

  class undirectedCommon(incoming:Type, outgoing:Type) extends UnitModifier(incoming, outgoing) {
    override def modify(graphUnit: CompilationUnit): Unit = {

      val clazz = graphUnit.getType(0)
      Java("public static final boolean isDirected = false;" +
        "protected Map verticesMap = new HashMap( );").fieldDeclarations()
        .foreach(f => clazz.addMember(f))
      val methods = Java(
        s"""
           |
           |
           |    public void addVertex( Vertex v ) {
           |      vertices.add( v );
           |      verticesMap.put( v.name, v );
           |    }
           |
           |    // Finds a vertex given its name in the vertices list
           |    public  Vertex findsVertex( String theName ) {
           |        // if we are dealing with the root
           |        if ( theName==null )
           |            return null;
           |          return ( Vertex ) verticesMap.get( theName );
           |    }
           |
           | """.stripMargin).methodDeclarations()

      methods.foreach(m => clazz.addMember(m))
    }
  }

//  class directedCommon {
//    def apply() : Seq[BodyDeclaration[_]] = {
//      Java(
//        s"""
//           |    public static final boolean isDirected = true;
//           |
//           |    public void addVertex( Vertex v ) {
//           |        vertices.add( v );
//           |    }
//           |
//           |     public IEdge addEdge( Vertex start,  Vertex end ) {
//           |        start.addAdjacent( end );
//           |        return (IEdge) start;
//           |    }
//           |
//           |    // Finds a vertex given its name in the vertices list
//           |    public  Vertex findsVertex( String theName )
//           |      {
//           |        int i=0;
//           |        Vertex theVertex;
//           |
//           |        // if we are dealing with the root
//           |        if ( theName==null )
//           |            return null;
//           |
//           |        for( i=0; i<vertices.size(); i++ ) {
//           |            theVertex = ( Vertex )vertices.get( i );
//           |            if ( theName.equals( theVertex.name ) )
//           |                return theVertex;
//           |        }
//           |        return null;
//           |    }
//           |
//           |""".stripMargin).classBodyDeclarations()
//    }
//
//    val semanticType: Type ='directed //graphLogic(graphLogic.base, graphLogic.prim)//
//  }

//  class graphChained1(t1:Type) {
//    def apply(bd1:Seq[BodyDeclaration[_]]): Seq[BodyDeclaration[_]] =
//      bd1
//
//    val semanticType:Type = t1 =>:
//      graphLogic(graphLogic.extensions)
//  }
//
//  class graphChained2(t1:Type,t2:Type) {
//    def apply(bd1:Seq[BodyDeclaration[_]],bd2:Seq[BodyDeclaration[_]]): Seq[BodyDeclaration[_]] =
//      bd1 ++ bd2
//
//    val semanticType:Type = t1 =>: t2 =>:
//      graphLogic(graphLogic.extensions)
//  }
//
//  class graphChained3(t1:Type,t2:Type,t3:Type) {
//    def apply(bd1:Seq[BodyDeclaration[_]],bd2:Seq[BodyDeclaration[_]],bd3:Seq[BodyDeclaration[_]]): Seq[BodyDeclaration[_]] =
//      bd1 ++ bd2 ++ bd3
//
//    val semanticType:Type = t1 =>: t2 =>:t3 =>:
//      graphLogic(graphLogic.extensions)
//  }

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
           |    public IEdge addEdge(Vertex start,  Vertex end) {
           |        Edge theEdge = new  Edge();
           |        theEdge.EdgeConstructor( start, end );
           |        edges.add( theEdge );
           |        start.addNeighbor( new  Neighbor( end, theEdge ) );
           |        end.addNeighbor( new  Neighbor( start, theEdge ) );
           |
           |        return theEdge;
           |    }
           |
           |    public Iterator<Edge> getEdges() { return edges.iterator(); }
           |
           |    // Finds an Edge given both of its vertices
           |    public  Edge findsEdge( Vertex theSource, Vertex theTarget )
           |       {
           |        for( Iterator<Edge> edgeiter = theSource.getEdges(); edgeiter.hasNext(); )
           |         {
           |            Edge theEdge = edgeiter.next();
           |            if ( ( theEdge.getStart().name.equals( theSource.name ) &&
           |                  theEdge.getEnd().name.equals( theTarget.name ) ) ||
           |                 ( theEdge.getStart().name.equals( theTarget.name ) &&
           |                  theEdge.getEnd().name.equals( theSource.name ) ) )
           |                return theEdge;
           |        }
           |        return null;
           |    }
           |
           |    public void display() {
           |        System.out.println( "******************************************" );
           |        System.out.println( "Vertices " );
           |        for ( Iterator<Vertex> vxiter = getVertices(); vxiter.hasNext() ; )
           |            vxiter.next().display();
           |
           |        System.out.println( "******************************************" );
           |        System.out.println( "Edges " );
           |        for ( Iterator<Edge> edgeiter = getEdges(); edgeiter.hasNext(); )
           |            edgeiter.next().display();
           |
           |        System.out.println( "******************************************" );
           |
           |}""".stripMargin).classBodyDeclarations()
    }

    val semanticType: Type = 'undirectedGenR//'undirectedGenR
  }

  class ChainedHere(inner:Type, cons: Type*) {
    def apply(bd:Seq[BodyDeclaration[_]]*) : Seq[BodyDeclaration[_]] =
      bd.foldRight(Seq.empty.asInstanceOf[Seq[BodyDeclaration[_]]])(_ ++ _)

    val semanticType:Type = cons.foldRight(inner)((a,b) => Arrow(a,b))
  }

}
