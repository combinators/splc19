package gpl.productline

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.body.BodyDeclaration
import com.github.javaparser.ast.expr.SimpleName
import gpl.domain.{Graph, GraphDomain, SemanticTypes}
import org.combinators.cls.interpreter.combinator
import org.combinators.cls.types.Type
import org.combinators.cls.types.syntax._
import org.combinators.templating.twirl.Java
import java.util.Iterator
import java.util.LinkedList


class GPLDomain(override val graph:Graph) extends GraphDomain(graph) with SemanticTypes {



    @combinator object GraphBase {
    def apply(extensions:Seq[BodyDeclaration[_]]): CompilationUnit = Java(
      s"""
         |package gpl;
         |
         |import java.util.Iterator;
         |import java.util.LinkedList;
         |
         |public class Graph {
         |   LinkedList vertices;
         |   Graph() {
         |     vertices = new LinkedList();
         |   }
         |
         |   public Iterator<Vertex> getVertices( ) { return vertices.iterator(); }
         |
         |   public void sortVertices(Comparator c) {
         |      Collections.sort(vertices, c);
         |   }
         |
         |   ${extensions.mkString("\n")}
         |}
       """.stripMargin).compilationUnit

    val semanticType: Type = graphSemantics(graphSemantics.extensions) =>:
      graphSemantics(graphSemantics.base)
  }


