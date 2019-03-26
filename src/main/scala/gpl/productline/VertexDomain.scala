package gpl.productline

import com.github.javaparser.ast.{CompilationUnit, Modifier}
import com.github.javaparser.ast.expr.Expression
import com.github.javaparser.ast.stmt.Statement
import gpl.domain._
import org.combinators.cls.interpreter.combinator
import org.combinators.cls.types.Type
import org.combinators.templating.twirl.Java
import org.combinators.cls.types.syntax._


trait VertexDomain extends SemanticTypes {

  val graph:Graph

  // TODO: Find Java AST for newly instantiated interface implementations
  def getComparatorBegin(tpe:String, v:String) : String = {
    s"""|new Comparator<$tpe>() {
        |  public int compare( $tpe ${v}1, $tpe ${v}2 ) {""".stripMargin
  }
  def getComparatorEnd(tpe:String, v1:String) : String = "} }"

  def getVertices(v:String, bs:Seq[Statement]) : Seq[Statement] = {
    Java(
      s"""
         |for (Iterator<Vertex> it$v = getVertices(); it$v.hasNext();) {
         |  Vertex $v = it$v.next();
         |  ${bs.mkString("\n")}
         |}""".stripMargin).statements
  }

  def getEdgesOf(en:String, u:String, bs:Seq[Statement]) : Seq[Statement] = {
    Java(
      s"""
         |for (Iterator<Edge> it$en = getEdges($u.); it$en.hasNext();) {
         |  Edge en = it$en.next();
         |  ${bs.mkString("\n")}
         |}""".stripMargin).statements
  }

  // based on model
  def adjacentVertices(v:String, bs:Seq[Statement]) : Seq[Statement] = {
    graph.storage match {
      case NeighboringNodes() =>
        Java(
          s"""
             |for (Iterator<Vertex> it$v = neighbors.iterator(); it$v.hasNext(); ) {
             |  Vertex $v = it$v.next();
             |  ${bs.mkString("\n")}
             |}
           """.stripMargin).statements

      case _ => Seq.empty
    }
  }

  /**
    * Knows how to return traversal code for the fundamental needs of any graph structure.
    *
    * getVertices(v,bs) = return all Vertices in a graph, using variable 'v' passed in, and
    *   presumably used within incoming bs.
    *
    * getEdgesOf(en,u,bs) = return all edges (u,x) in graph and return as edge variable 'en'
    *   passed in, and presumably used within incoming bs. This method is invoked within
    *   a Graph and since we can't rely on a vertex to store the edges (i.e., it might only
    *   store neighbors) there is an understanding that the implementation might construct
    *   edges anew
    *
    * adjacentVertices(v,bs) = return all neighbor vertices for the 'current' context, to
    *   be included within the Vertex class, using variable 'v' passed in, and presumably
    *   used within incoming bs.
    */
  class StructureGenerator {
    def getVerticesBegin(v:String) : String = {
        s"""
           |for ( Iterator<Vertex> it$v = getVertices(); it$v.hasNext();) {
           |  Vertex $v = it$v.next();
           |""".stripMargin
    }
    def getVerticesEnd(v:String) : String = "}"

    def getEdgesOfBegin(en:String, u:String) : String = {
        s"""
           |for ( Iterator<Edge> it$en = getEdges($u); it$en.hasNext();) {
           |  Edge $en = it$en.next();
           """.stripMargin
    }
    def getEdgesOfEnd(v:String) : String = "}"

    // Just the starting point (no closing brace)
    def adjacentVertices(v:String) : String = {
      s"""
         |for (Iterator<Vertex> it$v = neighbors.iterator(); it$v.hasNext(); ) {
         |  Vertex $v = it$v.next();
       """.stripMargin
    }

    def adjacentVerticesEnd(v:String) : String = "}"
  }


//  @combinator object StandardGenerator {
//    def apply(): StructureGenerator = new StructureGenerator()
//
//    val semanticType: Type = 'Generator
//  }

  @combinator object vertexBase {
    def apply(): CompilationUnit = {

      val gen = new StructureGenerator()

      Java(s"""
           |package gpl;
           |import java.util.*;
           |
           |public class Vertex  {
           |  public final String name;
           |
           |  public Vertex (String n) {
           |     this.name = n;
           |  }
           |
           |  public void display( ) {
           |   System.out.print( " Node " + name);
           |   ${gen.adjacentVertices("v")}
           |     System.out.print(v.name + ", ");
           |   ${gen.adjacentVerticesEnd("v")}
           |
           |   System.out.println( );
           |  }
           |
           |}""".stripMargin).compilationUnit
    }

    val semanticType: Type = vertexLogic(vertexLogic.base)
  }

  /**
    * Searching relies on common capability to record whether a vertex has been visited. A new
    * field is added, together with a new method "init_vertex" which is to be invoked (via dispatch)
    * when a workspace requests to init_vertex(this) on all vertices.
    *
    * Modified incoming class (vertexUnit)
    */
  class VertexNeighborList(incoming:Type, outgoing:Type) extends UnitModifier(incoming, outgoing) {
    override def modify(vertexUnit: CompilationUnit): Unit = {

      val clazz = vertexUnit.getType(0)

      // add field(s)
      Java("LinkedList<Vertex> neighbors = new LinkedList<>();").fieldDeclarations()
        .foreach(f => clazz.addMember(f))

      // Add init_vertex method
      Java("public Iterator<Vertex> getNeighbors( ) { return neighbors.iterator(); }").methodDeclarations()
        .foreach(m => clazz.addMember(m))
    }
  }

