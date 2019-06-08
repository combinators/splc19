package gpl.productline

import com.github.javaparser.ast.CompilationUnit
import gpl.domain._
import org.combinators.cls.interpreter.combinator
import org.combinators.cls.types.Type
import org.combinators.cls.types.syntax._
import org.combinators.templating.twirl.Java

trait DriverDomain extends SemanticTypes {

  val graph:Graph
//Cycle
  @combinator object driverBase {
    def apply(gen: StructureGenerator): CompilationUnit = {

      Java(s"""
           |package main;
           |import java.util.*;
           |import gpl.*;
           |
           |public class Main  {
           |  public static void main (String[] args) {
           |     Graph g = new Graph();
           |     Vertex va = new Vertex("a");
           |     Vertex vb = new Vertex("b");
           |     Vertex vc = new Vertex("c");
           |     g.addVertex(va);
           |     g.addVertex(vb);
           |     g.addVertex(vc);
           |
           |     g.addEdge(va, vb, 4);
           |     g.addEdge(vb, vc, 7);
           |     g.addEdge(vc, va, 11);
           |
           |     // issue request
           |     System.out.println ("Cycles exist: " + g.CycleCheck());
           |  }
           |}""".stripMargin).compilationUnit
    }

    val semanticType: Type = 'StructureGenerator =>: driverLogic(driverLogic.cycle)
  }
//Connected
  @combinator object driverCompBase {
    def apply(gen: StructureGenerator): CompilationUnit = {

      Java(s"""
              |package main;
              |import java.util.*;
              |import gpl.*;
              |
              |public class Main  {
              |  public static void main (String[] args) {
              |     Graph g = new Graph();
              |     Vertex va = new Vertex("a");
              |     Vertex vb = new Vertex("b");
              |     Vertex vc = new Vertex("c");
              |     Vertex vd = new Vertex("d");
              |     Vertex ve = new Vertex("e");
              |
              |     g.addVertex(va);
              |     g.addVertex(vb);
              |     g.addVertex(vc);
              |     g.addVertex(vd);
              |     g.addVertex(ve);
              |
              |     g.addEdge(va, vb, 4);
              |     g.addEdge(vb, vc, 7);
              |     g.addEdge(vc, va, 11);
              |     // g.addEdge(vd,ve,5);
              |
              |
              |     // issue request, the biggest number printed out is the number of components.
              |     //Vertexes with the same component# are in the same component.
              |     g.ConnectedComponents();
              |        Iterator<Vertex> vxiter= g.getVertices();
              |        while(vxiter.hasNext())
              |        {
              |        	Vertex v= vxiter.next();
              |        	System.out.println("Name:"+v.name+" "+"Number:"+v.componentNumber);
              |        }
              |  }
              |}""".stripMargin).compilationUnit
    }

    val semanticType: Type = 'StructureGenerator =>: driverLogic(driverLogic.connected)
  }

  //StronglyConnected
  @combinator object driverStrongBase {
    def apply(gen: StructureGenerator): CompilationUnit = {

      Java(s"""
              |package main;
              |import java.util.*;
              |import gpl.*;
              |
              |public class Main  {
              |  public static void main (String[] args) {
              |     Graph g = new Graph();
              |     Vertex va = new Vertex("a");
              |     Vertex vb = new Vertex("b");
              |     Vertex vc = new Vertex("c");
              |     Vertex vd = new Vertex("d");
              |     Vertex ve = new Vertex("e");
              |
              |     g.addVertex(va);
              |     g.addVertex(vb);
              |     g.addVertex(vc);
              |     g.addVertex(vd);
              |     g.addVertex(ve);
              |
              |     //Weight doesn't matter here
              |     g.addEdge(va, vb);
              |     g.addEdge(vb, vc);
              |     g.addEdge(vc, va);
              |     // g.addEdge(vd,ve,5);
              |
              |
              |     // issue request
              |     g.display();
              |     g.StrongComponents().display();
              |  }
              |}""".stripMargin).compilationUnit
    }

    val semanticType: Type = 'StructureGenerator =>: driverLogic(driverLogic.stronglyC)
  }
}