  /**
    * Edge Information as helper method for edge.
    */
  @combinator object edgeInterface {
    def apply(extensions: Seq[BodyDeclaration[_]]): CompilationUnit = {
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
           |   ${extensions.mkString("\n")}
           |}
         """.stripMargin).compilationUnit
    }

    val semanticType: Type = edgeIfcSemantics(edgeIfcSemantics.extensions) =>:
      edgeIfcLogic(edgeIfcLogic.base,edgeIfcLogic.complete )

  }


  //shell only contains prog and base

  // now try to do Prog, it uses base and Prog(need to extend Graph)
  // use class to update
  @combinator object GraphProgExtensions {
    def apply(): Seq[BodyDeclaration[_]] = {
      Java(
        s"""
           |
           |public void run( Vertex v ) { }
           |
         """.stripMargin).classBodyDeclarations
    }

    val semanticType: Type = graphProgSemantics(graphProgSemantics.extensions)
  }

  //BFS

  @combinator object vertexBFSExtensions{
    def apply(): Seq[BodyDeclaration[_]] = {
      Java(
        s"""
           |public void nodeSearch( WorkSpace w )
           |    {
           |        int     s, c;
           |        Vertex  v;
           |        Vertex  header;
           |
           |        // Step 1: if preVisitAction is true or if we've already
           |        //         visited this node
           |        w.preVisitAction( ( Vertex ) this );
           |
           |        if ( visited )
           |        {
           |            return;
           |        }
           |
           |        // Step 2: Mark as visited, put the unvisited neighbors in the queue
           |        //     and make the recursive call on the first element of the queue
           |        //     if there is such if not you are done
           |        visited = true;
           |
           |        // Step 3: do postVisitAction now, you are no longer going through the
           |        // node again, mark it as black
           |        w.postVisitAction( ( Vertex ) this );
           |
           |        // enqueues the vertices not visited
           |        for ( Iterator<Vertex> vxiter = getNeighbors( ); vxiter.hasNext( ); )
           |        {
           |            v = vxiter.next( );
           |
           |            // if your neighbor has not been visited then enqueue
           |            if ( !v.visited )
           |            {
           |                GlobalVarsWrapper.Queue.add( v );
           |            }
           |
           |        } // end of for
           |
           |        // while there is something in the queue
           |        while( GlobalVarsWrapper.Queue.size( )!= 0 )
           |        {
           |            header = ( Vertex ) GlobalVarsWrapper.Queue.get( 0 );
           |            GlobalVarsWrapper.Queue.remove( 0 );
           |            header.nodeSearch( w );
           |        }
           |    } // of bfsNodeSearch
         """.stripMargin).classBodyDeclarations
    }

    val semanticType: Type = vertexNodeSearchSemantics(vertexNodeSearchSemantics.extensions) :&: vertexNodeSearchSemantics.bfs
  }

  //DFS

  @combinator object vertexDFSExtensions{
    def apply(): Seq[BodyDeclaration[_]] = {
      Java(
        s"""
           |public void nodeSearch( WorkSpace w )
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
         """.stripMargin).classBodyDeclarations
    }

    val semanticType: Type = vertexNodeSearchSemantics(vertexNodeSearchSemantics.extensions) :&: vertexNodeSearchSemantics.dfs
  }

  // Benchmark
  @combinator object GraphBMExtensions {
    def apply(): Seq[BodyDeclaration[_]] = {
      Java(
        s"""
           |public Reader inFile; // File handler for reading
           |    public static int ch; // Character to read/write
           |
           |    // timings
           |    static long last = 0, current = 0, accum = 0;
           |
           |    public void runBenchmark( String FileName ) throws IOException
           |    {
           |        try
           |        {
           |            inFile = new FileReader( FileName );
           |        }
           |        catch ( IOException e )
           |        {
           |            System.out.println( "Your file " + FileName + " cannot be read" );
           |        }
           |    }
           |
           |    public void stopBenchmark( ) throws IOException
           |    {
           |        inFile.close( );
           |    }
           |
           |    public int readNumber( ) throws IOException
           |    {
           |        int index = 0;
           |        char[ ] word = new char[ 80 ];
           |        int ch = 0;
           |
           |        ch = inFile.read( );
           |        while( ch==32 )
           |        {
           |            ch = inFile.read( ); // skips extra whitespaces
           |        }
           |
           |        while( ch != -1 && ch != 32 && ch != 10 ) // while it is not EOF, WS, NL
           |        {
           |            word[ index++ ] = ( char )ch;
           |            ch = inFile.read( );
           |        }
           |        word[ index ] = 0;
           |
           |        String theString = new String( word );
           |
           |        theString = new String( theString.substring( 0,index ) ).trim( );
           |        return Integer.parseInt( theString,10 );
           |    }
           |
           |    public static void startProfile( )
           |    {
           |        accum = 0;
           |        current = System.currentTimeMillis( );
           |        last = current;
           |    }
           |
           |    public static void stopProfile( )
           |    {
           |        current = System.currentTimeMillis( );
           |        accum = accum + ( current - last );
           |    }
           |
           |    public static void resumeProfile( )
           |    {
           |        current = System.currentTimeMillis( );
           |        last = current;
           |    }
           |
           |    public static void endProfile( )
           |     {
           |        current = System.currentTimeMillis( );
           |        accum = accum + ( current-last );
           |        System.out.println( "Time elapsed: " + accum + " milliseconds" );
           |    }
         """.stripMargin).classBodyDeclarations
    }

    val semanticType: Type = graphBMSemantics(graphBMSemantics.extensions)
  }

  @combinator object VertexGraphDrExtensions {
    def apply(): Seq[BodyDeclaration[_]] = {
      Java(
        s"""
           | public LinkedList adjacentVertices = new LinkedList();
           |
           |    public void addAdjacent( Vertex n ) {
           |        adjacentVertices.add( n );
           |    }
           |
           |    public Iterator<Vertex> getNeighbors( ) { return adjacentVertices.iterator( ); }
           |
           |    public void display() {
           |        int s = adjacentVertices.size();
           |        int i;
           |
           |        System.out.print( "Vertex " + name + " connected to: " );
           |
           |        for ( i=0; i<s; i++ )
           |            System.out.print( ( ( Vertex )adjacentVertices.get( i ) ).name+", " );
           |        System.out.println();
           |    }
           |
           |//--------------------
           |// from IEdge
           |//--------------------
           |
           |    public Vertex getStart( ) { return null; }
           |    public Vertex getEnd( ) { return null; }
           |
           |    public void setWeight( int weight ){}
           |    public int getWeight() { return 0; }
           |
           |    public Vertex getOtherVertex( Vertex vertex )
           |    {
           |        return this;
           |    }
           |
           |
         """.stripMargin).classBodyDeclarations
    }

    val semanticType:Type = vertexDirectedGRSemantics(vertexDirectedGRSemantics.extensions)
  }


  @combinator object GraphDrExtensions {
    def apply(): Seq[BodyDeclaration[_]] = {
      Java(
        s"""
           | // Adds and edge by setting end as adjacent to start vertices
           |    public IEdge addEdge( Vertex start,  Vertex end ) {
           |        start.addAdjacent( end );
           |        return( IEdge ) start;
           |    }
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
           |        for( i=0; i<vertices.size(); i++ )
           |            {
           |            theVertex = ( Vertex )vertices.get( i );
           |            if ( theName.equals( theVertex.name ) )
           |                return theVertex;
           |        }
           |        return null;
           |    }
           |
           |    public void display() {
           |        int s = vertices.size();
           |        int i;
           |
           |        System.out.println( "******************************************" );
           |        System.out.println( "Vertices " );
           |        for ( i=0; i<s; i++ )
           |            ( ( Vertex ) vertices.get( i ) ).display();
           |        System.out.println( "******************************************" );
           |
           |    }
         """.stripMargin).classBodyDeclarations
    }

    val semanticType: Type = graphDGRSemantics(graphDGRSemantics.extensions)
  }

  // this defines INTERFACE but it must be a class
//  @combinator object workSpace {
//    def apply(extensions:Seq[BodyDeclaration[_]]): CompilationUnit = Java(
//      s"""
//         |package gpl;
//         |
//         |public interface WorkSpace {
//         |    public void init_vertex( Vertex v ) {}
//         |    public void preVisitAction( Vertex v ) {}
//         |    public void postVisitAction( Vertex v ) {}
//         |    public void nextRegionAction( Vertex v ) {}
//         |    public void checkNeighborAction( Vertex vsource, Vertex vtarget ) {}
//         |   ${extensions.mkString("\n")}
//         |}
//       """.stripMargin).compilationUnit
//
//    val semanticType: Type = workSpaceSemantics(workSpaceSemantics.extensions) =>:
//      workSpaceSemantics(workSpaceSemantics.base)
//  }