  /**
    * Searching relies on common capability to record whether a vertex has been visited. A new
    * field is added, together with a new method "init_vertex" which is to be invoked (via dispatch)
    * when a workspace requests to init_vertex(this) on all vertices.
    *
    * Modified incoming class (vertexUnit)
    */
  class SearchVertex(incoming:Type, outgoing:Type) extends UnitModifier(incoming, outgoing) {
    override def modify(vertexUnit: CompilationUnit): Unit = {
      val clazz = vertexUnit.getType(0)

      // add field
      Java("public boolean visited = false;").fieldDeclarations()
        .foreach(f => clazz.addMember(f))

      // Add init_vertex method
     Java(
        s"""
           |public void init_vertex( WorkSpace w ) {
           |  visited = false;
           |  w.init_vertex(this);
           |}
         """.stripMargin).methodDeclarations()
        .foreach(m => clazz.addMember(m))

      val dispStmts = Java(
        s"""
           |if (visited) {
           |  System.out.print("  visited");
           |} else {
           |  System.out.print(" !visited");
           |}""".stripMargin).statements

      val dispMethods = clazz.getMethodsBySignature("display")
      dispMethods.forEach (method => JavaCode.prependStatements(method, dispStmts))

    }
  }

  class PredKey(incoming:Type, outgoing:Type) extends UnitModifier(incoming, outgoing) {
    override def modify(vertexUnit: CompilationUnit): Unit = {
      val clazz = vertexUnit.getType(0)

      // add field(s)
      Java(
        """
          |public String pred;   // name of previous vertex
          |public int key;
        """.stripMargin).fieldDeclarations()
        .foreach(f => clazz.addMember(f))
    }
  }

  class DFSVertex(incoming:Type, outgoing:Type) extends UnitModifier(incoming, outgoing) {
    override def modify(vertexUnit: CompilationUnit): Unit = {

      val clazz = vertexUnit.getType(0)
      val methods = Java(
        s"""
           | public void nodeSearch( WorkSpace w ) {
           |    Vertex v;
           |
           |    // Step 1: Do preVisitAction.
           |    //            If we've already visited this node return
           |    w.preVisitAction( ( Vertex ) this );
           |
           |    if (visited) return;
           |
           |    // Step 2: else remember that we've visited and
           |    //         visit all neighbors
           |    visited = true;
           |
           |    for (Iterator<Vertex> vxiter = getNeighbors(); vxiter.hasNext(); ) {
           |        v = vxiter.next();
           |        w.checkNeighborAction(this, v);
           |        v.nodeSearch(w);
           |    }
           |
              |    // Step 3: do postVisitAction now
           |    w.postVisitAction (this);
           |} // of dftNodeSearch""".stripMargin).methodDeclarations()

      methods.foreach(m => clazz.addMember(m))
    }
  }

  /**
    * Add to existing declarations.
    *
    * Modified incoming class (vertexUnit) and adds componentNumber
    */
  class ConnectedVertex(incoming:Type, outgoing:Type) extends UnitModifier(incoming, outgoing) {
    override def modify(vertexUnit: CompilationUnit): Unit = {

      val clazz = vertexUnit.getType(0)

      // add field
      clazz.addFieldWithInitializer(Java("int").tpe(), "componentNumber", Java("0").expression[Expression](), Modifier.PUBLIC)

      val dispStmts = Java("""System.out.print( " comp# "+ componentNumber + " " );""").statements

      val dispMethods = clazz.getMethodsBySignature("display")
      dispMethods.forEach(method => JavaCode.prependStatements(method, dispStmts))
    }
  }

  class StronglyCVertex(incoming:Type, outgoing:Type) extends UnitModifier(incoming, outgoing) {
    override def modify(vertexUnit: CompilationUnit): Unit = {

      val clazz = vertexUnit.getType(0)

      // add field
      clazz.addFieldWithInitializer(Java("int").tpe(), "finishTime", Java("0").expression[Expression](), Modifier.PUBLIC)
      clazz.addFieldWithInitializer(Java("int").tpe(), "strongComponentNumber", Java("0").expression[Expression](), Modifier.PUBLIC)

      val dispStmts = Java("""System.out.print( " FinishTime -> " + finishTime + " SCCNo -> " + strongComponentNumber );""").statements

      val dispMethods = clazz.getMethodsBySignature("display")
      dispMethods.forEach(method => JavaCode.prependStatements(method, dispStmts))
    }
  }

  class NumVertex(incoming:Type, outgoing:Type) extends UnitModifier(incoming, outgoing) {
    override def modify(vertexUnit: CompilationUnit): Unit = {
      val clazz = vertexUnit.getType(0)

      // add field
      clazz.addFieldWithInitializer(Java("int").tpe(), "VertexNumber", Java("0").expression[Expression](), Modifier.PUBLIC)

      val dispStmts = Java(s"""System.out.print( " # "+ VertexNumber + " " );""").statements

      val dispMethods = clazz.getMethodsBySignature("display")
      dispMethods.forEach (method => JavaCode.prependStatements(method, dispStmts))

    }
  }

}
