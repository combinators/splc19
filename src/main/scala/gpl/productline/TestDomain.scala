package gpl.productline

import com.github.javaparser.ast.CompilationUnit
import gpl.domain._
import org.combinators.cls.interpreter.combinator
import org.combinators.cls.types.Type
import org.combinators.cls.types.syntax._
import org.combinators.templating.twirl.Java

//Junit test cases
trait TestDomain extends SemanticTypes {

  val graph:Graph
//Cycle
//  @combinator object testBase {
//    def apply(gen: StructureGenerator): CompilationUnit = {
//
//      Java(s"""
//           |package gpl;
//           |
//           |import static org.junit.jupiter.api.Assertions.*;
//           |
//           |
//           |import org.junit.jupiter.api.Test;
//           |
//           |public class Main  {
//           |  public static void main (String[] args) {
//           |     Graph g = new Graph(≥);
//           |     Vertex va = new Vertex("a");
//           |     Vertex vb = new Vertex("b");
//           |     Vertex vc = new Vertex("c");
//           |     g.addVertex(va);
//           |     g.addVertex(vb);
//           |     g.addVertex(vc);
//           |
//           |     g.addEdge(va, vb, 4);
//           |     g.addEdge(vb, vc, 7);
//           |     g.addEdge(vc, va, 11);
//           |
//           |     // issue request
//           |     System.out.println ("Cycles exist: " + g.CycleCheck());
//           |  }
//           |}""".stripMargin).compilationUnit
//    }
//
//    val semanticType: Type = 'StructureGenerator =>: driverLogic(driverLogic.cycle)
//  }

  //Cycle
  @combinator object testCycleBase {
    def apply(gen: StructureGenerator): CompilationUnit = {

      Java(s"""
              |package gpl;
              |
              |import static org.junit.jupiter.api.Assertions.*;
              |
              |
              |import org.junit.jupiter.api.Test;
              |
              |class cycleTest {
              |
              |
              |
              |	@Test
              |	void testCycleCheck()  {
              |		Graph g = new Graph();
              |        Vertex va = new Vertex("a");
              |        Vertex vb = new Vertex("b");
              |        Vertex vc = new Vertex("c");
              |        g.addVertex(va);
              |        g.addVertex(vb);
              |        g.addVertex(vc);
              |        g.addEdge(va, vb, 4);
              |        g.addEdge(vb, vc, 7);
              |        g.addEdge(vc, va, 11);
              |
              |        org.junit.Assert.assertTrue(g.CycleCheck());
              |
              |
              |
              |
              |
              |
              |	}
              |
              |}
              |
              |""".stripMargin).compilationUnit
    }

    val semanticType: Type = 'StructureGenerator =>: testLogic(testLogic.cycle)
  }
//Connected
  @combinator object testCompBase {
    def apply(gen: StructureGenerator): CompilationUnit = {

      Java(s"""
              |package gpl;
              |
              |import static org.junit.jupiter.api.Assertions.*;
              |
              |
              |import org.junit.jupiter.api.Test;
              |
              |class gplTest {
              |
              |
              |
              |	@Test
              |	void testConnectedComponents() {
              |		Graph g = new Graph();
              |        Vertex va = new Vertex("a");
              |        Vertex vb = new Vertex("b");
              |        Vertex vc = new Vertex("c");
              |        Vertex vd = new Vertex("d");
              |        Vertex ve = new Vertex("e");
              |        g.addVertex(va);
              |        g.addVertex(vb);
              |        g.addVertex(vc);
              |        g.addVertex(vd);
              |        g.addVertex(ve);
              |        g.addEdge(va, vb, 4);
              |        g.addEdge(vb, vc, 7);
              |        g.addEdge(vc, va, 11);
              |        g.ConnectedComponents();
              |        org.junit.Assert.assertEquals(va.componentNumber,1);
              |        org.junit.Assert.assertEquals(vb.componentNumber,1);
              |        org.junit.Assert.assertEquals(vc.componentNumber,1);
              |        org.junit.Assert.assertEquals(vd.componentNumber,2);
              |        org.junit.Assert.assertEquals(ve.componentNumber,3);
              |
              |
              |
              |
              |	//	fail("Not yet implemented");
              |	}
              |
              |}
              |
              |""".stripMargin).compilationUnit
    }

    val semanticType: Type = 'StructureGenerator =>: testLogic(testLogic.connected)
  }

  //StronglyC
  @combinator object testStrongBase {
    def apply(gen: StructureGenerator): CompilationUnit = {

      Java(s"""
              |package gpl;
              |
              |import static org.junit.jupiter.api.Assertions.*;
              |
              |
              |import org.junit.jupiter.api.Test;
              |
              |class strongTest {
              |
              |
              |	@Test
              |	void testStrongComponents() {
              |		Graph g = new Graph();
              |        Vertex va = new Vertex("a");
              |        Vertex vb = new Vertex("b");
              |        Vertex vc = new Vertex("c");
              |        Vertex vd = new Vertex("d");
              |        Vertex ve = new Vertex("e");
              |        g.addVertex(va);
              |        g.addVertex(vb);
              |        g.addVertex(vc);
              |        g.addVertex(vd);
              |        g.addVertex(ve);
              |        // Weight doesn't matter here
              |        g.addEdge(va, vb);
              |        g.addEdge(vb, vc);
              |        g.addEdge(vc, va);
              |        // g.addEdge(vd,ve,5);
              |        // issue request
              |        Graph g2= g.StrongComponents();
              |
              |        org.junit.Assert.assertEquals(g2.getVertex(0).strongComponentNumber, 1);
              |
              |        org.junit.Assert.assertEquals(g2.getVertex(1).strongComponentNumber, 2);
              |
              |        org.junit.Assert.assertEquals(g2.getVertex(2).strongComponentNumber, 3);
              |
              |        org.junit.Assert.assertEquals(g2.getVertex(3).strongComponentNumber, 3);
              |
              |        org.junit.Assert.assertEquals(g2.getVertex(4).strongComponentNumber, 3);
              |
              |	}
              |
              |}
              |
              |
              |""".stripMargin).compilationUnit
    }

    val semanticType: Type = 'StructureGenerator =>: testLogic(testLogic.stronglyC)
  }


}