  /** connected -- graph, regionworkspace and vertex */
  @combinator object GraphCNExtensions {
    def apply(): Seq[BodyDeclaration[_]] = {
      Java(
        s"""
           |    public void ConnectedComponents( )
           |    {
           |        GraphSearch( new RegionWorkSpace( ) );
           |    }
         """.stripMargin).classBodyDeclarations
    }

    val semanticType: Type = graphCNSemantics(graphCNSemantics.extensions)
  }


  @combinator object workSpaceRegionExtensions {
    def apply(): Seq[BodyDeclaration[_]] = {
      Java(
        s"""
           |    public void ConnectedComponents( )
           |    {
           |        GraphSearch( new RegionWorkSpace( ) );
           |    }
         """.stripMargin).classBodyDeclarations
    }

    val semanticType: Type = workSpaceCNSemantics(workSpaceCNSemantics.extensions)
  }

//  @combinator object AddStuff {
//    def apply(unit:CompilationUnit) : CompilationUnit = {
//      // this takes compilationUnit and modifies it to add the
//      // necessary files in return
//    }
//
//    val semanticType:Type = THETA =>: THETA :&: 'GraphSubType
//
//  }
  //sym:Symbm, newMethods:Seq[BodyDeclaration[_]]
  // ????????????????????
  class AddStuff(extensions:Seq[BodyDeclaration[_]], implements:Seq[SimpleName] = Seq.empty) {
    def apply(unit:CompilationUnit) : CompilationUnit = {
      // this takes compilationUnit and modifies it to add the
      // necessary files in return
      val implementsClause = if (implements.nonEmpty) {
        "implements " + implements.mkString(",")
      } else {
        ""
      }

      Java(s"""
              |class Tiny $implementsClause {
              |  ${extensions.mkString("\n")}
              |}
          """.stripMargin).compilationUnit
    }

    val semanticType:Type = vertexSemantics(vertexSemantics.extensions) =>: vertexCNSemantics(vertexCNSemantics.extensions)
  }



  @combinator object VertexCNExtensions {
    def apply(): Seq[BodyDeclaration[_]] = {
      Java(
        s"""
           |    public int componentNumber;
           |
           |    public void display( )
           |    {
           |        System.out.print( " comp# "+ componentNumber + " " );
           |        Super( ).display( );
           |    }
         """.stripMargin).classBodyDeclarations
    }

    val semanticType:Type = vertexCNSemantics(vertexCNSemantics.extensions)
  }

  /** connected done, may need to fix Graph, Graph is still hacking
    *
    */


  /** now let's move on to Cycle
    *
    */

  @combinator object GraphCCExtensions {
    def apply(): Seq[BodyDeclaration[_]] = {
      Java(
        s"""
           | public boolean CycleCheck() {
           |        CycleWorkSpace c = new CycleWorkSpace( isDirected );
           |        GraphSearch( c );
           |        return c.AnyCycles;
           |    }
         """.stripMargin).classBodyDeclarations
    }

    val semanticType: Type = graphCCSemantics(graphCCSemantics.extensions)
  }

  @combinator object workSpaceCycleExtensions {
    def apply(): Seq[BodyDeclaration[_]] = {
      Java(
        s"""
           |    public boolean AnyCycles;
           |    public int     counter;
           |    public boolean isDirected;
           |
           |    public static final int WHITE = 0;
           |    public static final int GRAY  = 1;
           |    public static final int BLACK = 2;
           |
           |    public CycleWorkSpace( boolean UnDir ) {
           |        AnyCycles = false;
           |        counter   = 0;
           |        isDirected = UnDir;
           |    }
           |
           |    public void init_vertex( Vertex v )
           |      {
           |        v.VertexCycle = Integer.MAX_VALUE;
           |        v.VertexColor = WHITE; // initialize to white color
           |    }
           |
           |    public void preVisitAction( Vertex v ) {
           |
           |        // This assigns the values on the way in
           |        if ( v.visited!=true )
           |        { // if it has not been visited then set the
           |            // VertexCycle accordingly
           |            v.VertexCycle = counter++;
           |            v.VertexColor = GRAY; // we make the vertex gray
           |        }
           |    }
           |
           |    public void postVisitAction( Vertex v )
           |      {
           |        v.VertexColor = BLACK; // we are done visiting so make it black
           |        counter--;
           |    } // of postVisitAction
           |
           |    public void checkNeighborAction( Vertex vsource,
           |                     Vertex vtarget )
           |      {
           |        // if the graph is directed is enough to check that the source node
           |        // is gray and the adyacent is gray also to find a cycle
           |        // if the graph is undirected we need to check that the adyacent is not
           |        // the father, if it is the father the difference in the VertexCount is
           |        // only one.
           |        if ( isDirected )
           |        {
           |            if ( ( vsource.VertexColor == GRAY ) && ( vtarget.VertexColor == GRAY ) )
           |              {
           |                AnyCycles = true;
           |            }
           |        }
           |        else
           |        { // undirected case
           |            if ( ( vsource.VertexColor == GRAY ) && ( vtarget.VertexColor == GRAY )
           |                 && vsource.VertexCycle != vtarget.VertexCycle+1 )
           |              {
           |                AnyCycles = true;
           |            }
           |        }
           |
           |    } // of checkNeighboor
         """.stripMargin).classBodyDeclarations
    }

    val semanticType: Type = workSpaceCCSemantics(workSpaceCCSemantics.extensions)
  }

  @combinator object VertexCCExtensions {
    def apply(): Seq[BodyDeclaration[_]] = {
      Java(
        s"""
           |    public int VertexCycle;
           |    public int VertexColor; // white ->0, gray ->1, black->2
           |
           |    public void display() {
           |        System.out.print( " VertexCycle# " + VertexCycle + " " );
           |        Super().display();
           |    }
         """.stripMargin).classBodyDeclarations
    }
    //val semanticType:Type = vertexCCSemantics(VertexExtension("CC").extensions);
    val semanticType:Type = vertexCCSemantics(vertexCCSemantics.extensions)
  }

  @combinator object GraphDCExtensions {
    def apply(): Seq[BodyDeclaration[_]] = {
      Java(
        s"""
           |    public static final boolean isDirected = true;
           |
           |    public void addVertex( Vertex v ) {
           |        vertices.add( v );
           |    }
           |
           |    // Adds an edge without weights if Weighted layer is not present
           |    //public void addAnEdge( Vertex start,  Vertex end, int weight ){
           |    //    addEdge( start,end );
           |    //}
         """.stripMargin).classBodyDeclarations
    }

    val semanticType: Type = graphDCSemantics(graphDCSemantics.extensions)
  }


//  // likely not to remain
//  @combinator object GraphWGRExtensions {
//    def apply(): Seq[BodyDeclaration[_]] = {
//      Java(
//        s"""
//           |    public void addEdge( Vertex start,  Vertex end, int weight ) {
//           |        addEdge( start,end ); // adds the start and end as adjacent
//           |        start.addWeight( weight ); // the direction layer takes care of that
//           |
//           |        // if the graph is undirected you have to include
//           |        // the weight of the edge coming back
//           |        if ( isDirected==false )
//           |            end.addWeight( weight );
//           |    }
//           |
//         """.stripMargin).classBodyDeclarations
//    }
//
//    val semanticType: Type = graphWGRSemantics(graphWGRSemantics.extensions)
//  }

  @combinator object VertexWGRExtensions {
    def apply(): Seq[BodyDeclaration[_]] = {
      Java(
        s"""
           |    public LinkedList weightsList = new LinkedList();
           |
           |    public void addWeight( int weight )
           |    {
           |        weightsList.add( new Integer( weight ) );
           |    }
           |
           |    public void setWeight( int weight )
           |    {
           |        addWeight( weight );
           |        Vertex v = ( Vertex ) adjacentVertices.getLast( );
           |        v .addWeight( weight );
           |    }
           |
           |    public void display()
           |    {
           |        int s = weightsList.size();
           |        int i;
           |
           |        System.out.print( " Weights : " );
           |
           |        for ( i=0; i<s; i++ ) {
           |            System.out.print( ( ( Integer )weightsList.get( i ) ).intValue() + ", " );
           |        }
           |
           |        Super().display();
           |    }
           |
         """.stripMargin).classBodyDeclarations
    }
    //val semanticType:Type = vertexCCSemantics(VertexExtension("CC").extensions);
    val semanticType:Type = vertexWGRSemantics(vertexWGRSemantics.extensions)
  }

}
